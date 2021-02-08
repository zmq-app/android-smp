package com.itheima.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.model.WeChatModel;
import com.itheima.ui.adapter.WeChatAdapter;

import java.util.ArrayList;

/**
 * @Subject RecyclerView控件模拟微信聊天的Activity
 * @Author  zhangming
 * @Date    2021-02-07 15:53
 */
public class WeChatRecyclerViewActivity extends AppCompatActivity {
    private RecyclerView rvChatLists;
    private WeChatAdapter chatAdapter;
    private EditText etChatMsg;
    private Button btnSendMsg;
    private ArrayList<WeChatModel> chatMsgLists = new ArrayList(); //聊天消息数据源

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_recycler_view);
        initView();
        initAdapter();
        setListeners();
    }

    private void initView() {
        rvChatLists = (RecyclerView) getDelegate().findViewById(R.id.rv_chat_lists);
        etChatMsg = (EditText) getDelegate().findViewById(R.id.et_chat_msg);
        btnSendMsg = (Button) getDelegate().findViewById(R.id.btn_send_msg);
    }

    private void initAdapter() {
        //给recyclerView创建布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatLists.setLayoutManager(layoutManager);
        //创建聊天适配器
        chatAdapter = new WeChatAdapter(chatMsgLists);
        rvChatLists.setAdapter(chatAdapter);
    }

    private void setListeners() {
        btnSendMsg.setOnClickListener(view -> {
            String msg = etChatMsg.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendChatMsg(msg);
                replyChatMsg(msg);
                etChatMsg.setText("");
            } else {
                Toast.makeText(WeChatRecyclerViewActivity.this, "Cant be empty！", Toast.LENGTH_SHORT).show();
            }
        });

        rvChatLists.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            /* 获取聊天列表RecyclerView控件在窗体的可视区域 */
            Rect rect = new Rect();
            rvChatLists.getWindowVisibleDisplayFrame(rect);
            /* 获取聊天列表RecyclerView控件在窗体的不可视区域,在键盘未弹起时,rvInvisibleHeight=0,键盘弹起后,rect.bottom会小于初始值,此时高度差即为软键盘覆盖的不可见区域 */
            int rvInvisibleHeight = rvChatLists.getRootView().getHeight() - rect.bottom;
            /* 不可见区域大于100px,说明软键盘弹起,此时将聊天列表RecyclerView控件滑动到最末端的Item条目 */
            if (rvInvisibleHeight > 100) {
                rvChatLists.scrollToPosition(chatMsgLists.size() - 1);
            }
        });
    }

    /**
     * 发送聊天信息
     * @param message
     */
    void sendChatMsg(String message) {
        WeChatModel chatModel = new WeChatModel(R.drawable.cpp, "张明", message, WeChatModel.SEND);
        chatMsgLists.add(chatModel);
        chatAdapter.notifyItemInserted(chatMsgLists.size() - 1);
        rvChatLists.scrollToPosition(chatMsgLists.size() - 1);
    }

    /**
     * 模拟回复消息
     * @param msg
     */
    void replyChatMsg(String msg) {
        String rMsg="";
        switch(msg){
            case "hello":
                rMsg="hello! How are you？";
                break;
            case "age":
                rMsg="22";
                break;
        }
        if(!rMsg.isEmpty()){
            recvChatMessage(rMsg);
        }
    }

    /**
     * 接收聊天信息
     * @param message
     */
    void recvChatMessage(String message) {
        WeChatModel chatModel = new WeChatModel(R.drawable.monitor_launcher, "王五", message, WeChatModel.RECEIVE);
        chatMsgLists.add(chatModel);
        chatAdapter.notifyItemInserted(chatMsgLists.size() - 1);
        rvChatLists.scrollToPosition(chatMsgLists.size() - 1);
    }

}
