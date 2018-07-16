package cn.wrh.smart.dove.view;

import android.widget.ExpandableListView;

import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/16
 */
public abstract class AbstractListDelegate extends AbstractViewDelegate {

    private ExpandableListView list;
    private MyExpandableListAdapter adapter;

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_expandable_listview;
    }

    @Override
    public void onInit() {
        list = findViewById(R.id.list);
    }

    public void setupList(final List<String> groups, List<List<Object>> data) {
        adapter = createAdapter(groups, data);
        list.setAdapter(adapter);
    }

    public void updateList() {
        adapter.notifyDataSetInvalidated();
    }

    protected abstract MyExpandableListAdapter createAdapter(final List<String> groups,
                                                             final List<List<Object>> data);

}
