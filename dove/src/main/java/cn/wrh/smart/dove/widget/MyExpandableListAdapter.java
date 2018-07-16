package cn.wrh.smart.dove.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import cn.wrh.smart.dove.R;

/**
 * @author bruce.wu
 * @date 2018/7/16
 */
public abstract class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    private final List<String> groups;
    private final List<List<Object>> children;

    public MyExpandableListAdapter(Context context, List<String> groups, List<List<Object>> children) {
        this.inflater = LayoutInflater.from(context);
        this.groups = groups;
        this.children = children;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = (TextView) inflater.inflate(R.layout.li_expandable_group, parent, false);
        } else {
            view = (TextView)convertView;
        }
        view.setText(groups.get(groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.li_expandable_child, parent, false);
        } else {
            view = convertView;
        }
        view.setOnClickListener(v -> onItemClick(v, groupPosition, childPosition));
        view.setOnLongClickListener(v -> onItemLongClick(v, groupPosition, childPosition));
        bindChild(view, groupPosition, childPosition);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    protected void bindChild(View view, int groupPosition, int childPosition) {

    }

    protected void onItemClick(View view, int groupPosition, int childPosition) {

    }

    protected boolean onItemLongClick(View view, int groupPosition, int childPosition) {
        return false;
    }

}
