package wrh.pigeon;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wurenhai on 2016/1/11.
 */
public class TodayWorkListActivity extends ExpandableListActivity implements OnMyItemClickLisener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dlg_loading_ = new ProgressDialog(this);
        dlg_loading_.setMessage(getResources().getString(R.string.loading));
        dlg_loading_.setIndeterminate(true);
        dlg_loading_.setCancelable(false);

        refresh();
    }

    public class TodayWorkListAdapter extends MyExpandableListAdapter {

        public TodayWorkListAdapter(Context context,
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
            ImageView imageView = (ImageView)view.findViewById(R.id.img_status);
            imageView.setVisibility(View.VISIBLE);
            Map<String, String> record = works_.get(groupPosition).get(childPosition);
            if (record.get("fin_dt") == null || "".equals(record.get("fin_dt"))){
                imageView.setImageResource(android.R.drawable.checkbox_off_background);
            } else {
                imageView.setImageResource(android.R.drawable.checkbox_on_background);
            }
            TextView textView = (TextView)view.findViewById(R.id.text2);
            textView.setText(getWorkTypeRes(record.get("work_type")));
            ImageButton imageButton = (ImageButton)view.findViewById(R.id.btn_disclosure);
            if (record.get("egg_id") != null && !"".equals(record.get("egg_id"))){
                imageButton.setVisibility(View.VISIBLE);
            } else {
                imageButton.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    public static int getWorkTypeRes(String workType){
        if ("0".equals(workType)){
            return R.string.work_lay1;
        } else if ("1".equals(workType)){
            return R.string.work_lay2;
        } else if ("2".equals(workType)){
            return R.string.work_review;
        } else if ("3".equals(workType)){
            return R.string.work_hatch;
        } else if ("4".equals(workType)){
            return R.string.work_sell;
        } else {
            return R.string.work_lay1;
        }
    }

    private static final String LOG_NAME = "TodayWorkList";
    private TodayWorkListAdapter adapter_;
    private List<List<Map<String, String>>> works_ = null;

    private ProgressDialog dlg_loading_;

    public static final int FILTER_WAIT = 0;
    public static final int FILTER_FIN = 1;
    public static final int FILTER_ALL = 2;

    private int filter_status = FILTER_WAIT;

    public void filter(int which){
        filter_status = which;
        refresh();
    }

    public void refresh(){
        dlg_loading_.show();

        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        Map<String, List<Map<String, String>>> grouped = dbm.getGroupedWorks(filter_status);

        List<Map<String, String>> group_data = new ArrayList<Map<String, String>>();
        works_ = new ArrayList<List<Map<String, String>>>();

        for(Map.Entry<String, List<Map<String, String>>> groupEntry : grouped.entrySet()){
            Map<String, String> group_info = new HashMap<String, String>();
            List<Map<String, String>> children = groupEntry.getValue();
            group_info.put("name", groupEntry.getKey() + "(" + children.size() + ")");
            group_data.add(group_info);
            works_.add(children);
        }

        adapter_ = new TodayWorkListAdapter(
                this,
                group_data,
                R.layout.expandablelist_group,
                new String[] { "name" },
                new int[] { R.id.group_name },
                works_,
                R.layout.expandablelist_item,
                new String[] { "cage_sn", "work_type" },
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
    public void onMyItemClick(View view, int groupPosition, int childPosition) {
        Map<String, String> work = works_.get(groupPosition).get(childPosition);
        if (work.get("egg_id") == null || "".equals(work.get("egg_id"))) {
            return;
        }
        Intent intent = new Intent(TodayWorkListActivity.this, EggInfoActivity.class);
        intent.putExtra("id", work.get("egg_id"));
        intent.putExtra("cage_sn", work.get("cage_sn"));
        intent.putExtra("cage_id", work.get("cage_id"));
        startActivity(intent);
    }

    @Override
    public void onMyItemLongClick(View view, int groupPosition, int childPosition) {
        final Map<String, String> work = works_.get(groupPosition).get(childPosition);
        final String work_type = work.get("work_type");
        final DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        final ImageView imageView = (ImageView)view.findViewById(R.id.img_status);

        if (work.get("fin_dt") != null && !"".equals(work.get("fin_dt"))){
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String fin_dt = sdf.format(new Date());

        new AlertDialog.Builder(TodayWorkListActivity.this)
                .setItems(R.array.work_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //已经确检
                                if ("0".equals(work_type)) {
                                    if (dbm.addFirstEgg(work.get("id"), work.get("cage_id"))) {
                                        imageView.setImageResource(android.R.drawable.checkbox_on_background);
                                        work.put("fin_dt", fin_dt);
                                    }
                                } else if ("1".equals(work_type)) {
                                    if (dbm.addEgg(work.get("id"), work.get("egg_id"))) {
                                        imageView.setImageResource(android.R.drawable.checkbox_on_background);
                                        work.put("fin_dt", fin_dt);
                                    }
                                } else if ("2".equals(work_type)) {
                                    if (dbm.reviewEgg(work.get("id"), work.get("egg_id"))) {
                                        imageView.setImageResource(android.R.drawable.checkbox_on_background);
                                        work.put("fin_dt", fin_dt);
                                    }
                                } else if ("3".equals(work_type)) {
                                    if (dbm.hatchEgg(work.get("id"), work.get("egg_id"))) {
                                        imageView.setImageResource(android.R.drawable.checkbox_on_background);
                                        work.put("fin_dt", fin_dt);
                                    }
                                } else if ("4".equals(work_type)) {
                                    if (dbm.offEgg(work.get("id"), work.get("egg_id"))) {
                                        imageView.setImageResource(android.R.drawable.checkbox_on_background);
                                        work.put("fin_dt", fin_dt);
                                    }
                                }
                                break;
                            case 1: //明天在看
                                dbm.finWork(work.get("id"));
                                imageView.setImageResource(android.R.drawable.checkbox_on_background);
                                work.put("fin_dt", fin_dt);
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onMyItemDisclosure(View view, int groupPosition, int childPosition) {}
}
