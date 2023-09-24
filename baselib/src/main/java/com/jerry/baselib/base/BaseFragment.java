package com.jerry.baselib.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jerry.baselib.util.ToastUtil;

/**
 * @author Jerry
 * @createDate 2017/2/23
 * @copyright www.axiang.com
 * @description 基类Fragment，所有Fragment都要继承它
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected BaseActivity mActivity;
    protected boolean isCreateView;
    protected boolean isInViewpager;

    protected abstract int getContentViewResourceId();

    protected abstract void initView(View view);

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewResourceId(), null);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isCreateView = true;
        if (!isInViewpager || getUserVisibleHint()) {
            reload();
        }
    }

    /**
     * 短时间显示Toast提示
     *
     * @param resId 字符串资源Id
     */
    protected void toast(int resId) {
        ToastUtil.showShortText(resId);
    }

    /**
     * 长时间显示Toast提示
     *
     * @param s 字符串
     */
    protected void toast(String s) {
        ToastUtil.showShortText(s);
    }

    public boolean isHostFinishOrSelfDetach() {
        return getContext() == null || getActivity() == null || getActivity().isFinishing() || isDetached() || !isAdded();
    }

    public void loadingDialog() {
        if (isHostFinishOrSelfDetach()) {
            return;
        }
        mActivity.loadingDialog();
    }

    public void loadingDialog(String loadingText) {
        if (isHostFinishOrSelfDetach()) {
            return;
        }
        mActivity.loadingDialog(loadingText);
    }

    public void closeLoadingDialog() {
        if (isHostFinishOrSelfDetach()) {
            return;
        }
        try {
            mActivity.closeLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
    }

    protected int getColor(@ColorRes int colorRes) {
        if (getContext() != null) {
            return ContextCompat.getColor(getContext(), colorRes);
        }
        return 0x333333;
    }
}
