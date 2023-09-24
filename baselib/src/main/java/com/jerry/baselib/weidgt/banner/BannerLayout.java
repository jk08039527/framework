package com.jerry.baselib.weidgt.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.impl.OnDataCallback;
import com.jerry.baselib.util.DisplayUtil;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.baselib.R;

/**
 * Created by wzl on 2018/6/10.
 *
 * @Description 用 {@link RecyclerView} 实现轮播图
 */
public class BannerLayout extends FrameLayout implements LifecycleObserver {

    private static final int AUTO_PLAY = 1000;
    private static final int AUTO_PLAY_DURATION = 5000;

    private Drawable mSelectedDrawable;
    private Drawable mUnselectedDrawable;

    private RecyclerView mRecyclerView;

    private BannerLayoutManager mLayoutManager;
    private IndicatorAdapter mIndicatorAdapter;

    private int mItemSpace;
    private int mCurrentIndex;
    private int mBannerSize = 1;
    private int mIndicatorMargin;

    private float mMoveSpeed;
    private float mCenterScale;

    private boolean hasInit;
    private boolean allowAutoPlaying;
    private boolean isPlaying;
    /**
     * 监听轮播指针
     */
    private OnDataCallback<Integer> mIndexChangedListener;

    private WeakHandler mHandler = new WeakHandler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == AUTO_PLAY) {
                if (mCurrentIndex == mLayoutManager.getCurrentPosition()) {
                    if (allowAutoPlaying) {
                        ++mCurrentIndex;
                    }
                    if (hasInit && allowAutoPlaying) {
                        refreshIndicator(mCurrentIndex);
                        if (mIndexChangedListener != null) {
                            mIndexChangedListener.onDataCallback(mCurrentIndex);
                        }
                        mRecyclerView.smoothScrollToPosition(mCurrentIndex);
                    }
                    mHandler.sendEmptyMessageDelayed(AUTO_PLAY, AUTO_PLAY_DURATION);
                }
            }
            return false;
        }
    });

    public BannerLayout(@NonNull Context context) {
        this(context, null);
    }

    public BannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerLayout);
        mItemSpace = a.getInt(R.styleable.BannerLayout_itemSpace, 0);
        mCenterScale = a.getFloat(R.styleable.BannerLayout_centerScale, 1.0f);
        mMoveSpeed = a.getFloat(R.styleable.BannerLayout_moveSpeed, 1.0f);
        mIndicatorMargin = DisplayUtil.getDimensionPixelSize(R.dimen.four_dp);
        float leftMargin = a.getDimension(R.styleable.BannerLayout_indicatorStartMargin, 0);
        float rightMargin = a.getDimension(R.styleable.BannerLayout_indicatorEndMargin, 0);
        float topMargin = a.getDimension(R.styleable.BannerLayout_indicatorTopMargin, 0);
        float bottomMargin = a.getDimension(R.styleable.BannerLayout_indicatorBottomMargin, DisplayUtil.getDimensionPixelSize(R.dimen.four_dp));
        int indicatorDefaultColor = a
            .getColor(R.styleable.BannerLayout_indicatorDefaultColor, ContextCompat.getColor(context, R.color.light_text_color));
        int indicatorSelectedColor = a
            .getColor(R.styleable.BannerLayout_indicatorSelectedColor, ContextCompat.getColor(context, R.color.blue_primary));
        int indicatorGravity = a.getInt(R.styleable.BannerLayout_indicatorGravity, 0);
        int indicatorHw = DisplayUtil.getDimensionPixelSize(R.dimen.six_dp);
        if (mSelectedDrawable == null) {
            //绘制默认选中状态图形
            GradientDrawable selectedGradientDrawable = new GradientDrawable();
            selectedGradientDrawable.setShape(GradientDrawable.OVAL);
            selectedGradientDrawable.setColor(indicatorSelectedColor);
            selectedGradientDrawable.setSize(indicatorHw, indicatorHw);
            selectedGradientDrawable.setCornerRadius(indicatorHw >> 1);
            mSelectedDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        }
        if (mUnselectedDrawable == null) {
            //绘制默认未选中状态图形
            GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
            unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            unSelectedGradientDrawable.setColor(indicatorDefaultColor);
            unSelectedGradientDrawable.setSize(indicatorHw, indicatorHw);
            unSelectedGradientDrawable.setCornerRadius(indicatorHw >> 1);
            mUnselectedDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        }

        int orientation = LinearLayoutManager.HORIZONTAL;
        a.recycle();
        //轮播图部分
        mRecyclerView = new RecyclerView(context);
        LayoutParams vpLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRecyclerView, vpLayoutParams);
        mLayoutManager = new BannerLayoutManager(getContext(), orientation);
        mLayoutManager.setItemSpace(mItemSpace);
        mLayoutManager.setCenterScale(mCenterScale);
        mLayoutManager.setMoveSpeed(mMoveSpeed);
        mRecyclerView.setLayoutManager(mLayoutManager);
        new CenterSnapHelper().attachToRecyclerView(mRecyclerView);

        //指示器部分
        RecyclerView indicatorContainer = new RecyclerView(context);
        LinearLayoutManager indicatorLayoutManager = new LinearLayoutManager(context, orientation, false);
        indicatorContainer.setLayoutManager(indicatorLayoutManager);
        mIndicatorAdapter = new IndicatorAdapter();
        indicatorContainer.setHasFixedSize(true);
        indicatorContainer.setAdapter(mIndicatorAdapter);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (indicatorGravity) {
            case 1:
                params.gravity = Gravity.BOTTOM | Gravity.START;
                break;
            case 2:
                params.gravity = Gravity.BOTTOM | Gravity.END;
                break;
            case 0:
            default:
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
        }
        params.leftMargin = (int) leftMargin;
        params.rightMargin = (int) rightMargin;
        params.topMargin = (int) topMargin;
        params.bottomMargin = (int) bottomMargin;
        addView(indicatorContainer, params);
    }

    /**
     * 设置是否自动播放（上锁）
     *
     * @param playing 开始播放
     */
    protected synchronized void setPlaying(boolean playing) {
        if (!isPlaying && playing) {
            mHandler.sendEmptyMessageDelayed(AUTO_PLAY, AUTO_PLAY_DURATION);
            isPlaying = true;
        } else if (isPlaying && !playing) {
            mHandler.removeMessages(AUTO_PLAY);
            isPlaying = false;
        }
    }

    /**
     * 设置当前图片缩放系数
     */
    public void setCenterScale(float centerScale) {
        this.mCenterScale = centerScale;
        mLayoutManager.setCenterScale(centerScale);
    }

    /**
     * 设置跟随手指的移动速度
     */
    public void setMoveSpeed(float moveSpeed) {
        this.mMoveSpeed = moveSpeed;
        mLayoutManager.setMoveSpeed(moveSpeed);
    }

    /**
     * 设置图片间距
     */
    public void setItemSpace(int itemSpace) {
        this.mItemSpace = itemSpace;
        mLayoutManager.setItemSpace(itemSpace);
    }

    public void setOrientation(int orientation) {
        mLayoutManager.setOrientation(orientation);
    }

    @OnLifecycleEvent(Event.ON_RESUME)
    public void onResume() {
        startAutoPlay();
    }

    private void startAutoPlay() {
        mHandler.sendEmptyMessageDelayed(AUTO_PLAY, AUTO_PLAY_DURATION);
        isPlaying = true;
    }

    @OnLifecycleEvent(Event.ON_DESTROY)
    public void onDestroy() {
        stopAutoPlay();
    }

    private void stopAutoPlay() {
        mHandler.removeMessages(AUTO_PLAY);
        isPlaying = false;
    }

    @OnLifecycleEvent(Event.ON_PAUSE)
    public void onPause() {
        stopAutoPlay();
    }


    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 轮播图指针改变
     */
    public void setIndexChangedListener(final OnDataCallback<Integer> indexChangedListener) {
        mIndexChangedListener = indexChangedListener;
    }

    /**
     * 设置轮播数据集
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        hasInit = false;
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                int first = mLayoutManager.getCurrentPosition();
                if (mCurrentIndex != first) {
                    mCurrentIndex = first;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    setPlaying(true);
                    refreshIndicator(mCurrentIndex);
                    if (mIndexChangedListener != null) {
                        mIndexChangedListener.onDataCallback(mCurrentIndex);
                    }
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dx != 0) {
                    setPlaying(false);
                }
            }
        });
        hasInit = true;
    }

    public void notifyDataSetChanged() {
        if (hasInit) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null) {
                mBannerSize = adapter.getItemCount();
                boolean onlyOne = mBannerSize > 1;
                this.allowAutoPlaying = onlyOne;
                mLayoutManager.setInfinite(onlyOne);
                adapter.notifyDataSetChanged();
                mIndicatorAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 改变导航的指示点
     */
    protected synchronized void refreshIndicator(int position) {
        if (mBannerSize > 1) {
            mIndicatorAdapter.setCurrentPos(position % mBannerSize);
            mIndicatorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            setPlaying(true);
        } else {
            setPlaying(false);
        }
    }

    private class IndicatorAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        int mCurrentPos = 0;

        public void setCurrentPos(int currentPos) {
            mCurrentPos = currentPos;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView indicator = new ImageView(getContext());
            indicator.setPadding(mIndicatorMargin, 0, mIndicatorMargin, 0);
            return RecyclerViewHolder.createViewHolder(indicator);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            ImageView indicator = (ImageView) holder.itemView;
            indicator.setImageDrawable(mCurrentPos == position ? mSelectedDrawable : mUnselectedDrawable);
        }

        @Override
        public int getItemCount() {
            return mBannerSize;
        }
    }
}
