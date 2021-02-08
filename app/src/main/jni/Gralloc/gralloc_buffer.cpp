#include "../include/jni_native_util.h"


jint JNICALL gralloc_framebuffer(JNIEnv* env,jobject obj)
{
#if 0
    framebuffer_device_t* fbDev;
    alloc_device_t* grDev;

    hw_module_t const* module;
    buffer_handle_t handle;
    gralloc_module_t const *mAllocMod;
    void* vaddr;
    int stride;
    int err;

    if (hw_get_module(GRALLOC_HARDWARE_MODULE_ID, &module) == 0) { //加载gralloc模块
        err = framebuffer_open(module, &fbDev); //打开fb设备
        if (err) ALOGE("couldn't open framebuffer HAL (%s)", strerror(-err));
        err = gralloc_open(module, &grDev); //打开gralloc设备
        if (err) ALOGE("couldn't open gralloc HAL (%s)", strerror(-err));

        /* GRALLOC_USAGE_HW_FB决定申请的是系统图形内存还是普通内存 */
        err = grDev->alloc(grDev, display.w, display.h, HAL_PIXEL_FORMAT_RGBA_8888,
                           GRALLOC_USAGE_HW_FB, &handle, &stride); //分配图形缓冲区
        //err = grDev->alloc(grDev, 1024, 600, HAL_PIXEL_FORMAT_RGBA_8888, 0 , &handle, &stride); //分配图形缓冲区

        mAllocMod = (gralloc_module_t const *) module;
        err = mAllocMod->registerBuffer(mAllocMod, handle); //映射内存到进程中

        err = mAllocMod->lock(mAllocMod, handle, HAL_PIXEL_FORMAT_RGBA_8888, 0, 0, display.w,
                              display.h, &vaddr);
        ALOGE("++++++++++++++++> vaddr = %p\n", vaddr);

        err = mAllocMod->lock(mAllocMod, handle, HAL_PIXEL_FORMAT_RGBA_8888, 0, 0, 1024, 600,
                              &vaddr);
        ALOGE("++++++++++++++++> vaddr = %p\n", vaddr);

        //这就绘图即可,将绘制的图的内存直接拷贝到vaddr里面即可
        bitmap.lockPixels();
        canvas->drawPath(path, paint);
        memcpy(vaddr, bitmap.getPixels(), bitmap.getSize());
        bitmap.unlockPixels();

        err = mAllocMod->unlock(mAllocMod, handle);
        err = fbDev->post(fbDev, handle); //图形缓冲区的渲染
        err = mAllocMod->unregisterBuffer(mAllocMod, handle); //解除映射内存

        grDev->free(grDev, handle);  //释放图形缓冲区
        gralloc_close(grDev);    //关闭gralloc设备
        framebuffer_close(fbDev);    //关闭fb设备
    }
#endif
    return 0;
}