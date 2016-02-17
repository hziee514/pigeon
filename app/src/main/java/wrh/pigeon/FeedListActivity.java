package wrh.pigeon;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wurenhai on 2016/1/12.
 */
public class FeedListActivity extends ExpandableListActivity implements OnMyItemClickLisener {

    public class FeedListAdapter extends MyExpandableListAdapter{

        public FeedListAdapter(Context context,
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
            Map<String, String> record = store_.get(groupPosition).get(childPosition);
            if (groupby_type == GROUPBY_DATE) {
                TextView textView = (TextView)view.findViewById(R.id.text2);
                if ("0".equals(record.get("stage"))){
                    textView.setText(R.string.lay_egg);
                } else {
                    textView.setText(R.string.hatch_egg);
                }
            }
            return view;
        }
    }

    private static final String LOG_NAME = "FeedList";
    private FeedListAdapter adapter_;
    private List<List<Map<String, String>>> store_ = null;

    private ProgressDialog dlg_loading_;

    public static final int GROUPBY_STAGE = 0;
    public static final int GROUPBY_DATE = 1;

    private int groupby_type = GROUPBY_DATE;

    public void group(int which){
        groupby_type = which;
        refresh();
    }

    public void refresh(){
        dlg_loading_.show();

        String[] item_fields;

        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        Map<String, List<Map<String, String>>> grouped;
        if (groupby_type == GROUPBY_STAGE) {
            grouped = dbm.getGroupedFeedsByStage();
            item_fields = new String[]{"cage_sn", "dt"};
        } else {
            grouped = dbm.getGroupedFeedsByDate();
            item_fields = new String[]{"cage_sn", "stage"};
        }

        List<Map<String, String>> group_data = new ArrayList<Map<String, String>>();
        store_ = new ArrayList<List<Map<String, String>>>();

        for(Map.Entry<String, List<Map<String, String>>> groupEntry : grouped.entrySet()){
            Map<String, String> group_info = new HashMap<String, String>();
            List<Map<String, String>> children = groupEntry.getValue();
            group_info.put("name", groupEntry.getKey() + "(" + children.size() + ")");
            group_data.add(group_info);
            store_.add(children);
        }

        adapter_ = new FeedListAdapter(
                this,
                group_data,
                R.layout.expandablelist_group,
                new String[] { "name" },
                new int[] { R.id.group_name },
                store_,
                R.layout.expandablelist_item,
                item_fields,
                new int[] { R.id.text1, R.id.text2 },
                this
        );
        setListAdapter(adapter_);

        /*if (group_data.size() > 0) {
            getExpandableListView().expandGroup(0);
        }*/

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
        Map<String, String> egg = store_.get(groupPosition).get(childPosition);
        Intent intent = new Intent(FeedListActivity.this, EggInfoActivity.class);
        intent.putExtra("id", egg.get("id"));
        intent.putExtra("cage_sn", egg.get("cage_sn"));
        intent.putExtra("cage_id", egg.get("cage_id"));
        startActivity(intent);
    }

    @Override
    public void onMyItemLongClick(View view, int groupPosition, int childPosition) {

    }

    @Override
    public void onMyItemDisclosure(View view, int groupPosition, int childPosition) {

    }
}
