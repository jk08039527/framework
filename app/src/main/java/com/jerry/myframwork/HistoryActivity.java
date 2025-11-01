package com.jerry.myframwork;

import java.text.MessageFormat;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jerry.baselib.R;
import com.jerry.baselib.base.BaseRecyclerActivity;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.weidgt.NoticeDialog;
import com.jerry.myframwork.bean.RecordBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

/**
 * @author Jerry
 * @createDate 2025/5/22
 * @description
 */
public class HistoryActivity extends BaseRecyclerActivity<RecordBean> {

    @Override
    protected void initView() {
        super.initView();
        mPtrRecyclerView.canRefresh = false;
        setTitle(R.string.history);
        setRight(R.string.clear);
    }

    @Override
    protected BaseRecyclerAdapter<RecordBean> initAdapter() {
        return new BaseRecyclerAdapter<RecordBean>(this,mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final RecordBean bean) {
                TextView tvTitle = holder.getView(R.id.tv_title);
                tvTitle.setText(MessageFormat.format("{0}|{1}|{2}|{3}", bean.cardNum, bean.cardDate, bean.cardCvc, bean.cardAdd));
                switch (bean.handleStatus) {
                    case 1:
                        tvTitle.setTextColor(ContextCompat.getColor(HistoryActivity.this,R.color.green_primary));
                        break;
                    case -1:
                        tvTitle.setTextColor(ContextCompat.getColor(HistoryActivity.this,R.color.red_primary));
                        break;
                    default:
                        tvTitle.setTextColor(ContextCompat.getColor(HistoryActivity.this,R.color.second_text_color));
                        break;
                }
            }
        };
    }

    @Override
    protected void getData() {
        HistoryDataHelper.getInstance().init(result -> {
            mData.clear();
            mData.addAll(HistoryDataHelper.getInstance().getHistoryData());
            onAfterRefresh();
        });
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tv_right) {
            NoticeDialog dialog = new NoticeDialog(this);
            dialog.setMessage("清除后不能恢复，确认清除吗？");
            dialog.setPositiveListener(v1 -> {
                dialog.dismiss();
                MyDbManager.getInstance().deleteAll(RecordBean.class);
                HistoryDataHelper.getInstance().getHistoryData().clear();
                mData.clear();
                onAfterRefresh();
                toast("清除完毕");
            });
            dialog.show();
        }
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
