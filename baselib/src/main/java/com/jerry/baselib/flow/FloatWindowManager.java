package com.jerry.baselib.flow;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import com.jerry.baselib.App;
import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.flow.FloatMenuView.SimpleMenuClickListener;
import com.jerry.baselib.R;
import com.jerry.baselib.impl.OnItemClickListener;

/**
 * @author Jerry
 * @createDate 2023/6/16
 * @copyright www.axiang.com
 * @description
 */
public class FloatWindowManager {

    private final FloatItem configItem = new FloatItem("配置", -0x67000000, -0x67000000,
        BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.play), "0");
    private final FloatItem stopItem = new FloatItem("暂停", -0x67000000, -0x67000000,
        BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.pause), "0");
    private final ArrayList<FloatItem> itemList = new ArrayList<>();
    private final FloatLogoMenu menu;
    private OnItemClickListener<String> mOnItemClickListener;

    private static volatile FloatWindowManager sWindowManager;

    public static FloatWindowManager getInstance() {
        if (sWindowManager == null) {
            synchronized (FloatWindowManager.class) {
                if (sWindowManager == null) {
                    sWindowManager = new FloatWindowManager();
                }
            }
        }
        return sWindowManager;
    }

    public FloatWindowManager(){
        Context context = App.getInstance();
        itemList.clear();
        itemList.add(configItem);
        menu = new FloatLogoMenu.Builder()
            .withContext(context) //这个在7.0（包括7.0）以上以及大部分7.0以下的国产手机上需要用户授权，需要搭配<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
            .logo(BitmapFactory.decodeResource(context.getResources(), R.drawable.menu))
            .drawCicleMenuBg(true)
            .backMenuColor(-0x1b1c1f)
            .setBgDrawable(ContextCompat.getDrawable(context, R.drawable.yw_game_float_menu_bg)) //这个背景色需要和logo的背景色一致
            .addFloatItem(itemList)
            .defaultLocation(FloatLogoMenu.LEFT)
            .drawRedPointNum(false)
            .showWithListener(new SimpleMenuClickListener() {
                @Override
                public void onItemClick(final int position, final String title) {
                    super.onItemClick(position, title);
                    mOnItemClickListener.onItemClick(title, position);
                    BaseListenerService service = BaseListenerService.getInstance();
                    if (service.isPlaying) {
                        service.stop();
                    } else {
                        Intent intent = new Intent("android.accessibilityservice.AccessibilityService");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        service.startActivity(intent);
                    }
                }
            });
    }

    public void init(final OnItemClickListener<String> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void show() {
        if (menu != null) {
            menu.show();
        }
    }

    public void startScript() {
        itemList.clear();
        itemList.add(stopItem);
        menu.updateFloatItemList(itemList);
        menu.hide();
    }

    public void stopScript() {
        itemList.clear();
        itemList.add(configItem);
        menu.updateFloatItemList(itemList);
    }

}
