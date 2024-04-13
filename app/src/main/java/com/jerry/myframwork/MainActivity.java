package com.jerry.myframwork;

import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.asyctask.WhenTaskEnd;
import com.jerry.baselib.base.BaseRecyclerActivity;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.DateUtils;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.ParseUtil;
import com.jerry.myframwork.bean.MyCurrencyPair;
import com.jerry.myframwork.dbhelper.MyDbManager;

import io.gate.gateapi.ApiException;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.api.SpotApi.APIlistCandlesticksRequest;
import io.gate.gateapi.models.CurrencyPair;

public class MainActivity extends BaseRecyclerActivity<MyCurrencyPair> {

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected BaseRecyclerAdapter<MyCurrencyPair> initAdapter() {
        return new BaseRecyclerAdapter<MyCurrencyPair>(this, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final MyCurrencyPair bean) {
                TextView tvTitle = holder.getView(R.id.tv_title);
                TextView tvContent = holder.getView(R.id.tv_content);
                tvTitle.setText(bean.getId());
                tvContent.setText(DateUtils.getDateWTimesByLong(bean.getBuyStart() * 1000L));
            }
        };
    }

    @Override
    protected void getData() {
        AppTask.with(this).assign((BackgroundTask<List<MyCurrencyPair>>) () -> {
            try {
                SpotApi spotApi = new SpotApi();
                List<CurrencyPair> list = spotApi.listCurrencyPairs();
                List<MyCurrencyPair> myCurrencyPairs = JSON.parseArray(JSON.toJSONString(list), MyCurrencyPair.class);
                myCurrencyPairs.sort((o1, o2) -> Long.compare(o2.getBuyStart(), o1.getBuyStart()));
                return myCurrencyPairs;
            } catch (ApiException e) {
                LogUtils.e(e.getResponseBody());
            }
            return null;
        }).whenDone((WhenTaskDone<List<MyCurrencyPair>>) result -> {
            mData.clear();
            if (!CollectionUtils.isEmpty(result)) {
                mData.addAll(result);
            }
            onAfterRefresh();
        }).execute();
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onItemClick(final View itemView, final int position) {
        MyCurrencyPair currencyPair = mData.get(position);
        AppTask.with(this).assign((BackgroundTask<List<MyCurrencyPair>>) () -> {
            try {
                SpotApi spotApi = new SpotApi();
                APIlistCandlesticksRequest request = spotApi.listCandlesticks(currencyPair.getId())
                    .from(currencyPair.getBuyStart())
                    .limit(360).interval("10s");
                List<List<String>> results = request.execute();
                if (results.size() > 10) {
                    List<String> info10s = results.get(0);
                    List<String> info30s = results.get(2);
                    List<String> info1m = results.get(5);
                    currencyPair.new10s = ParseUtil.parse2Double(info10s.get(2));
                    currencyPair.new30s = ParseUtil.parse2Double(info30s.get(2));
                    currencyPair.new1m = ParseUtil.parse2Double(info1m.get(2));
                    MyDbManager.getInstance().insertOrReplaceObject(currencyPair);
                }
            } catch (ApiException e) {
                LogUtils.e(e.getResponseBody());
            }
            return null;
        }).whenDone((WhenTaskDone<List<CurrencyPair>>) result -> mAdapter.notifyItemChanged(position)).execute();
    }
}
