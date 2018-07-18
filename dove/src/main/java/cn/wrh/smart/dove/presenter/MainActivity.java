package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.view.MainDelegate;

public class MainActivity extends BaseActivity<MainDelegate>
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        getViewDelegate().setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (getViewDelegate().checkOrCloseDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
            case R.id.nav_backup:
            case R.id.nav_restore:
                toast(R.string.not_implemented);
                break;
        }
        getViewDelegate().closeDrawer();
        return true;
    }



}
