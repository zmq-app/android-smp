package com.itheima.service;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.smp.R;

/**
 * @Subject 输入法服务ImeService
 * @Author  zhangming
 * @Date    2020-07-09 16:30
 */
public class ImeService extends InputMethodService {
    private static String TAG = ImeService.class.getSimpleName();

    /* 服务启动 */
    @Override
    public void onCreate() {
        Log.v(TAG,"ImeService onCreate...");
        super.onCreate();
    }

    /* 创建键盘区视图区域 */
    @Override
    public View onCreateInputView() {
        Log.v(TAG,"ImeService onCreateInputView...");
        View skbView = LayoutInflater.from(this).inflate(R.layout.skb_container,null);
        Button btn = (Button)skbView.findViewById(R.id.btn_commit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    char[] text = new char[] {'h','e','l','l','o'};  //text.length = 5
                    ic.commitText(new String(text),1);
                    requestHideSelf(0);
                }
            }
        });
        return skbView;
    }

    /* 创建候选框区域 */
    @Override
    public View onCreateCandidatesView() {
        Log.v(TAG,"ImeService onCreateCandidatesView...");
        View candidatesView = LayoutInflater.from(this).inflate(R.layout.candidate_container,null);
        TextView tv = (TextView) candidatesView.findViewById(R.id.candidate_view);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImeService.this,"onCreateCandidatesView...",Toast.LENGTH_SHORT).show();
            }
        });
        return candidatesView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        Log.v(TAG,"ImeService onStartInput,restarting = "+restarting);
        super.onStartInput(attribute, restarting);
    }

    /* 设置键盘区域(软键盘)样式 */
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.v(TAG,"ImeService onStartInputView,restarting = "+restarting);
        super.onStartInputView(info, restarting);
        /* 设置CandidateView视图永远可见,如果不设置,默认是不可见的 */
        setCandidatesViewShown(true);
    }

    /* 防止全屏 */
    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }
}
