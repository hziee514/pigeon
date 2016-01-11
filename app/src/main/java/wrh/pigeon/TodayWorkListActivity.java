package wrh.pigeon;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public class TodayWorkListAdapter extends MySimpleExpandableListAdapter{

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
            if (record.get("fin_dt") == null && "".equals(record.get("fin_dt"))){
                imageView.setImageResource(android.R.drawable.checkbox_off_background);
            } else {
                imageView.setImageResource(android.R.drawable.checkbox_on_background);
            }
            return view;
        }
    }

    private static final String LOG_NAME = "TodayWorkList";
    private TodayWorkListAdapter adapter_;
    private List<List<Map<String, String>>> works_ = null;
    private ProgressDialog dlg_loading_;

    public void refresh(){
        Log.d(LOG_NAME, "refreshed");
    }

    @Override
    public void onMyItemClick(int groupPosition, int childPosition) {

    }

    @Override
    public void onMyItemLongClick(int groupPosition, int childPosition) {

    }

    @Override
    public void onMyItemDisclosure(int groupPosition, int childPosition) {

    }
}
