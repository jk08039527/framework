package com.jerry.baselib.base;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jerry.baselib.R;

/**
 * @author Jerry
 * @createDate 2023/7/6
 * @copyright www.axiang.com
 * @description
 */
public abstract class BaseActivityWithFragment<T extends BaseFragment> extends BaseActivity {

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_framlayout;
    }

    @Override
    protected void initView() {
        FragmentManager fm = getSupportFragmentManager();
        T fg = fragmentNewInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_fragment, fg, fg.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    /**
     * 初始化BaseFragment
     */
    public abstract T fragmentNewInstance();
}
