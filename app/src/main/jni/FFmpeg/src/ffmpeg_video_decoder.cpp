#include <unistd.h>
#include "../../include/jni_native_util.h"

/** C++编译器会将在extern “C”的大括号内部的代码当做C语言来处理,实现C++与C兼容 **/
/** 否则编译会报错"src/main/jni/ffmpeg_video_decoder.cpp:21: error: undefined reference to 'av_register_all()'" **/
#ifdef __cplusplus
extern "C" {
#endif
#include <android/native_window_jni.h>  //ANativeWindow使用,隶属于"libandroid.so"共享库[frameworks/base/native/android/Android.bp]
#include "libavcodec/avcodec.h"   //编码处理
#include "libavformat/avformat.h" //封装格式处理
#include "libswscale/swscale.h"   //图像数据格式的转换以及图片的缩放处理
#ifdef __cplusplus
}
#endif

/**
 *                  |-->  AVInputFormat
 * AVFormatContext  |-->  AVStream[0]  -->  AVCodecContext[width,height,...]  -->  AVCodec
 *                  |-->  AVStream[1]  -->  AVCodecContext[width,height,...]  -->  AVCodec
 *
 * AVFormatContext  封装格式上下文结构体,也是统领全局的结构体,保存了视频文件封装格式相关信息
 * AVInputFormat    每种封装格式(例如:FLV,MKV,MP4,AVI)对应一个结构体
 * AVStream         视频文件中每个视频(音频)流对应一个该结构体
 *
 * AVCodecContext   编码器上下文结构体,保存了视频(音频)编解码相关信息
 * AVCodec          每种视频(音频)编解码器(例如:H.264解码器)对应一个该结构体
 *
 * AVPacket         存储一帧压缩编码数据
 * AVFrame          存储一帧解码后像素(采样)数据
 *
 * int64_t dts      AVPacket结构体的成员,解码时间戳,意义在于告诉播放器需要在什么时候解码这一帧数据[Decoding Time Stamp]
 * int64_t pts      AVFrame结构体的成员,显示时间戳,意义在于告诉播放器需要在什么时候显示这一帧的数据[Presentation Time Stamp]
 */

/** 提供给java层调用的方法 **/
jint JNICALL ffmpeg_video_render(JNIEnv* env,jobject obj, jstring inputVideoPath, jobject surface)
{
    jboolean isCopy = false;
    const char* videoPath = env->GetStringUTFChars(inputVideoPath,&isCopy);
    ALOGI("ffmpeg_video_render register success,videoPath = %s",videoPath);

    /** 注册各大组件(libavformat/avformat.h) **/
    av_register_all();

    /** 分配一个内容上下文 **/
    AVFormatContext* avFormatContext = avformat_alloc_context();
    if (avFormatContext == NULL) {
        ALOGE("ffmpeg_video_render avFormatContext is NULL...");
        return -1;
    }

    /* 问题: 在小米6手机上,读写sdcard文件系统需要动态授予权限,执行时会报错 */
    /* 错误: ffmpeg_video_render can not open file,videoPath = /sdcard/ffmpeg_movie.mp4,errorNum = -13,error_buf = Permission denied */
    /* 解决: 使用项目中的权限申请工具类PermissionUtil来执行申请运行权限,或者直接在手机权限管理中针对此应用的读写sdcard权限由询问状态更改为授予状态即可 */
    int errorNum;
    char error_buf[1024] = {0};
    if ((errorNum = avformat_open_input(&avFormatContext,videoPath,NULL,NULL)) < 0) {
        ALOGE("ffmpeg_video_render can not open file,videoPath = %s,errorNum = %d,error_buf = %s",videoPath,errorNum,error_buf);
        av_strerror(errorNum, error_buf, sizeof(error_buf));
        avformat_close_input(&avFormatContext);
        return -1;
    }

    /** 寻找AvStream类实例,包括音频流和视频流 **/
    if (avformat_find_stream_info(avFormatContext,NULL) < 0) {
        ALOGE("ffmpeg_video_render find_stream_info is failed...");
        return -1;
    }
    ALOGI("ffmpeg_video_render avFormatContext->nb_streams = %d",avFormatContext->nb_streams);  //nb_streams = 2

    /** 找到视频流,标记index索引,用于后续的视频流进行解码,转换,绘制等 **/
    int videoIndex = 0;
    for (unsigned int i=0; i<(avFormatContext->nb_streams); i++) {
        if ((avFormatContext->streams[i]->codec->codec_type) == AVMEDIA_TYPE_VIDEO) {
            videoIndex = i;
        }
    }

    /** 根据视频流AVStream实例,获取解码器上下文 **/
    AVCodecContext* avCodecContext = avFormatContext->streams[videoIndex]->codec;
    if (avCodecContext == NULL) {
        ALOGE("ffmpeg_video_render avCodecContext is NULL...");
        memset(error_buf, 0, sizeof(error_buf));
        av_strerror(errorNum, error_buf, sizeof(error_buf));
        avcodec_close(avCodecContext);
        return -1;
    }
    /** avCodecContext->pix_fmt = AV_PIX_FMT_YUV420P = 0 **/
    ALOGI("ffmpeg_video_render avCodecContext width = %d,height = %d,src_pix_fmt = %d",
          avCodecContext->width,avCodecContext->height,avCodecContext->pix_fmt);

    /** 获取解码器decoder,准备解码 **/
    AVCodec* avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    if (avCodec != NULL) {
        if (avcodec_open2(avCodecContext, avCodec, NULL) < 0) {
            ALOGE("ffmpeg_video_render avcodec_open2 is failed...");
            return -1;
        }
    }

    /** 申请AVPacket **/
    AVPacket* packet = (AVPacket*) av_malloc(sizeof(AVPacket));
    av_init_packet(packet);

    /** 申请AVFrame **/
    /** 分配一个AVFrame结构体srcDecoderFrame,此AVFrame结构体一般用于存储原始数据,指向解码后的原始帧 **/
    /** 分配一个AVFrame结构体,指向存放转换成RGBA后的数据帧 **/
    AVFrame* srcDecoderFrame = (AVFrame*)av_malloc(sizeof(AVFrame));
    AVFrame* rgbaFrame = (AVFrame*)av_malloc(sizeof(AVFrame));

    /** 申请转换后的图像数据缓存区[AVPixelFormat格式:RGBA] **/
    int bufferSize = avpicture_get_size(AV_PIX_FMT_RGBA,avCodecContext->width,avCodecContext->height);
    uint8_t* outBuffer = (uint8_t*)av_malloc(bufferSize);
    if (outBuffer == NULL) {
        ALOGE("ffmpeg_video_render av_malloc is failed...");
        return -1;
    }

    /** 与Frame缓存区相关联,预先设置rgbFrame缓存区,用于保存enqueue BufferQueue的RGBA图像数据 **/
    avpicture_fill((AVPicture*)rgbaFrame, outBuffer, AV_PIX_FMT_RGBA, avCodecContext->width, avCodecContext->height);

    /** struct SwsContext:视频图像的转换,比如:格式转换;  struct SwrContext:音频重采样,比如:采样率转换,声道转换  **/
    /** 此段视频的SwsContext上下文输入图像数据格式 = avCodecContext->pix_fmt = AV_PIX_FMT_YUV420P = 0 **/
    /** 此段视频的SwsContext上下文的输出图像数据格式 = AV_PIX_FMT_RGBA **/
    struct SwsContext* swsContext = sws_getContext(avCodecContext->width, avCodecContext->height, avCodecContext->pix_fmt,
            avCodecContext->width, avCodecContext->height, AV_PIX_FMT_RGBA, SWS_BICUBIC, NULL, NULL, NULL);
    if (swsContext == NULL) {
        ALOGE("ffmpeg_video_render sws_getContext is failed...");
        return -1;
    }

    /** 由surface转化为ANativeWindow指针实例,获取ANativeWindow **/
    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env,surface);
    if (nativeWindow == NULL) {
        ALOGE("ffmpeg_video_render nativeWindow is NULL...");
        return -1;
    }

    int frameCount;
    ANativeWindow_Buffer native_buffer;
    while(av_read_frame(avFormatContext,packet) >= 0) {
        /** 过滤音频流,仅保留视频流[VideoStream: packet->stream_index = 1,videoIndex = 1] **/
        if (packet->stream_index == videoIndex) {
            /** 如果是视频流,执行解码操作[AVPacket packet ==> AVFrame srcDecoderFrame] **/
            avcodec_decode_video2(avCodecContext,srcDecoderFrame,&frameCount,packet);
            if (frameCount > 0) {
                /** 绘制之前配置nativewindow **/
                ANativeWindow_setBuffersGeometry(nativeWindow,avCodecContext->width,avCodecContext->height,WINDOW_FORMAT_RGBA_8888);
                /** ANativeWindow锁住一块buffer缓冲区 **/
                ANativeWindow_lock(nativeWindow,&native_buffer,NULL);

                /**
                 * YUV420P格式转换为RGBA格式数据
                 * 解码后YUV格式的视频像素数据保存在AVFrame的data[0],data[1],data[2]中,但是这些像素值并不是连续存储的,
                 * 每行有效像素之后存储了一些无效像素,以亮度Y数据为例,data[0]中一共包含了linesize[0]*height个数据.
                 * 但是出于优化等方面的考虑,linesize[0]实际上并不等于宽度width,而是一个比宽度大一些的值,因此需要使用sws_scale函数进行转换.
                 * 转换后去除了无效数据,width和linesize[0]取值相等.
                 * const uint8_t *const srcSlice[]   "srcDecoderFrame->data"
                 * const int srcStride[]   "srcDecoderFrame->linesize"
                 * srcDecoderFrame->width = 960  srcDecoderFrame->linesize[0] = 960(转换前一行的字节数)  rgbaFrame->linesize[0] = 3840(转换RGB后一行的字节数)
                 */
                sws_scale(swsContext, (const uint8_t* const*)(srcDecoderFrame->data), srcDecoderFrame->linesize, 0,
                    srcDecoderFrame->height, rgbaFrame->data, rgbaFrame->linesize);

                /** rgbFrame有画面的数据 **/
                uint8_t* dst = (uint8_t*)(native_buffer.bits);
                /** 转换后RGBA格式中一行有多少个字节 **/
                int dst_strides = native_buffer.stride * 4;  //一行的像素pixels个数[native_buffer.stride]
                /** 初始像素数据的首地址 **/
                uint8_t* src = rgbaFrame->data[0];
                /** 初始内存的一行像素数量 **/
                int src_strides = rgbaFrame->linesize[0];
                /** 将每一行的解码buffer数据拷贝到对应的ANativeWindow_Buffer缓冲区中 **/
                for (int i=0; i<(avCodecContext->height); i++) {
                    memcpy(dst+i*dst_strides, src+i*src_strides, src_strides);  //src_strides = dst_strides = 3840
                }

                /** ANativeWindow解锁一块buffer缓冲区 **/
                ANativeWindow_unlockAndPost(nativeWindow);
                /** VSYNC机制,16ms交换Buffer缓冲区,同步刷新一次 **/
                usleep(16*1000);
            }
        }
        if (packet != NULL) {
            av_free_packet(packet);
        }
    }

    /** 解码播放完成以后,执行必要的释放清理工作 **/
    ANativeWindow_release(nativeWindow);
    av_frame_free(&srcDecoderFrame);
    av_frame_free(&rgbaFrame);
    sws_freeContext(swsContext);
    avcodec_close(avCodecContext);
    avformat_free_context(avFormatContext);
    /**  ReleaseStringUTFChars(jstring string, const char* utf)  **/
    env->ReleaseStringUTFChars(inputVideoPath,videoPath);

    return 0L;
}