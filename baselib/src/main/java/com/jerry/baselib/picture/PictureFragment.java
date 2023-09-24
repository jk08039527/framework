package com.jerry.baselib.picture;

import android.view.View;

import com.bumptech.glide.Glide;
import com.jerry.baselib.base.BaseFragment;
import com.jerry.baselib.R;
import com.jerry.baselib.weidgt.DragPhotoView;


/**
 * @author Jerry
 * @createDate 2020-01-08
 * @copyright www.axiang.com
 * @description 承载图片，包含图片总数和索引
 */
public class PictureFragment extends BaseFragment {

    /**
     * 图片链接
     */
    private String url;

    public static PictureFragment newInstance(final String url) {
        PictureFragment pictureFragment = new PictureFragment();
        pictureFragment.url = url;
        return pictureFragment;
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.img_browse;
    }

    @Override
    protected void initView(final View view) {
        DragPhotoView photoView = view.findViewById(R.id.mPhoneview);
        //必须添加一个onExitListener,在拖拽到底部时触发.
        Glide.with(this).load(url).into(photoView);
    }

    @Override
    public void onClick(final View v) {
    }
}
