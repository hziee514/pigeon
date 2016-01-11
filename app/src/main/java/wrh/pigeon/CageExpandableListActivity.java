package wrh.pigeon;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;
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

    public class CageExpandableListAdapter extends MySimpleExpandableListAdapter{

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
            if (record.get("status").equals("1")){
                TextView textView = (TextView)view.findViewById(R.id.cage_status);
                textView.setTextColor(Color.GREEN);
            } else if (record.get("status").equals("2")) {
                TextView textView = (TextView)view.findViewById(R.id.cage_status);
                textView.setTextColor(Color.RED);
            }
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
                R.layout.cagelist_group,
                new String[] { "name" },
                new int[] { R.id.group_name },
                cages_,
                R.layout.cagelist_item,
                new String[] { "sn", "str_status" },
                new int[] { R.id.cage_sn, R.id.cage_status },
                this
        );
        setListAdapter(adapter_);

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
    public void onMyItemClick(int groupPosition, int childPosition) {
        Map<String, String> cage = cages_.get(groupPosition).get(childPosition);
        Toast.makeText(this, cage.get("sn"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMyItemLongClick(int groupPosition, int childPosition) {
        Map<String, String> cage = cages_.get(groupPosition).get(childPosition);
        Toast.makeText(this, cage.get("sn"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMyItemDisclosure(int groupPosition, int childPosition) {
    }

}
