package com.jerry.baselib.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jerry.baselib.util.AppUtils;
import com.jerry.baselib.util.CollectionUtils;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    protected Context mContext;
    protected List<T> mData;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    public static final int TYPE_DATA = 0;
    public static final int TYPE_STICKY_HEAD = 1;
    private View mHeaderView;
    private View mFooterView;

    public BaseRecyclerAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<>() : data;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == BASE_ITEM_TYPE_HEADER && mHeaderView != null) {
            return RecyclerViewHolder.createViewHolder(mHeaderView);
        }
        if (viewType == BASE_ITEM_TYPE_FOOTER && mFooterView != null) {
            return RecyclerViewHolder.createViewHolder(mFooterView);
        }
        final RecyclerViewHolder holder = RecyclerViewHolder.createViewHolder(LayoutInflater.from(mContext)
            .inflate(getItemLayoutId(viewType), viewGroup, false));
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                if (AppUtils.isFastDoubleClick()) {
                    return;
                }
                if (holder.getLayoutPosition() - getHeadersCount() < mData.size()) {
                    mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition() - getHeadersCount());
                } else {
                    notifyDataSetChanged();
                }
            });
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition() - getHeadersCount());
                return true;
            });
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return BASE_ITEM_TYPE_HEADER;
        } else if (isFooterViewPos(position)) {
            return BASE_ITEM_TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            return;
        }
        int realPosition = position - getHeadersCount();
        if (CollectionUtils.isItemInCollection(realPosition, mData)) {
            convert(holder, realPosition, getItemViewType(position), mData.get(realPosition));
        }
    }

    public abstract int getItemLayoutId(int viewType);

    public abstract void convert(RecyclerViewHolder holder, int position, int viewType, T bean);

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    public int getRealItemCount() {
        return mData.size();
    }

    public void add(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void delete(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        delete(position);
    }

    public void swap(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public T getItemAtPosition(int position) {
        return (mData == null || position < 0 || position >= mData.size()) ? null : mData.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(View itemView, int position);
    }

    protected boolean isHeaderViewPos(int position) {
        return position == 0 && mHeaderView != null;
    }

    protected boolean isFooterViewPos(int position) {
        return (position == mData.size() && mHeaderView == null) || position > mData.size();
    }

    public void addHeaderView(View view) {
        mHeaderView = view;
    }

    public void addFooterView(View view) {
        mFooterView = view;
    }

    public void removeFooterView() {
        mFooterView = null;
    }

    public int getHeadersCount() {
        return mHeaderView == null ? 0 : 1;
    }

    public int getFootersCount() {
        return mFooterView == null ? 0 : 1;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        }
    }

}