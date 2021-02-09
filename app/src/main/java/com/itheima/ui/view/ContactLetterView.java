package com.itheima.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Subject 联系人首字母自定义控件
 * @Author  zhangming
 * @Date    2021-02-08 21:36
 */
public class ContactLetterView extends LinearLayout {
    private Context mContext;
    private CharacterClickListener mListener;

    public ContactLetterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        this.setOrientation(VERTICAL);

        this.initView();
    }

    private void initView() {
        for (char i = 'A'; i <= 'Z'; i++) {
            final String character = i + "";
            TextView tv = createTextLayout(character);
            addView(tv);
        }
        addView(createTextLayout("#"));
    }

    private TextView createTextLayout(final String character) {
        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setClickable(true);
        tv.setText(character);
        tv.setOnClickListener((view) -> {
            if (mListener != null) {
                mListener.clickCharacter(character);
            }
        });
        return tv;
    }

    public void setCharacterListener(CharacterClickListener listener) {
        if (mListener == null) {
            mListener = listener;
        }
    }

    /**
     * 点击右侧首字母的监听事件
     */
    public interface CharacterClickListener {
        void clickCharacter(String character);
    }
}
