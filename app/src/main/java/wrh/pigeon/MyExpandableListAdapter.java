package wrh.pigeon;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/9.
 */
public class MyExpandableListAdapter extends SimpleExpandableListAdapter {

    private OnMyItemClickLisener listener_;

    public MyExpandableListAdapter(Context context,
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
        view.setOnClickListener(new MyItemClickListener(view, groupPosition, childPosition));
        view.setOnLongClickListener(new MyItemLongClickListener(view, groupPosition, childPosition));
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
        ImageButton button = (ImageButton)view.findViewById(R.id.btn_disclosure);
        button.setOnClickListener(new MyItemDisclosureListener(view, groupPosition, childPosition));
        return view;
    }

    public class MyItemClickListener implements View.OnClickListener {

        private View view_;
        private int groupPosition_;
        private int childPosition_;

        public MyItemClickListener(View view, int groupPosition, int childPosition) {
            view_ = view;
            groupPosition_ = groupPosition;
            childPosition_ = childPosition;
        }

        @Override
        public void onClick(View v) {
            listener_.onMyItemClick(view_, groupPosition_, childPosition_);
        }
    }

    public class MyItemLongClickListener implements View.OnLongClickListener {

        private View view_;
        private int groupPosition_;
        private int childPosition_;

        public MyItemLongClickListener(View view, int groupPosition, int childPosition) {
            view_ = view;
            groupPosition_ = groupPosition;
            childPosition_ = childPosition;
        }

        @Override
        public boolean onLongClick(View v) {
            listener_.onMyItemLongClick(view_, groupPosition_, childPosition_);
            return true;
        }
    }

    public class MyItemDisclosureListener implements View.OnClickListener{

        private View view_;
        private int groupPosition_;
        private int childPosition_;

        public MyItemDisclosureListener(View view, int groupPosition, int childPosition) {
            view_ = view;
            groupPosition_ = groupPosition;
            childPosition_ = childPosition;
        }

        @Override
        public void onClick(View v) {
            listener_.onMyItemDisclosure(view_, groupPosition_, childPosition_);
        }
    }

}
