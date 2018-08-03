package cn.wrh.smart.dove.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.event.RestoreCompleted;
import cn.wrh.smart.dove.storage.BackupFile;
import cn.wrh.smart.dove.storage.BackupRunner;
import cn.wrh.smart.dove.storage.Counter;
import cn.wrh.smart.dove.util.DateUtils;
import cn.wrh.smart.dove.view.MainDelegate;

public class MainActivity extends BaseActivity<MainDelegate>
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int REQ_CREATE_FOR_BACKUP = 1;
    private static final int REQ_OPEN_FOR_RESTORE = 2;

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        getViewDelegate().setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null || data.getData() == null) {
            return;
        }
        if (requestCode == REQ_CREATE_FOR_BACKUP) {
            doBackup(data.getData());
            return;
        }
        if (requestCode == REQ_OPEN_FOR_RESTORE) {
            alertForRestore(data.getData());
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        alertForRestore(intent.getData());
    }

    private void onBackup() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, getBackupFilename());
        startActivityForResult(intent, REQ_CREATE_FOR_BACKUP);
    }

    private String getBackupFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String timestamp = sdf.format(DateUtils.now());
        return String.format("dove.bak.%s", timestamp);
    }

    private void doBackup(Uri uri) {
        Log.i(TAG, "backup to: " + uri);
        BackupFile.Writer writer = null;
        try {
            getViewDelegate().showWaiting();
            writer = BackupFile.write(getContentResolver(), uri);
            new BackupRunner().backup(getDatabase(), writer, this::onBackupCompleted, this::onBackupError);
        } catch (Exception e) {
            onBackupError(e);
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void onBackupCompleted(Counter counter) {
        Log.i(TAG, "" + counter.getTotalCage() + ", " + counter.getTotalEgg());
        getViewDelegate().showResultDialog(R.string.backup_complete_message,
                counter.getTotalCage(), counter.getTotalEgg());
    }

    private void onBackupError(Throwable tr) {
        Log.e(TAG, "backup error", tr);
        getViewDelegate().showResultDialog(R.string.backup_error_message, tr.getMessage());
    }

    private void onRestore() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        startActivityForResult(intent, REQ_OPEN_FOR_RESTORE);
    }

    private void alertForRestore(Uri uri) {
        getViewDelegate().showWarnDialog(() -> doRestore(uri),
                R.string.restore_warn_message, uri.getLastPathSegment());
    }

    private void doRestore(Uri uri) {
        Log.i(TAG, "restore from: " + uri);
        BackupFile.Reader reader = null;
        try {
            reader = BackupFile.read(getContentResolver(), uri);
            Log.i(TAG, "restore meta: " + reader.getMeta().getTimestamp() + ", " + reader.getMeta().getVersion());
            new BackupRunner().restore(getDatabase(), reader, this::onRestoreCompleted, this::onRestoreError);
        } catch (Exception e) {
            onRestoreError(e);
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void onRestoreCompleted(Counter result) {
        Log.i(TAG, "" + result.getTotalCage() + ", " + result.getTotalEgg());
        getViewDelegate().showResultDialog(R.string.restore_complete_message,
                result.getTotalCage(), result.getTotalEgg());
        EventBus.getDefault().post(new RestoreCompleted());
    }

    private void onRestoreError(Throwable tr) {
        Log.e(TAG, "restore error", tr);
        getViewDelegate().showResultDialog(R.string.restore_error_message, tr.getMessage());
        EventBus.getDefault().post(new RestoreCompleted());
    }

}
