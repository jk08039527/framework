package com.jerry.baselib.base;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.jerry.baselib.util.NetworkUtil;
import com.jerry.baselib.weidgt.ptrlib.OnLoadMoreListener;
import com.jerry.baselib.weidgt.ptrlib.widget.PtrRecyclerView;
import com.jerry.baselib.R;

public abstract class BaseRecyclerActivity<T> extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    protected PtrRecyclerView mPtrRecyclerView;
    protected BaseRecyclerAdapter<T> mAdapter;
    protected List<T> mData = new ArrayList<>();
    protected boolean canLoadMore = true;
    protected int pageSize = 30;
    protected int page;

    protected OnLoadMoreListener mLoadMoreListener = () -> {
        if (canLoadMore) {
            page++;
            getData();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lazyLoad();
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_ptrrv;
    }

    @Override
    protected void initView() {
        mAdapter = initAdapter();
        mAdapter.setOnItemClickListener(this);
        mPtrRecyclerView = findViewById(R.id.ptrRecyclerView);
        mPtrRecyclerView.setLayoutManager(getLayoutManager());
        mPtrRecyclerView.setAdapter(mAdapter);
        mPtrRecyclerView.setOnLoadMoreListener(mLoadMoreListener);
        mPtrRecyclerView.setOnRefreshListener(() -> {
            onPreRefresh();
            reload();
        });
    }

    protected LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected abstract BaseRecyclerAdapter<T> initAdapter();

    private void lazyLoad() {
        if (mAdapter != null && mData != null && mData.size() != 0) {
            mAdapter.notifyDataSetChanged();
        } else if (canLoadMore) {
            if (NetworkUtil.isNetworkAvailable() && mData.size() == 0) {
                loadingDialog();
            }
            getData();
        }
    }

    /**
     * 刷新前还原排序
     */
    protected void onPreRefresh() {
    }

    /**
     * 从网络端获取数据
     */
    protected abstract void getData();

    public void reload() {
        canLoadMore = true;
        page = 0;
        getData();
    }

    protected void onAfterRefresh() {
        mAdapter.notifyDataSetChanged();
        closeLoadingDialog();
        mPtrRecyclerView.onRefreshComplete();
    }
}
