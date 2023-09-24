package com.jerry.baselib.weidgt.ptrlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.jerry.baselib.util.WeakHandler;
import com.jerry.baselib.weidgt.ptrlib.OnRefreshListener;
import com.jerry.baselib.weidgt.ptrlib.PtrDefaultHandler;
import com.jerry.baselib.weidgt.ptrlib.PtrFrameLayout;
import com.jerry.baselib.weidgt.ptrlib.PtrUIHandler;
import com.jerry.baselib.weidgt.ptrlib.indicator.PtrIndicator;
import com.jerry.baselib.R;

/**
 * Created by wzl on 2019/9/23.
 *
 * @Description 类说明:不带hearder的scrollView
 */
public class PtrEmptyHeaderScrollView extends FrameLayout {

    private View mContentView;

    private NestedScrollView mScrollView;
    private PtrFrameLayout mPtrFrameLayout;
    private WeakHandler mWeakHandler;

    public boolean canRefresh = true;
    private OnRefreshListener mOnRefreshListener;

    public PtrEmptyHeaderScrollView(Context context) {
        this(context, null);
    }

    public PtrEmptyHeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.refresh_scrollview, this);
        mPtrFrameLayout = findViewById(R.id.ptrFrameLayout);
        mScrollView = findViewById(R.id.scrollView);
        if (mContentView != null) {
            mScrollView.addView(mContentView);
        }
        PtrEmptyHeader mPtrSimpleHeader = new PtrEmptyHeader(getContext());
        mPtrFrameLayout.setHeaderView(mPtrSimpleHeader);
        mPtrFrameLayout.addPtrUIHandler(mPtrSimpleHeader);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
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
        });
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
        if (mContentView != null) {
            mScrollView.addView(mContentView);
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

    public NestedScrollView getRefreshableView() {
        return mScrollView;
    }

    public void disableWhenHorizontalMove(boolean disable) {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.disableWhenHorizontalMove(disable);
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.mOnRefreshListener = refreshListener;
    }

    public void setHeaderColor(final int color) {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.setBackgroundColor(color);
        }
    }

    public class PtrEmptyHeader extends LinearLayout implements PtrUIHandler {

        public PtrEmptyHeader(Context context) {
            this(context, null);
        }

        public PtrEmptyHeader(Context context, AttributeSet attrs) {
            super(context, attrs);
            View.inflate(context, R.layout.llayout, this);
        }

        @Override
        public void onUIReset(final PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshPrepare(PtrFrameLayout frame) {
        }

        @Override
        public void onUIRefreshBegin(PtrFrameLayout frame) {
        }

        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {
        }

        @Override
        public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        }
    }
}
