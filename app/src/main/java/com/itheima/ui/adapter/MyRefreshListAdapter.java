package com.itheima.ui.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.ui.RefreshViewActivity;
import com.itheima.utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Subject 为ListView定制的适配器,包含基本的刷新加载数据,滑动删除等功能
 * @Author  zhangming
 * @Date    2018-09-26 16:13
 */
public class MyRefreshListAdapter extends BaseAdapter {
    private Context mContext;
    private List<RefreshViewActivity.DataModel> mDataList;

    private Map<Integer,Boolean> isCheckBoxStatusMaps;
    private Map<Integer,Boolean> isViewSlideMaps;
    private Map<Integer,Integer> viewSlideDistanceMaps;
    public static HashMap<Integer,View> lMaps;   //定义hashMap用来存放之前创建的每一项item

    private int curSlideItemIndex = -1;          //记录当前手指滑动的条目索引号Index
    private static final int btnWidthDP = 100;   //删除按钮的宽度100dp

    public addRefreshDataListener refreshListener; //回调监听实例listener

    public MyRefreshListAdapter(Context context,List<RefreshViewActivity.DataModel> list) {
        this.mContext  = context;
        this.mDataList = list;

        this.isCheckBoxStatusMaps = new HashMap<>();
        this.isViewSlideMaps = new HashMap<>();
        this.viewSlideDistanceMaps = new HashMap<>();
        this.lMaps = new HashMap<>();

        for(int i=0;i<list.size();i++){
            isCheckBoxStatusMaps.put(i,false);
            isViewSlideMaps.put(i,false);
            viewSlideDistanceMaps.put(i,0);
        }

        this.setOnRefreshDataListener(new addRefreshDataListener() {
            @Override
            public void onRefreshData(List<RefreshViewActivity.DataModel> newDataList) {
                if(newDataList != null){
                    int nCount = newDataList.size();
                    if(nCount > 0){
                        //获取现有Map集合的数目
                        int curCheckBoxStatusMapsNum = isCheckBoxStatusMaps.size();
                        int curViewSlideMapsNum = isViewSlideMaps.size();
                        int curViewSlideDistanceMapsNum = viewSlideDistanceMaps.size();

                        //添加上拉加载后刷新的数据,并更新各个Map集合的状态信息
                        for(int i=0;i<nCount;i++){
                            isCheckBoxStatusMaps.put((curCheckBoxStatusMapsNum+i),false);
                            isViewSlideMaps.put((curViewSlideMapsNum+i),false);
                            viewSlideDistanceMaps.put((curViewSlideDistanceMapsNum+i),0);
                            mDataList.add(newDataList.get(i));
                        }

                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder mHolder;
        if(lMaps.get(position) == null){
            mHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.refresh_list_item,null,false);
            mHolder.imageView = (ImageView) convertView.findViewById(R.id.left_image);
            mHolder.textView  = (TextView) convertView.findViewById(R.id.tv_content);
            mHolder.checkBox  = (CheckBox) convertView.findViewById(R.id.ck_box);
            mHolder.deleteBtn = (DeleteButton) convertView.findViewById(R.id.delete_btn);

            int btnWidthSP = CommonUtils.dip2px(mContext,btnWidthDP);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0,0,(-1)*(btnWidthSP),0);
            mHolder.deleteBtn.setLayoutParams(params);

            convertView.setTag(mHolder);
            lMaps.put(position,convertView);  //解决View条目复用的问题
        }else{
            convertView = lMaps.get(position);
            mHolder = (ViewHolder) convertView.getTag();
        }

        //设置相应的控件信息和状态
        RefreshViewActivity.DataModel dm = mDataList.get(position);
        mHolder.imageView.setImageResource(dm.image_local_id);
        mHolder.textView.setText(dm.content);

        Object result = isCheckBoxStatusMaps.get(position);
        if(result != null){
            if(result instanceof Boolean){
                Boolean status = (Boolean) result;
                mHolder.checkBox.setChecked(status);
            }
        }

        //设置CheckBox选择状态
        mHolder.checkBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(isCheckBoxStatusMaps.get(position)){
                            isCheckBoxStatusMaps.put(position,false);
                        }else{
                            isCheckBoxStatusMaps.put(position,true);
                        }
                        notifyDataSetChanged();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

        //设置回调监听方法,保存对应View条目的滑动状态信息,之后重新通知适配器更新ListView各个条目
        mHolder.deleteBtn.setOnSlideListener(new DeleteButton.OnSlideViewListener() {
            @Override
            public void onSlideView(int slideIndex,boolean isSlideStatus,int dx) {
                curSlideItemIndex = slideIndex;
                isViewSlideMaps.put(slideIndex,isSlideStatus);
                viewSlideDistanceMaps.put(slideIndex,dx);
                notifyDataSetChanged();
            }
        });

        //TODO 优先级OnTouchListener > OnTouchEvent > OnClickListener [会在OnTouchEvent方法的ACTION_UP事件中执行performClick方法]
        mHolder.deleteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /** 此处删除Button仅仅只能接受到父控件认定的点击事件,滑动事件在父控件已作出拦截,即ACTION_MOVE没有意义,onTouch这个方法会执行两次,DOWN一次,UP一次 **/
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        //仅处理抬起的事件
                        //更新View视图的Map集合[做了移除操作,集合中的下标索引需要与界面上的位置序号保持一致]
                        Map removeViews = removeItemFromMap(lMaps,position);
                        lMaps.clear();
                        lMaps.putAll(removeViews);

                        //更新View滑动状态的Map集合[做了移除操作,集合中的下标索引需要与界面上的位置序号保持一致]
                        Map removeVStatus = removeItemFromMap(isViewSlideMaps,position);
                        isViewSlideMaps.clear();
                        isViewSlideMaps.putAll(removeVStatus);

                        //更新View滑动距离的Map集合,未滑动距离默认为初始值0[做了移除操作,集合中的下标索引需要与界面上的位置序号保持一致]
                        Map removeVDistance = removeItemFromMap(viewSlideDistanceMaps,position);
                        viewSlideDistanceMaps.clear();
                        viewSlideDistanceMaps.putAll(removeVDistance);

                        //更新CheckBox状态信息的Map集合
                        Map removeCStatus = removeItemFromMap(isCheckBoxStatusMaps,position);
                        isCheckBoxStatusMaps.clear();
                        isCheckBoxStatusMaps.putAll(removeCStatus);

                        //删除数据集合中指定位置的Item条目
                        mDataList.remove(position);

                        //通知适配器数据源变化,更新ListView
                        notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        if(isViewSlideMaps.get(position) && (curSlideItemIndex == position)){
            int mdx = viewSlideDistanceMaps.get(position);
            convertView.scrollBy((-1)*mdx,0);
            //TODO 重置当前滑动索引值为初始值(-1);主要避免竖直方向上滑动ListView导致视图重绘,进而执行scrollBy滑动方法;只有在回调方法onSlideView设置方才有效
            curSlideItemIndex = -1;
        }

        return convertView;
    }

    private Map removeItemFromMap(Map rMaps,int index){
        Map retMaps = new HashMap();
        int tmpSize = rMaps.size();  //移除之前保存Map集合之前的数目
        rMaps.remove(index);

        int nIndex = 0;
        for(int i=0;i<tmpSize;i++){
            Object obj = rMaps.get(i);
            if(obj != null){
                retMaps.put(nIndex,obj);
                nIndex++;
            }
        }

        return retMaps;
    }

    /** 构造MyRefreshListAdapter类的回调监听接口 **/
    public interface addRefreshDataListener{
        void onRefreshData(List<RefreshViewActivity.DataModel> newDataList);
    }

    public void setOnRefreshDataListener(addRefreshDataListener listener){
        this.refreshListener = listener;
    }

    class ViewHolder{
        ImageView     imageView;
        TextView      textView;
        CheckBox      checkBox;
        DeleteButton  deleteBtn;
    }

    public static class DeleteButton extends Button{
        public OnSlideViewListener mListener;

        public DeleteButton(Context context) {
            super(context);
        }

        public DeleteButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public interface OnSlideViewListener{
            void onSlideView(int slideIndex,boolean isSlideStatus,int dx);
        }

        public void setOnSlideListener(OnSlideViewListener mListener) {
            this.mListener = mListener;
        }
    }
}
