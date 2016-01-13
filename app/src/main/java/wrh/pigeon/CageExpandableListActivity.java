package wrh.pigeon;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/9.
 */
public class CageExpandableListActivity extends ExpandableListActivity implements OnMyItemClickLisener {

    public class CageExpandableListAdapter extends MyExpandableListAdapter {

        public CageExpandableListAdapter(Context context,
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
            View  view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            Map<String, String> record = cages_.get(groupPosition).get(childPosition);
            TextView textView = (TextView)view.findViewById(R.id.text2);
            if (record.get("status").equals("1")){
                textView.setTextColor(Color.GREEN);
            } else if (record.get("status").equals("2")) {
                textView.setTextColor(Color.RED);
            }
            textView.setText(getCageStatusRes(record.get("status")));
            return view;
        }
    }

    private static final String LOG_NAME = "CageExpandableList";

    private CageExpandableListAdapter adapter_;

    private List<List<Map<String, String>>> cages_ = null;

    private ProgressDialog dlg_loading_;

    public static final int FILTER_IDLE = 0;
    public static final int FILTER_HEALTHLY = 1;
    public static final int FILTER_SICK = 2;
    public static final int FILTER_ALL = 3;

    private int filter_status = FILTER_ALL;

    public static int getCageStatusRes(String status){
        if ("0".equals(status)) {
            return R.string.cage_idle;
        } else if ("1".equals(status)) {
            return R.string.cage_healthly;
        } else if ("2".equals(status)) {
            return R.string.cage_sick;
        } else {
            return R.string.cage_idle;
        }
    }

    public void filter(int which){
        filter_status = which;
        refresh();
    }

    public void refresh(){
        dlg_loading_.show();

        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        Map<String, List<Map<String, String>>> grouped = dbm.getGroupedCages(filter_status);

        List<Map<String, String>> group_data = new ArrayList<Map<String, String>>();
        cages_ = new ArrayList<List<Map<String, String>>>();

        for(Map.Entry<String, List<Map<String, String>>> groupEntry : grouped.entrySet()){
            Map<String, String> group_info = new HashMap<String, String>();
            group_info.put("name", groupEntry.getKey());
            group_data.add(group_info);
            List<Map<String, String>> children = groupEntry.getValue();
            cages_.add(children);
        }

        adapter_ = new CageExpandableListAdapter(
                this,
                group_data,
                R.layout.expandablelist_group,
                new String[] { "name" },
                new int[] { R.id.group_name },
                cages_,
                R.layout.expandablelist_item,
                new String[] { "sn", "status" },
                new int[] { R.id.text1, R.id.text2 },
                this
        );
        setListAdapter(adapter_);

        if (group_data.size() > 0) {
            getExpandableListView().expandGroup(0);
        }

        dlg_loading_.hide();

        Log.d(LOG_NAME, "refreshed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dlg_loading_ = new ProgressDialog(this);
        dlg_loading_.setMessage(getResources().getString(R.string.loading));
        dlg_loading_.setIndeterminate(true);
        dlg_loading_.setCancelable(false);
        refresh();
    }

    @Override
    public void onMyItemClick(View view, int groupPosition, int childPosition) {
        Map<String, String> cage = cages_.get(groupPosition).get(childPosition);

        Intent intent = new Intent(CageExpandableListActivity.this, CageInfoActivity.class);
        intent.putExtra("id", cage.get("id"));
        intent.putExtra("sn", cage.get("sn"));
        intent.putExtra("status", cage.get("status"));
        startActivity(intent);
    }

    @Override
    public void onMyItemLongClick(View view, int groupPosition, int childPosition) {
        //Map<String, String> cage = cages_.get(groupPosition).get(childPosition);
        //Toast.makeText(this, cage.get("sn"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMyItemDisclosure(View view, int groupPosition, int childPosition) {
    }

}
