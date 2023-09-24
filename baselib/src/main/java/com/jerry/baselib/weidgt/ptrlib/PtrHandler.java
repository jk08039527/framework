package com.jerry.baselib.weidgt.ptrlib;

import android.view.View;

public interface PtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link com.jerry.baselib.weidgt.ptrlib.PtrDefaultHandler#checkContentCanBePulledDown}
     */
    boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header);

    /**
     * When refresh begin
     */
    void onRefreshBegin(PtrFrameLayout frame);
}