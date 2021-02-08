package com.itheima.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.utils.permission.PermissionResultCallBack;
import com.itheima.utils.permission.PermissionUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangming on 2018/9/9.
 */
public class AudioActivity extends Activity {
    private static final String TAG = AudioActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private MyGridAdapter gridAdapter;
    private List<String> datalist;

    private PermissionResultCallBack permissionResultCallBack;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    //采集麦克风音频数据(录制声音),播放声音相关实例变量
    private AudioRecord mAudioRecord;
    private AudioTrack  mAudioTrack;

    //回声消除类实例
    private AcousticEchoCanceler acousticEchoCanceler;

    //AudioRecord,AudioTrack存放音频数据的缓冲区Buffer
    //类似于C语言的short指针,此时编译器并不知道数组的大小,仅当初始化时才知晓
    //注: 二维数组每一维的长度可以不等 { private static byte[][] mBytes = new byte[3][]; }
    private short[] mAudioRecordData;
    private short[] mAudioTrackData;

    //录音PCM文件
    private File mAudioPcmFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        initView();
        initData();
    }

    private void initView() {
        /** RecycleView网格布局 **/
        GridLayoutManager grid = new GridLayoutManager(this,2);
        grid.setOrientation(GridLayoutManager.VERTICAL);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 2 || position == 3) {
                    return 2; //后两项标题占满全屏的两格
                }else{
                    return 1; //前两项标题占全屏两格中的一格
                }
            }
        });

        /** RecycleView添加水平和竖直分割线 **/
        /** RecycleView设置布局管理器和数据适配器 **/
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(grid);
        recyclerView.setAdapter(gridAdapter=new MyGridAdapter(this,datalist = new ArrayList<>()));
    }

    private void initData(){
        datalist.add("开始录音");
        datalist.add("停止录音");
        datalist.add("播放录音");
        datalist.add("去除噪音");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //对于Android6.0以上的系统,需要授权,方可进行内置存储的读写,录音操作等
            initPermissions();
        }else{
            initAudio();
            initAEC();
            createAudioFile();
        }
    }

    private void initPermissions(){
        permissionResultCallBack = new PermissionResultCallBack() {
            @Override
            public void onPermissionGranted() {
                Log.i(TAG,"All permissions have been granted !");
                initAudio();
                initAEC();
                createAudioFile();
            }

            @Override
            public void onPermissionGranted(String... permissions) {

            }

            @Override
            public void onPermissionDenied(String... permissions) {
                AlertDialog dialog = new AlertDialog.Builder(AudioActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent,2);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }

            @Override
            public void onRationalShow(String... permissions) {
                Log.i(TAG,"onRationalShow");
                AlertDialog dialog = new AlertDialog.Builder(AudioActivity.this)
                        .setMessage(getResources().getString(R.string.perm_grant_hite))
                        .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtil.getInstance().request(AudioActivity.this, PERMISSIONS_STORAGE, permissionResultCallBack);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
                dialog.show();
            }
        };
        PermissionUtil.getInstance().request(AudioActivity.this, PERMISSIONS_STORAGE, permissionResultCallBack);
    }

    /** 分别创建AudioRecord实例和AudioTrack实例 **/
    private void initAudio(){
        //所有Android系统都支持的采样频率
        int sampleRateInHz = 44100;
        //单声道输入: AudioFormat.CHANNEL_IN_MONO   双声道输入: AudioFormat.CHANNEL_IN_STEREO
        //采样的比特数: AudioFormat.ENCODING_PCM_16BIT这个所有Android系统都支持
        int recordBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz,
            AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        //创建AudioRecord采集数据存储的Buffer
        mAudioRecordData = new short[recordBufferSize];
        //创建AudioRecord实例
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRateInHz,
            AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,recordBufferSize);

        //构造AudioTrack播放缓冲区的Buffer
        int trackBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz,
            AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrackData = new short[trackBufferSize];
        //创建mAudioTrack实例
        //第一个参数: 一般来说从喇叭放音选择AudioManager.STREAM_MUSIC
        //第二个参数: 双声道输出 AudioFormat.CHANNEL_OUT_MONO
        //最后一个参数: 一次性把数据传给AudioTrack or 连续不断地使用write函数传递数据
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRateInHz,AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,trackBufferSize,AudioTrack.MODE_STREAM);

        Log.i("zhangming","recordBufferSize = "+recordBufferSize+" trackBufferSize = "+trackBufferSize);
    }

    public void initAEC() {
        Log.i(TAG,">>>>>>>> initAEC...");
        if (AcousticEchoCanceler.isAvailable()) {
            Log.i(TAG, "initAEC: " +"AcousticEchoCanceler is available...");
            if ((acousticEchoCanceler == null) && (mAudioRecord != null)) {
                int audioSessionId = mAudioRecord.getAudioSessionId();
                acousticEchoCanceler = AcousticEchoCanceler.create(audioSessionId);
                Log.i(TAG, "initAEC: " + acousticEchoCanceler + "\t" + audioSessionId);
                if (acousticEchoCanceler == null) {
                    Log.e(TAG, "initAEC: AcousticEchoCanceler create fail.");
                } else {
                    acousticEchoCanceler.setEnabled(true);
                }
            }
        }
    }

    /** 创建录音文件 **/
    public void createAudioFile(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.i(TAG,"rootPath = "+rootPath);

            File dir = new File(rootPath+"/audio/");
            Log.i(TAG,"dir = "+dir.getPath());
            if(!dir.exists()){
                boolean result = dir.mkdirs();
                Log.i(TAG,"result = "+result);
            }
            mAudioPcmFile = new File(dir,System.currentTimeMillis()+".pcm");
        }
    }

    class MyGridAdapter extends RecyclerView.Adapter<MyGridAdapter.GridViewHolder>{
        private Context mContext;
        private List<String> datalist;

        public MyGridAdapter(Context context, List<String> datalist){
            this.mContext = context;
            this.datalist = datalist;
        }

        /** (1)加载XML布局view到内存中 **/
        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.audio_grid_item,parent,false);
            /** 将view视图传入GridViewHolder类中 **/
            return new GridViewHolder(view);
        }

        /** (3)view中的控件设置内容 **/
        @Override
        public void onBindViewHolder(GridViewHolder holder, final int position) {
            holder.tv.setText(datalist.get(position));
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == 0){
                        startRecord();
                    }else if(position == 1){
                        stopRecord();
                    }else if(position == 2){
                        playbackRecord();
                    }else{

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        /** (2)初始化view中的部分控件 **/
        class GridViewHolder extends RecyclerView.ViewHolder{
            private TextView tv;
            public GridViewHolder(View view){
                super(view);
                tv = (TextView) view.findViewById(R.id.tv_audio_command);
            }
        }
    }

    /** 开始录制: AudioRecord读取麦克风采集的音频数据到Buffer中,之后写入到PCM文件中 **/
    private void startRecord(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(mAudioRecord == null){
                        Log.i("zhangming","mAudioRecord is null...");
                        return;
                    }
                    mAudioRecord.startRecording();
                    DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(mAudioPcmFile)));
                    while(mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                        int rSize = mAudioRecord.read(mAudioRecordData,0,mAudioRecordData.length);
                        for(int i=0;i<rSize;i++){
                            dos.writeShort(mAudioRecordData[i]);
                        }
                        dos.flush();
                    }
                    dos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /** AudioRecord停止录制 **/
    private void stopRecord(){
        if(mAudioRecord != null && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            mAudioRecord.stop();
        }
    }

    /** AudioTrack读取PCM音频文件,开始播放 **/
    private void playbackRecord(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    mAudioTrack.play();
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(mAudioPcmFile)));
                    while(mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING &&  dis.available()> 0){
                        int index = 0;
                        while(index < mAudioTrackData.length){
                            mAudioTrackData[index] = dis.readShort();
                            index++;
                        }
                        mAudioTrack.write(mAudioTrackData,0,mAudioTrackData.length);
                    }
                }catch (IOException e){
                }finally {
                    if(dis != null) {
                        try{
                            dis.close();
                        }catch (IOException e){
                        }
                    }
                    mAudioTrack.stop();
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAudioRecord != null){
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if(mAudioTrack != null){
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }
}
