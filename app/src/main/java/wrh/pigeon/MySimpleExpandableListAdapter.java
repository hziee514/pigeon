package wrh.pigeon;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/9.
 */
public class MySimpleExpandableListAdapter extends SimpleExpandableListAdapter {

    private OnMyItemClickLisener listener_;

    public MySimpleExpandableListAdapter(Context context,
                                         List<? extends Map<String, ?>> groupData,
                                         int groupLayout,
                                         String[] groupFrom,
                                         int[] groupTo,
                                         List<? extends List<? extends Map<String, ?>>> childData,
                                         int childLayout,
                                         String[] childFrom,
                                         int[] childTo,
                                         OnMyItemClickLisener listener) {
        super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        listener_ = listener;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        view.setOnClickListener(new MyItemClickListener(groupPosition, childPosition));
        view.setOnLongClickListener(new MyItemLongClickListener(groupPosition, childPosition));
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(android.R.color.holo_blue_light);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundResource(android.R.color.transparent);
                        break;
                }
                return false;
            }
        });
        return view;
    }

    public class MyItemClickListener implements View.OnClickListener {

        private int groupPosition_;
        private int childPosition_;

        public MyItemClickListener(int groupPosition, int childPosition) {
            groupPosition_ = groupPosition;
            childPosition_ = childPosition;
        }

        @Override
        public void onClick(View v) {
            listener_.onMyItemClick(groupPosition_, childPosition_);
        }
    }

    public class MyItemLongClickListener implements View.OnLongClickListener {

        private int groupPosition_;
        private int childPosition_;

        public MyItemLongClickListener(int groupPosition, int childPosition) {
            groupPosition_ = groupPosition;
            childPosition_ = childPosition;
        }

        @Override
        public boolean onLongClick(View v) {
            listener_.onMyItemLongClick(groupPosition_, childPosition_);
            return true;
        }
    }

}
