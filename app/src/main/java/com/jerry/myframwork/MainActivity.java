package com.jerry.myframwork;

import java.util.List;

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
import com.jerry.myframwork.greendao.MyCurrencyPairDao.Properties;

import io.gate.gateapi.ApiException;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.api.SpotApi.APIlistTradesRequest;
import io.gate.gateapi.models.CurrencyPair;
import io.gate.gateapi.models.Trade;

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
                return R.layout.item_coin;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final MyCurrencyPair bean) {
                TextView tvTitle = holder.getView(R.id.tv_title);
                TextView tvStartTime = holder.getView(R.id.tv_start_time);
                TextView tvPrice1m = holder.getView(R.id.tv_price_1m);
                tvTitle.setText(bean.getId());
                tvStartTime.setText(DateUtils.getDateWTimesByLong(bean.getBuyStart() * 1000L));
                tvPrice1m.setText(String.valueOf(bean.getNewPrice()));
            }
        };
    }

    @Override
    protected void getData() {
        AppTask.with(this).assign((BackgroundTask<List<MyCurrencyPair>>) () -> {
            try {
                List<MyCurrencyPair> myCurrencyPairs = MyDbManager.getInstance().queryAll(MyCurrencyPair.class, null);
                if (myCurrencyPairs.size() < 1000) {
                    SpotApi spotApi = new SpotApi();
                    List<CurrencyPair> list = spotApi.listCurrencyPairs();
                    myCurrencyPairs = JSON.parseArray(JSON.toJSONString(list), MyCurrencyPair.class);
                    MyDbManager.getInstance().insertOrReplaceObjects(myCurrencyPairs);
                }
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
        }).whenTaskEnd(this::onAfterRefresh).execute();
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
                APIlistTradesRequest request = spotApi.listTrades(currencyPair.getId())
                    .from(currencyPair.getBuyStart())
                    .to(currencyPair.getBuyStart() + 5)
                    .limit(1000);
                currencyPair.newPrice = 0;
                List<Trade> results = request.execute();
                for (Trade result : results) {
                    currencyPair.newPrice += ParseUtil.parse2Double(result.getPrice());
                }
                currencyPair.newPrice = currencyPair.newPrice / results.size();
                MyDbManager.getInstance().insertOrReplaceObject(currencyPair);
            } catch (ApiException e) {
                LogUtils.e(e.getResponseBody());
            }
            return null;
        }).whenDone((WhenTaskDone<List<CurrencyPair>>) result -> mAdapter.notifyItemChanged(position)).execute();
    }
}
