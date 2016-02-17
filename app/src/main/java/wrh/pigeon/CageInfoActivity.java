package wrh.pigeon;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CageInfoActivity extends Activity implements OnMyItemClickLisener {

    public class CageHistoryListAdapter extends MyExpandableListAdapter{
        public CageHistoryListAdapter(Context context,
                               List<? extends Map<String, ?>> groupData,
                               int groupLayout,
                               String[] groupFrom,
                               int[] groupTo,
                               List<? extends List<? extends Map<String, ?>>> childData,
                               int childLayout,
                               String[] childFrom,
                               int[] childTo,
                               OnMyItemClickLisener listener) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo, listener);
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            Map<String, String> r = store_.get(groupPosition).get(childPosition);
            TextView textView = (TextView)view.findViewById(R.id.text2);
            if ("0".equals(r.get("stage"))){
                textView.setText(R.string.lay_egg);
            } else if ("1".equals(r.get("stage"))) {
                textView.setText(R.string.hatch_egg);
            } else if ("2".equals(r.get("stage"))) {
                textView.setText(R.string.sell_egg);
                textView.setTextColor(Color.GREEN);
            }
            return view;
        }
    }

    private Boolean initing_ = true;
    private CageHistoryListAdapter adapter_;
    private List<List<Map<String, String>>> store_ = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_cageinfo);

        Intent intent = getIntent();

        final String cage_id = intent.getStringExtra("id");
        final DbManager dbm = ((MyApplication)getApplication()).getDbManager();

        EditText editText = (EditText)findViewById(R.id.cagesn);
        editText.setText(intent.getStringExtra("sn"));

        Spinner statusSpinner = (Spinner)findViewById(R.id.status);
        statusSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.cage_status)));
        statusSpinner.setSelection(Integer.parseInt(intent.getStringExtra("status")));
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initing_) {
                    initing_ = false;
                    return;
                }
                dbm.updateCage(cage_id, (int) id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        List<Map<String, String>> group_data = new ArrayList<Map<String, String>>();
        store_ = new ArrayList<List<Map<String, String>>>();

        Map<String, String> group_info = new HashMap<String, String>();
        List<Map<String, String>> children = dbm.getCageHistory(intent.getStringExtra("sn"));
        store_.add(children);
        group_info.put("name", getResources().getString(R.string.txt_feed_record) + "(" + children.size() + ")");
        group_data.add(group_info);

        ExpandableListView listView = (ExpandableListView)findViewById(R.id.history);
        adapter_ = new CageHistoryListAdapter(
                this,
                group_data,
                R.layout.expandablelist_group,
                new String[] { "name" },
                new int[] { R.id.group_name },
                store_,
                R.layout.expandablelist_item,
                new String[] { "dt", "stage" },
                new int[] { R.id.text1, R.id.text2 },
                this
        );
        listView.setAdapter(adapter_);
        listView.expandGroup(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,R.string.delete).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                doDelete();
                break;
        }
        return true;
    }

    private void doDelete(){
        final Intent intent = getIntent();
        final CageInfoActivity that = this;
        final DbManager dbm = ((MyApplication)getApplication()).getDbManager();

        new AlertDialog.Builder(CageInfoActivity.this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.msg_del_cage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbm.deleteCage(intent.getStringExtra("id"));
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
    }

    @Override
    public void onMyItemClick(View view, int groupPosition, int childPosition) {

    }

    @Override
    public void onMyItemLongClick(View view, int groupPosition, int childPosition) {

    }

    @Override
    public void onMyItemDisclosure(View view, int groupPosition, int childPosition) {

    }
}
