package wrh.pigeon;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    }

    private static final String LOG_NAME = "FeedList";
    private FeedListAdapter adapter_;
    private List<List<Map<String, String>>> store_ = null;

    private ProgressDialog dlg_loading_;

    public void refresh(){
        dlg_loading_.show();
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

    }

    @Override
    public void onMyItemLongClick(View view, int groupPosition, int childPosition) {

    }

    @Override
    public void onMyItemDisclosure(View view, int groupPosition, int childPosition) {

    }
}
