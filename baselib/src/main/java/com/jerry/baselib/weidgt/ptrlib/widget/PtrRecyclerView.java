package com.jerry.baselib.weidgt.ptrlib.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.jerry.baselib.R;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.baselib.weidgt.ptrlib.OnLoadMoreListener;
import com.jerry.baselib.weidgt.ptrlib.OnRefreshListener;
import com.jerry.baselib.weidgt.ptrlib.PtrDefaultHandler;
import com.jerry.baselib.weidgt.ptrlib.PtrFrameLayout;
import com.jerry.baselib.weidgt.ptrlib.header.PtrSimpleHeader;

/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:RecyclerView刷新封装
 */
public class PtrRecyclerView extends FrameLayout {

    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<?> mAdapter;
    private View mHeaderView;
    private View mFooterView;
    protected PtrFrameLayout mPtrFrameLayout;
    protected WeakHandler mWeakHandler;
    protected LayoutManager mLayoutManager;
    protected OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    public boolean canRefresh = true;
    public boolean canLoadMore;

    public PtrRecyclerView(Context context) {
        this(context, null);
    }

    public PtrRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerView getRefreshableView() {
        return mRecyclerView;
    }

    public LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        View.inflate(getContext(), getPtrLayoutId(), this);
        mPtrFrameLayout = findViewById(R.id.ptrFrameLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        PtrSimpleHeader mPtrSimpleHeader = new PtrSimpleHeader(getContext());
        mPtrFrameLayout.setHeaderView(mPtrSimpleHeader);
        mPtrFrameLayout.addPtrUIHandler(mPtrSimpleHeader);
        mPtrFrameLayout.setPtrHandler(initPtrHandler());
        mRecyclerView.setOnTouchListener((v, event) -> mPtrFrameLayout.isRefreshing());
    }

    @NonNull
    protected PtrDefaultHandler initPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return canRefresh && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        };
    }

    protected int getPtrLayoutId() {
        return R.layout.refresh_recyclerview;
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    public void setAdapter(@NonNull BaseRecyclerAdapter<?> adapter) {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }

    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener listener) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && canLoadMore) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int mLastItemVisible = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        if (!mPtrFrameLayout.isRefreshing() && mAdapter.getItemCount() > 0 && mLastItemVisible == mAdapter.getItemCount() - 1) {
                            listener.onLoadMore();
                        }
                    }
                }
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    View currentFocus = ((Activity) getContext()).getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }
        });
    }

    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    public void autoRefresh() {
        mPtrFrameLayout.autoRefresh();
    }

    public void setPrtBgColor(@ColorRes int color) {
        mPtrFrameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    public void addHeaderView(View view) {
        if (getHeaderViewCount() > 0) {
            return;
        }
        mHeaderView = view;
        mAdapter.addHeaderView(mHeaderView);
    }

    public int getHeaderViewCount() {
        return mAdapter.getHeadersCount();
    }

    public void addFooterView() {
        if (getFooterViewsCount() > 0) {
            return;
        }
        if (mFooterView == null) {
            mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
        }
        mAdapter.addFooterView(mFooterView);
    }

    public void removeFooterView() {
        if (getFooterViewsCount() > 0) {
            mAdapter.removeFooterView();
        }
    }

    public int getFooterViewsCount() {
        return mAdapter.getFootersCount();
    }

    public void setFooterViewStatus(boolean isFinish) {
        if (mFooterView == null) {
            return;
        }
        if (isFinish) {
            mFooterView.findViewById(R.id.loading_view).setVisibility(View.GONE);
            mFooterView.findViewById(R.id.no_more_view).setVisibility(View.VISIBLE);
        } else {
            mFooterView.findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
            mFooterView.findViewById(R.id.no_more_view).setVisibility(View.GONE);
        }
    }

    public void onRefreshComplete() {
        if (mWeakHandler == null) {
            mWeakHandler = new WeakHandler();
        }
        mWeakHandler.postDelayed(() -> {
            if (mPtrFrameLayout != null) {
                mPtrFrameLayout.refreshComplete();
            }
        }, PtrSimpleView.REFRESH_LOADING_TIME);
    }

    public void disableWhenHorizontalMove(boolean disable) {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.disableWhenHorizontalMove(disable);
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.mOnRefreshListener = refreshListener;
    }

}
