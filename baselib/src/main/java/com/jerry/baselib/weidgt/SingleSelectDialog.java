package com.jerry.baselib.weidgt;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jerry.baselib.R;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.BaseRecyclerAdapter.OnItemClickListener;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.util.CollectionUtils;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 单选类型对话框, (有title)
 */
public class SingleSelectDialog extends BaseDialog {

    private final List<SingleSelectBean> mData = new ArrayList<>();
    private final OnItemClickListener mOnItemClickListener;
    private BaseRecyclerAdapter<SingleSelectBean> mAdapter;
    private TextView tvTitle;
    private String title;


    public SingleSelectDialog(Context context, OnItemClickListener onItemClickListener) {
        super(context);
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_single_select;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle = findViewById(R.id.title_tv);
        RecyclerView recyclerView = findViewById(R.id.rv_items);
        tvTitle.setText(title == null ? mContext.getString(R.string.notice) : title);
        mAdapter = new BaseRecyclerAdapter<SingleSelectBean>(mContext, mData) {

            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_img_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final SingleSelectBean bean) {
                ImageView imageView = holder.getView(R.id.imageView);
                if (TextUtils.isEmpty(bean.url)) {
                    imageView.setVisibility(View.GONE);
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(bean.url).into(imageView);
                }
                TextView tvTitle = holder.getView(R.id.tv_title);
                tvTitle.setText(bean.title);
                TextView tvContent = holder.getView(R.id.tv_start_time);
                tvContent.setText(bean.content);
            }
        };
        mAdapter.setOnItemClickListener((itemView, position) -> {
            dismiss();
            mOnItemClickListener.onItemClick(itemView,position);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);
    }

    public void setDialogTitle(String title) {
        this.title = title;
        if (tvTitle != null) {
            tvTitle.setHint(title);
        }
    }

    public void setData(List<SingleSelectBean> data) {
        mData.clear();
        if (!CollectionUtils.isEmpty(data)) {
            mData.addAll(data);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static class SingleSelectBean {

        public String url;
        public String title;
        public String content;
    }
}
