package wrh.pigeon;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends TabActivity {

    private AlertDialog dlg_filter_cage_;
    private AlertDialog dlg_filter_work_;
    private AlertDialog dlg_group_feed_;
    private AlertDialog dlg_rebuild_works_;

    public static final int REQUEST_CODE = 0x0ba7c0de;
    private static final String SCAN_INTENT = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);

        setContentView(R.layout.activity_main);

        final MainActivity that = this;

        dlg_filter_cage_ = new AlertDialog.Builder(MainActivity.this)
                .setSingleChoiceItems(R.array.cage_filters, CageExpandableListActivity.FILTER_ALL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        that.filterCages(which);
                        dlg_filter_cage_.hide();
                    }
                })
                .create();

        dlg_filter_work_ = new AlertDialog.Builder(MainActivity.this)
                .setSingleChoiceItems(R.array.work_filters, TodayWorkListActivity.FILTER_WAIT, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        that.filterWorks(which);
                        dlg_filter_work_.hide();
                    }
                })
                .create();

        dlg_group_feed_ = new AlertDialog.Builder(MainActivity.this)
                .setSingleChoiceItems(R.array.feed_groups, FeedListActivity.GROUPBY_DATE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        that.groupFeeds(which);
                        dlg_group_feed_.hide();
                    }
                })
                .create();

        dlg_rebuild_works_ = new AlertDialog.Builder(MainActivity.this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.msg_rebuild_works)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MyApplication)getApplication()).getDbManager().rebuildTodayWorks();
                        getTabHost().setCurrentTab(2);
                        ((TodayWorkListActivity)getTabHost().getCurrentView().getContext()).refresh();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        final TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab_cage")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_dialog_dialer))
                .setContent(new Intent(this, CageExpandableListActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab_feed")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_menu_recent_history))
                .setContent(new Intent(this, FeedListActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab_work")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_menu_today))
                .setContent(new Intent(this, TodayWorkListActivity.class)));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            final MainActivity that = this;
            new AlertDialog.Builder(MainActivity.this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.msg_exit)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            that.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void setMenuIconEnable(Menu menu){
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setMenuIconEnable(menu);
        menu.add(0, 1, 1, R.string.filter).setIcon(android.R.drawable.ic_menu_more).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 2, 2, R.string.refresh).setIcon(android.R.drawable.ic_popup_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 3, 3, R.string.scan).setIcon(android.R.drawable.ic_menu_gallery).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 4, 4, R.string.add_cage).setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 5, 5, R.string.add_egg).setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 6, 6, R.string.backup).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 7, 7, R.string.rebuild_today_works).setIcon(android.R.drawable.ic_menu_day).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                onFilter();
                break;
            case 2:
                onRefresh();
                break;
            case 3:
                Intent intentScan = new Intent(SCAN_INTENT);
                intentScan.addCategory(Intent.CATEGORY_DEFAULT);
                intentScan.setPackage(getApplicationContext().getPackageName());
                startActivityForResult(intentScan, REQUEST_CODE);
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, AddCageActivity.class));
                break;
            case 5:
                startActivity(new Intent(MainActivity.this, AddEggActivity.class));
                break;
            case 6:
                ((MyApplication)getApplication()).getDbManager().backup();
                break;
            case 7:
                dlg_rebuild_works_.show();
                break;
            default:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                String result = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                handleBarcode(result);
            }
        }
    }

    private Pattern pattern_ = Pattern.compile("^[0-9]{2}-[0-9]{2}-[1-9][0-9]{2}$");

    private void handleBarcode(final String barcode){
        if (!pattern_.matcher(barcode).matches()){
            Toast.makeText(this, getResources().getString(R.string.msg_invalid_barcode) + barcode, Toast.LENGTH_SHORT).show();
            return;
        }

        final DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        Map<String, String> cage = dbm.getCageBySN(barcode);
        if (cage.size() == 0){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.msg_add_cage_to_manager)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            showCage(dbm.addCage(barcode));
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .create()
                    .show();
        } else {
            showCage(cage);
        }
    }

    private void showCage(Map<String, String> cage){
        //Toast.makeText(this, cage.get("sn"), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, CageInfoActivity.class);
        intent.putExtra("id", cage.get("id"));
        intent.putExtra("sn", cage.get("sn"));
        intent.putExtra("status", cage.get("status"));
        startActivity(intent);
    }

    protected void onFilter(){
        final TabHost tabHost = getTabHost();
        switch (tabHost.getCurrentTab()){
            case 0:
                dlg_filter_cage_.show();
                break;
            case 1:
                dlg_group_feed_.show();
                break;
            case 2:
                dlg_filter_work_.show();
                break;
        }
    }

    protected void filterCages(int which){
        ((CageExpandableListActivity)getTabHost().getCurrentView().getContext()).filter(which);
    }

    protected void filterWorks(int which){
        ((TodayWorkListActivity)getTabHost().getCurrentView().getContext()).filter(which);
    }

    protected void groupFeeds(int which){
        ((FeedListActivity)getTabHost().getCurrentView().getContext()).group(which);
    }

    protected void onRefresh(){
        final TabHost tabHost = getTabHost();
        switch (tabHost.getCurrentTab()){
            case 0:
                ((CageExpandableListActivity)getTabHost().getCurrentView().getContext()).refresh();
                break;
            case 1:
                ((FeedListActivity)getTabHost().getCurrentView().getContext()).refresh();
                break;
            case 2:
                ((TodayWorkListActivity)getTabHost().getCurrentView().getContext()).refresh();
                break;
        }
    }
}
