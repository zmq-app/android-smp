package com.itheima.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.model.ContactItemBean;
import com.itheima.smp.R;
import com.itheima.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 * @Subject 联系人Contact RecyclerView的适配器adapter(多个Item条目类型)
 * @Author  zhangming
 * @Date    2021-02-08 21:36
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;

    private ArrayList<String> mContactNameLists;   //保存传递联系人姓名list集合
    private ArrayList<String> mContactPinyinLists; //联系人姓名拼音全称list集合
    private ArrayList<String> mHeadCharacterLists; //联系人姓名拼音首字母list集合
    private ArrayList<ContactItemBean> mItemLists; //最终结果的条目集合(包含分组的字母)


    /**
     * Item条目类型枚举类定义
     */
    public enum ITEM_TYPE {
        ITEM_CONTACT_HEAD_CHARACTER_TYPE, //联系人名称的首字母条目
        ITEM_CONTACT_NAME_TYPE            //联系人实际的全称条目
    }


    public ContactRecyclerViewAdapter(Context context, ArrayList<String> cNameLists) {
        this.mContactNameLists = cNameLists;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void handleContact() {
        /* HashMap一对一关联,key为拼音的姓名全称,value为联系人姓名 */
        HashMap<String,String> pinYinNameMaps = new HashMap();

        /* (1)对每个联系人的姓名做拼音转换,将其保存到mContactPinyinLists变量集合中 */
        mContactPinyinLists = new ArrayList();
        for (int i = 0; i < mContactNameLists.size(); i++) {
            final String contactName = mContactNameLists.get(i);
            final String pinyinName = CommonUtils.getPingYin(contactName);
            pinYinNameMaps.put(pinyinName, contactName);
            mContactPinyinLists.add(pinyinName);
        }
        /* (2)对拼音list集合进行首字母排序 */
        Collections.sort(mContactPinyinLists, new ContactPinyinComparator());

        /* (3)对拼音list排序的集合进行遍历,将姓名首字母保存到集合mHeadCharacterLists,将姓名首字母Item和该首字母分类中对应的联系人姓名Item保存到集合mItemLists */
        mHeadCharacterLists = new ArrayList();
        mItemLists = new ArrayList();
        for (int i = 0; i < mContactPinyinLists.size(); i++) {
            String pinyinName = mContactPinyinLists.get(i); //获取拼音全称
            String character = (pinyinName.charAt(0) + "").toUpperCase(Locale.ENGLISH); //获取拼音首字母

            /* 给相同首字母的联系人姓名归类前添加一个起始的首字母条目Item */
            if (!mHeadCharacterLists.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    mHeadCharacterLists.add(character);
                    mItemLists.add(new ContactItemBean(character, ITEM_TYPE.ITEM_CONTACT_HEAD_CHARACTER_TYPE.ordinal()));
                } else {
                    if (!mHeadCharacterLists.contains("#")) {
                        mHeadCharacterLists.add("#");
                        mItemLists.add(new ContactItemBean("#", ITEM_TYPE.ITEM_CONTACT_HEAD_CHARACTER_TYPE.ordinal()));
                    }
                }
            }

            /* 通过拼音全称获取联系人姓名,之后添加该首字母分类中对应的各个联系人姓名条目Item */
            String contactName = pinYinNameMaps.get(pinyinName);
            mItemLists.add(new ContactItemBean(contactName, ITEM_TYPE.ITEM_CONTACT_NAME_TYPE.ordinal()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("zhangming","ContactRecyclerViewAdapter onCreateViewHolder viewType = "+viewType);
        if (viewType == ITEM_TYPE.ITEM_CONTACT_HEAD_CHARACTER_TYPE.ordinal()) {
            /* 返回联系人首字母的ViewHolder实例 */
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_contact_character, parent, false));
        } else {
            /* 返回联系人全称的ViewHolder实例 */
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact_name, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Log.i("zhangming","ContactRecyclerViewAdapter onBindViewHolder position = "+position);
        if (viewHolder instanceof CharacterHolder) {
            ((CharacterHolder) viewHolder).tv_character.setText(mItemLists.get(position).getContactName());
        } else if (viewHolder instanceof ContactHolder) {
            ((ContactHolder) viewHolder).tv_contact_name.setText(mItemLists.get(position).getContactName());
        }
    }

    @Override
    public int getItemCount() {
        return (mItemLists == null)?(0):(mItemLists.size());
    }

    @Override
    public int getItemViewType(int position) {
        /* 获取条目的ItemType类型 */
        /* 调用方式: tryGetViewHolderForPositionByDeadline -> type = mAdapter.getItemViewType(); -> mAdapter.createViewHolder(RecyclerView.this,type); -> onCreateViewHolder(...) */
        final int itemType = mItemLists.get(position).getItemType();
        Log.i("zhangming","ContactRecyclerViewAdapter itemType = "+itemType);
        return itemType;
    }

    /**
     * 联系人拼音首字母的ViewHolder子类
     */
    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView tv_character;

        CharacterHolder(View itemView) {
            super(itemView);
            /* 联系人首字母条目 */
            tv_character = (TextView) itemView.findViewById(R.id.tv_character);
        }
    }

    /**
     * 联系人全称的ViewHolder子类
     */
    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView tv_contact_name;

        ContactHolder(View itemView) {
            super(itemView);
            tv_contact_name = (TextView) itemView.findViewById(R.id.tv_contact_name);
        }
    }

    /**
     * 通过字符串首字符character获取滚动的条目Item位置索引
     * @param character
     * @return
     */
    public int getScrollPosition(String character) {
        try {
            if (mHeadCharacterLists.contains(character)) {
                for (int i = 0; i < mItemLists.size(); i++) {
                    if (mItemLists.get(i).getContactName().equals(character)) {
                        return i;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; //返回值-1表示不会滑动
    }

    /**
     * 联系人拼音首字母比较类,使用hashcode值比较大小
     */
    private class ContactPinyinComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            int c1 = (str1.charAt(0) + "").toUpperCase().hashCode();
            int c2 = (str2.charAt(0) + "").toUpperCase().hashCode();

            boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); //不是字母
            boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); //不是字母
            if (c1Flag && !c2Flag) {
                return 1;
            } else if (!c1Flag && c2Flag) {
                return -1;
            }

            return c1 - c2;
        }
    }


    public static void main(String args[]) {
        /**
         * h = 31 * h + charAt(i);  默认值h = 0;  此处字符串长度len=1;
         * hash = h = charAt(0) = 字符的ASCII数值
         * 所以‘A’~'Z','a'~'z'字符可以直接通过hashCode值比较,因为其hashCode的数值就是等于字符的ASCII数值
         */
        for(char c = 'A'; c <= 'Z'; c++) {
            System.out.print((c+"").hashCode());
            System.out.print(" ");
        }
        System.out.println();
    }
}
