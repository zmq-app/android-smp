package com.itheima.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.itheima.smp.R;
import com.itheima.ui.adapter.ContactRecyclerViewAdapter;
import com.itheima.ui.view.ContactLetterView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Subject1 RecyclerView控件实现联系人列表(多个Item条目类型)
 * @Subject2 AppCompatActivity是自带actionbar,通过修改设置activity的theme继承"Theme.AppCompat.Light.NoActionBar",设置状态栏的背景
 * @Author   zhangming
 * @Date     2021-02-08 21:03
 */
public class ContactRecyclerActivity extends AppCompatActivity {
    private RecyclerView rvContactLists;
    private ContactLetterView contactLetterView;
    private ContactRecyclerViewAdapter contactAdapter;
    private LinearLayoutManager layoutManager;

    private ArrayList<String> mContactLists = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_recycler_view);
        initView();
        addDatas();
        setListener();
    }

    private void initView() {
        rvContactLists = getDelegate().findViewById(R.id.rv_contact_list);
        contactLetterView = getDelegate().findViewById(R.id.contact_letter_view);

        layoutManager = new LinearLayoutManager(this);
        contactAdapter = new ContactRecyclerViewAdapter(this, mContactLists);

        rvContactLists.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvContactLists.setLayoutManager(layoutManager);
        rvContactLists.setAdapter(contactAdapter);
    }

    private void addDatas() {
        final String[] contactNames = new String[] {"张三丰", "郭靖", "黄蓉", "黄老邪", "赵敏", "123", "天山童姥", "任我行", "于万亭", "陈家洛", "韦小宝", "$6", "穆人清", "陈圆圆", "郭芙", "郭襄", "穆念慈", "东方不败", "梅超风", "林平之", "林远图", "灭绝师太", "段誉", "鸠摩智"};
        mContactLists.addAll(Arrays.asList(contactNames));
        contactAdapter.handleContact();
        contactAdapter.notifyDataSetChanged();
    }

    private void setListener() {
        contactLetterView.setCharacterListener((character) -> {
            final int position = contactAdapter.getScrollPosition(character);
            if (position >= 0) {
                layoutManager.scrollToPositionWithOffset(position,0);
            }
        });
    }
}
