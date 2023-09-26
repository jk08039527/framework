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
public abstract class BaseActivityWithFragment extends BaseActivity {

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_framlayout;
    }

    @Override
    protected void initView() {
        FragmentManager fm = getSupportFragmentManager();
        BaseFragment fg = fragmentNewInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_fragment, fg, fg.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    public abstract BaseFragment fragmentNewInstance();
}
