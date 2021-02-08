package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.itheima.smp.R;
import com.itheima.ui.view.MyPaletteView;

/**
 * 测试画板入口Activity
 * @author zhangming
 * @date 2017/09/27/ 16:35:45
 */
public class PaletteViewActivity extends Activity implements View.OnClickListener{
    private MyPaletteView mPaletteView;
    private ImageView undoImageView,redoImageView,penImageView,eraseImageView,clearImageView;
    private boolean isAppLive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paletteview);
        
        mPaletteView = (MyPaletteView) findViewById(R.id.palette);
        undoImageView = (ImageView)findViewById(R.id.undo);
        redoImageView = (ImageView)findViewById(R.id.redo);
        penImageView = (ImageView)findViewById(R.id.pen);
        eraseImageView = (ImageView)findViewById(R.id.eraser);
        clearImageView = (ImageView)findViewById(R.id.clear);
        
        undoImageView.setOnClickListener(this);
        redoImageView.setOnClickListener(this);
        penImageView.setOnClickListener(this);
        eraseImageView.setOnClickListener(this);
        clearImageView.setOnClickListener(this);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if(mPaletteView.mMode == MyPaletteView.Mode.DRAW){
	    	//初始化默认是绘制模式,使用画笔即可
	    	penImageView.setSelected(true);
	        eraseImageView.setSelected(false);
    	}else{
    		penImageView.setSelected(false);
	        eraseImageView.setSelected(true);
    	}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	isAppLive = false;
    }
    
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	if(hasFocus && !isAppLive){
            View[] views = new View[]{undoImageView,redoImageView,penImageView,eraseImageView,clearImageView};
            mPaletteView.setViewUIHandler(views);
            isAppLive = true;
    	}
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.undo:
        	mPaletteView.undo();
            break;
        case R.id.redo:
        	mPaletteView.redo();
            break;
        case R.id.pen:
        	v.setSelected(true);
        	eraseImageView.setSelected(false);
        	mPaletteView.setMode(MyPaletteView.Mode.DRAW);
            break;
        case R.id.eraser:
        	v.setSelected(true);
        	penImageView.setSelected(false);
        	mPaletteView.setMode(MyPaletteView.Mode.ERASE);
            break;
        case R.id.clear:
            mPaletteView.clear();
            break;
		}
	}
}
