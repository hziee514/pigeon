package wrh.pigeon;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends TabActivity {

    private AlertDialog dlg_filter_cage_;

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

        final TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_dialog_dialer))
                .setContent(new Intent(this, CageExpandableListActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_menu_recent_history))
                .setContent(new Intent(this, MyExpandableListActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_menu_agenda))
                .setContent(new Intent(this, MyExpandableListActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(null, getResources().getDrawable(android.R.drawable.ic_menu_preferences))
                .setContent(new Intent(this, MyExpandableListActivity.class)));
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
        menu.add(0,1,1,R.string.filter).setIcon(android.R.drawable.ic_menu_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,2, 2, R.string.refresh).setIcon(android.R.drawable.ic_popup_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,3, 3, R.string.scan).setIcon(android.R.drawable.ic_menu_gallery).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0,4,4,R.string.add_cage).setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 5, 5, R.string.add_egg).setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case 1:
                onFilter();
                break;
            case 2:
                ((CageExpandableListActivity)getTabHost().getCurrentView().getContext()).refresh();
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, AddCageActivity.class));
                break;
            default:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    protected void onFilter(){
        final TabHost tabHost = getTabHost();
        switch (tabHost.getCurrentTab()){
            case 0:
                dlg_filter_cage_.show();
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    protected void filterCages(int which){
        final TabHost tabHost = getTabHost();
        CageExpandableListActivity activity = (CageExpandableListActivity)tabHost.getCurrentView().getContext();
        activity.filter(which);
    }
}
