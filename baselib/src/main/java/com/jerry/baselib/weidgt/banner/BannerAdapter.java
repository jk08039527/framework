package com.jerry.baselib.weidgt.banner;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.bean.BannerBean;
import com.jerry.baselib.parsehelper.WebViewActivity;
import com.jerry.baselib.util.CollectionUtils;

/**
 * Created by zhangkk on 2018/6/10.
 *
 * @Description
 */
public class BannerAdapter extends BaseRecyclerAdapter<BannerBean> {

    public BannerAdapter(Context context, List<BannerBean> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.banner_image;
    }

    @Override
    public void convert(RecyclerViewHolder holder, int position, int viewType, BannerBean bean) {
        if (CollectionUtils.isEmpty(mData)) {
            return;
        }
        ImageView img = holder.getView(R.id.banner_image);
        Glide.with(mContext).load(bean.getCover()).into(img);
        img.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra(Key.DATA, bean.getUrl());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
    }
}
