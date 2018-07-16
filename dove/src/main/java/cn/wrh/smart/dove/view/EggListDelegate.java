package cn.wrh.smart.dove.view;

import android.content.Context;
import android.os.Handler;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class EggListDelegate extends AbstractViewDelegate {

    private ExpandableListView list;

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_expandable_listview;
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_egg;
    }

    @Override
    public void onInit() {
        list = findViewById(R.id.list);

        List<String> groups = new ArrayList<>(Arrays.asList("111", "222", "333"));
        Arrays.asList("111", "222", "333");
        List<List<Object>> children = new ArrayList<>(Arrays.asList(
                Arrays.asList("111-1", "111-2", "111-3"),
                Arrays.asList("222-1"),
                Arrays.asList("333-1", "333-2")
        ));
        BaseExpandableListAdapter adapter = new EggExpandableListAdapter(getActivity(), groups, children);
        list.setAdapter(adapter);

        new Handler().postDelayed(() -> {
            groups.add("444");
            children.add(Arrays.asList("444-1", "444-2"));
            adapter.notifyDataSetInvalidated();
        }, 2000);
    }

    static class EggExpandableListAdapter extends MyExpandableListAdapter {
        EggExpandableListAdapter(Context context, List<String> groups, List<List<Object>> children) {
            super(context, groups, children);
        }
    }

}
