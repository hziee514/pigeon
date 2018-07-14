package cn.wrh.smart.dove.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Arrays;

import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.mvp.CompatActivityPresenter;
import cn.wrh.smart.dove.mvp.ViewDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public abstract class BaseActivity<T extends ViewDelegate> extends CompatActivityPresenter<T> {

    private static final String TAG = "BaseActivity";

    private static final int REQ_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new Handler().postDelayed(this::checkSelfPermissions, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult: " + requestCode
                + ", " + Arrays.toString(permissions)
                + ", " + Arrays.toString(grantResults));

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
            return;
        }

        switch (requestCode) {
            case REQ_WRITE_EXTERNAL_STORAGE:
                onPermissionsGranted();
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onPermissionsGranted() {
        Log.v(TAG, "onPermissionsGranted");
    }

    private void checkSelfPermissions() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQ_WRITE_EXTERNAL_STORAGE)) {
            onPermissionsGranted();
        }
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.v(TAG, "checkSelfPermission: " + permission + ", " + requestCode);
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {permission}, requestCode);
            return false;
        }
        return true;
    }

    protected void toast(@StringRes int resId) {
        Snackbar.make(getViewDelegate().getRootView(), resId, Snackbar.LENGTH_SHORT).show();
    }

    protected AppDatabase getDatabase() {
        return AppDatabase.getInstance(getApplicationContext());
    }

}
