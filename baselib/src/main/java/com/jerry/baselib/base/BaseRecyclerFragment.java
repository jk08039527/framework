package com.jerry.baselib.base;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.NetworkUtil;
import com.jerry.baselib.weidgt.ptrlib.widget.PtrRecyclerView;
import com.jerry.baselib.R;

public abstract class BaseRecyclerFragment<T> extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    protected int pageSize = 10;
    protected boolean canLoadMore = true;
    protected int page;
    protected PtrRecyclerView mPtrRecyclerView;
    protected BaseRecyclerAdapter<T> mAdapter;
    protected List<T> mData = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mAdapter = initAdapter();
        mAdapter.setOnItemClickListener(this);
    }

    protected abstract BaseRecyclerAdapter<T> initAdapter();

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fg_recycler_with_viewstub;
    }

    @Override
    protected void initView(View view) {
        mPtrRecyclerView = view.findViewById(R.id.ptrRecyclerView);
        mPtrRecyclerView.setLayoutManager(getLayoutManager());
        mPtrRecyclerView.setAdapter(mAdapter);
        mPtrRecyclerView.setOnLoadMoreListener(() -> {
            if (canLoadMore) {
                page++;
                getData();
            }
        });
        mPtrRecyclerView.setOnRefreshListener(() -> {
            onPreRefresh();
            reload();
        });
    }

    protected LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mActivity);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyLoad();
        }
    }

    protected void lazyLoad() {
        if (mAdapter != null && !CollectionUtils.isEmpty(mData)) {
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

    @Override
    public void reload() {
        canLoadMore = true;
        page = 0;
        getData();
    }

    @Override
    public void onClick(final View v) {

    }

    protected void onAfterRefresh() {
        mAdapter.notifyDataSetChanged();
        closeLoadingDialog();
        mPtrRecyclerView.onRefreshComplete();
    }
}
