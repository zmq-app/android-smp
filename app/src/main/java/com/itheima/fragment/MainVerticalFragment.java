package com.itheima.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.smp.R;
import com.itheima.di.modules.GlideApp;
import com.itheima.model.Item;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Subject 主界面MvpMainActivity通过垂直的ViewPager控件分页上下滑动,每一页展示的是当前的一个Fragment实例
 * @Author  zhangming
 * @Date    2020-11-01 16:49
 */
public class MainVerticalFragment extends Fragment {
    @BindView(R.id.image_iv)
    ImageView imageIv;
    @BindView(R.id.comment_tv)
    TextView commentTv;
    @BindView(R.id.like_tv)
    TextView likeTv;
    @BindView(R.id.readcount_tv)
    TextView readcountTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.content_tv)
    TextView contentTv;
    @BindView(R.id.author_tv)
    TextView authorTv;
    @BindView(R.id.type_tv)
    TextView typeTv;
    @BindView(R.id.image_type)
    ImageView imageType;
    @BindView(R.id.download_start_white)
    ImageView downloadStartWhite;
    @BindView(R.id.home_advertise_iv)
    ImageView homeAdvertiseIv;
    @BindView(R.id.pager_content)
    RelativeLayout pagerContent;

    public static Fragment instance(Item item) {
        Fragment fragment = new MainVerticalFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.item_main_page, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Item item = getArguments().getParcelable("item");
        final int model = Integer.valueOf(item.getModel());

        if (model == 5){
            pagerContent.setVisibility(View.GONE);
            homeAdvertiseIv.setVisibility(View.VISIBLE);
            GlideApp.with(this.getContext()).load(item.getThumbnail()).centerCrop().into(homeAdvertiseIv);
        }else{
            pagerContent.setVisibility(View.VISIBLE);
            homeAdvertiseIv.setVisibility(View.GONE);
            GlideApp.with(this.getContext()).load(item.getThumbnail()).centerCrop().into(imageIv);
            commentTv.setText(item.getComment());
            likeTv.setText(item.getGood());
            readcountTv.setText(item.getView());
            titleTv.setText(item.getTitle());
            contentTv.setText(item.getExcerpt());
            authorTv.setText(item.getAuthor());
            typeTv.setText(item.getCategory());
            switch (model) {
                case 2:
                    imageType.setVisibility(View.VISIBLE);
                    downloadStartWhite.setVisibility(View.GONE);
                    imageType.setImageResource(R.drawable.library_video_play_symbol);
                    break;
                case 3:
                    imageType.setVisibility(View.VISIBLE);
                    downloadStartWhite.setVisibility(View.VISIBLE);
                    imageType.setImageResource(R.drawable.library_voice_play_symbol);
                    break;
                default:
                    downloadStartWhite.setVisibility(View.GONE);
                    imageType.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
