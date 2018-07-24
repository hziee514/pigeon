package cn.wrh.smart.dove.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.event.RestoreCompleted;
import cn.wrh.smart.dove.util.BackupManager;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.view.MainDelegate;

public class MainActivity extends BaseActivity<MainDelegate>
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

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
            case R.id.nav_backup:
                onBackup();
                break;
            case R.id.nav_restore:
                onRestore();
                break;
            case R.id.nav_settings:
                toast(R.string.not_implemented);
                break;
        }
        getViewDelegate().closeDrawer();
        return true;
    }

    @Override
    protected void onIntentReceived(Intent intent) {
        if (!Intent.ACTION_VIEW.equals(intent.getAction())) {
            return;
        }
        if (intent.getData() == null) {
            return;
        }
        Log.i(TAG, intent.getData().getLastPathSegment());
    }

    private void onBackup() {
        getViewDelegate().showWarnDialog(this::doBackup,
                R.string.backup_warn_message, BackupManager.getCandidate());
    }

    private void doBackup() {
        new BackupManager(getDatabase())
                .backup(this::onBackupCompleted, this::onBackupError);
    }

    private void onBackupCompleted(Tuple<Integer, Integer> result) {
        Log.i(TAG, "" + result.getFirst() + ", " + result.getSecond());
        getViewDelegate().showResultDialog(R.string.backup_complete_message,
                result.getFirst(), result.getSecond());
    }

    private void onBackupError(Throwable tr) {
        Log.e(TAG, "backup error", tr);
        getViewDelegate().showResultDialog(R.string.backup_error_message, tr.getMessage());
    }

    private void onRestore() {
        getViewDelegate().showWarnDialog(this::doRestore,
                R.string.restore_warn_message, BackupManager.getCandidate());
    }

    private void doRestore() {
        new BackupManager(getDatabase())
                .restore(this::onRestoreCompleted, this::onRestoreError);
    }

    public void onRestoreCompleted(Tuple<Integer, Integer> result) {
        Log.i(TAG, "" + result.getFirst() + ", " + result.getSecond());
        getViewDelegate().showResultDialog(R.string.restore_complete_message,
                result.getFirst(), result.getSecond());
        EventBus.getDefault().post(new RestoreCompleted());
    }

    private void onRestoreError(Throwable tr) {
        Log.e(TAG, "restore error", tr);
        getViewDelegate().showResultDialog(R.string.restore_error_message, tr.getMessage());
        EventBus.getDefault().post(new RestoreCompleted());
    }

}
