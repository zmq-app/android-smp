package com.itheima.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.smp.R;
import com.itheima.utils.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangming on 2018/9/1.
 */
public class ChatMsgFragment extends Fragment {
    private View mFragmentView;
    private RecyclerView recyclerView;
    private RecycleViewAdapter mAdapter;
    private List<String> datalist;

    private boolean isViewCreated; //Fragment的View加载完毕的标记
    private boolean isUIVisible;   //Fragment对用户可见的标记

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mFragmentView == null){
            mFragmentView = inflater.inflate(R.layout.fragment_chat_msg,container,false);
        }
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        initView();
        lasyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //TODO isVisibleToUser这个boolean值表示:该Fragment的UI用户是否可见
        if(isVisibleToUser){
            isUIVisible = true;
            lasyLoad();
        }else {
            isUIVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        isUIVisible = false;
        super.onDestroyView();
    }

    private void lasyLoad(){
        if(isViewCreated && isUIVisible) {
            Log.i(CommonConstants.TAG, "lasyLoad ChatMsgFragment...");
            initData();

            isViewCreated = false;
            isUIVisible = false;
        }
    }

    private void initView(){
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(mAdapter = new RecycleViewAdapter(getActivity(),datalist = new ArrayList<>()));
    }

    private void initData(){
        for(int i=10;i<50;i++){
            datalist.add(String.valueOf(i));
        }
        mAdapter.notifyDataSetChanged();
    }


    static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ChatViewHolder>{
        private Context mContext;
        private List<String> mList;

        public RecycleViewAdapter(Context context, List<String> datalist){
            this.mContext = context;
            this.mList = datalist;
        }

        /**
         * 创建ViewHolder的回调接口
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.chat_msg_item,parent,false);  //加载RecycleView Item条目视图view
            ChatViewHolder cViewHolder = new ChatViewHolder(view);  //将Item条目视图view传入定义的ViewHolder中
            return cViewHolder;
        }

        /**
         * 操作Item的位置接口
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(ChatViewHolder holder, int position) {
            String s = mList.get(position);
            holder.tv.setText(s);

            //对一条Item中的控件TextView进行点击事件监听
            holder.tv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"TextView is clicked...",Toast.LENGTH_SHORT).show();
                }
            });

            //对整个一条Item进行点击事件监听
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"ItemView is clicked...",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder{
            private TextView tv;

            public ChatViewHolder(View view){
                super(view);
                tv = (TextView) view.findViewById(R.id.tv_chat_msg);
            }
        }
    }
}
