package com.jerry.baselib.picture;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.jerry.baselib.base.BaseActivity;
import com.jerry.baselib.base.FragmentViewPagerAdapter;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;

/**
 * @author Jerry
 * @createDate 2019/3/26
 * @copyright www.axiang.com
 * @description 图片预览界面
 */
public class PictureActivity extends BaseActivity {

    /**
     * 图片链接列表
     */
    private List<String> urls;
    /**
     * 要显示的图片索引
     */
    private int index;

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_picture;
    }

    @Override
    protected void beforeViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        urls = bundle.getStringArrayList(Key.DATA);
        index = bundle.getInt(Key.INDEX);
    }

    @Override
    protected void initView() {
        if (CollectionUtils.isEmpty(urls)) {
            toast("暂无图片预览");
            return;
        }
        List<PictureFragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            fragmentList.add(PictureFragment.newInstance(urls.get(i)));
        }
        FragmentViewPagerAdapter<PictureFragment> adapter = new FragmentViewPagerAdapter<>(getSupportFragmentManager(), null, fragmentList);
        TextView tvNumber = findViewById(R.id.tv_phone);
        // ViewPager与tab标签
        ViewPager mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(index);
        tvNumber.setText(new StringBuilder().append(index + 1).append(" / ").append(urls.size()).toString());
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int i) {
                tvNumber.setText(new StringBuilder().append(i + 1).append(" / ").append(urls.size()).toString());
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(final View v) {
    }
}
