package com.jerry.baselib.weidgt.ptrlib.itemdecoration.sticky;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jerry
 * @createDate 2019/4/11
 * @copyright www.axiang.com
 * @description 时间轴效果
 */
public class TimeStickyItemDecoration extends StickyItemDecoration {

    public TimeStickyItemDecoration(StickyHeadContainer stickyHeadContainer, DataCallback dataCallback) {
        super(stickyHeadContainer, dataCallback);
    }

    // 当我们调用mRecyclerView.addItemDecoration()方法添加decoration的时候，RecyclerView在绘制的时候，去会绘制decorator，即调用该类的onDraw和onDrawOver方法，
    // 1.onDraw方法先于drawChildren
    // 2.onDrawOver在drawChildren之后，一般我们选择复写其中一个即可。
    // 3.getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator。
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        checkCache(parent);
        if (mAdapter == null) {
            // checkCache的话RecyclerView未设置之前mAdapter为空
            return;
        }
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        // 获取第一个可见的item位置
        mFirstVisiblePosition = findFirstVisiblePosition(layoutManager);

        int stickyHeadPosition = findStickyHeadPosition(mFirstVisiblePosition);
        if (stickyHeadPosition >= 0 && mStickyHeadPosition != stickyHeadPosition) {
            // 标签位置有效并且和缓存的位置不同
            mStickyHeadPosition = stickyHeadPosition;
        }

        if (mFirstVisiblePosition >= mStickyHeadPosition && mStickyHeadPosition != -1) {
            View belowView = parent.findChildViewUnder(c.getWidth() >> 1, 0.01f);
            mDataCallback.onDataChange(mStickyHeadContainer, mStickyHeadPosition);
            int offset;
            if (isStickyHead() && belowView != null) {
                offset = belowView.getTop();
            } else {
                offset = 0;
            }
            mStickyHeadContainer.scrollChild(offset);
        }
    }

    /**
     * 查找到view对应的位置从而判断出是否标签类型
     */
    private boolean isStickyHead() {
        if (mAdapter.getItemCount() == 1) {
            return true;
        }
        int type = mAdapter.getItemViewType(mFirstVisiblePosition + 1);
        return TYPE_STICKY_HEAD == type;
    }
}
