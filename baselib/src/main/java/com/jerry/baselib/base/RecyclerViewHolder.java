package com.jerry.baselib.base;

import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    /**
     * 默认大小是10
     */
    private final SparseArray<View> views;

    private RecyclerViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
    }

    public static RecyclerViewHolder createViewHolder(View itemView) {
        return new RecyclerViewHolder(itemView);
    }

    /**
     * 获取view
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

}