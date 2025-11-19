package com.jerry.myframwork;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jerry.baselib.BuildConfig;
import com.jerry.baselib.R;
import com.jerry.baselib.base.BaseActivity;
import com.jerry.baselib.util.LogUtils;

public class MainActivity extends BaseActivity {

    private static final int CODE_READ = 1001;
    private static final int CODE_CAN_DRAW_OVERLAYS = 1002;
    private static final int CODE_WRITE_EXTERNAL_STORAGE = 1003;

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setTitle(R.string.home);
        findViewById(R.id.btn_read).setOnClickListener(this);
        findViewById(R.id.btn_export).setOnClickListener(this);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission.WRITE_EXTERNAL_STORAGE}, CODE_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent1.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            startActivityForResult(intent1, CODE_CAN_DRAW_OVERLAYS);
            Toast.makeText(this, "请打开悬浮窗权限", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.btn_read) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CODE_READ) {
            loadingDialog(getString(R.string.loading));
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意权限
                LogUtils.d("PERMISSION_GRANTED");
            } else {
                // 用户拒绝权限
                toast("存储权限被拒绝，无法导出文件");
            }
        }
    }
}
