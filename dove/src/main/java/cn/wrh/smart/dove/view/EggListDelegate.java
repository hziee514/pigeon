package cn.wrh.smart.dove.view;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.vo.EggVO;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class EggListDelegate extends AbstractViewDelegate {

    private ExpandableListView list;
    private EggExpandableListAdapter adapter;

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
    }

    public void setupList(final List<String> groups, List<List<Object>> data) {
        adapter = new EggExpandableListAdapter(groups, data);
        list.setAdapter(adapter);
    }

    public void updateList() {
        adapter.notifyDataSetInvalidated();
    }

    class EggExpandableListAdapter extends MyExpandableListAdapter {

        EggExpandableListAdapter(List<String> groups, List<List<Object>> children) {
            super(getActivity(), groups, children);
        }

        @Override
        protected void bindChild(View view, int groupPosition, int childPosition) {
            EggVO vo = (EggVO) getChild(groupPosition, childPosition);
            setName(view, vo.getCageSn());
            if (vo.getGroupMethod() == EggVO.GROUP_DATE) {
                setStatus(view, vo.getStageText());
            } else {
                setStatus(view, vo.getDate());
            }
        }

        private void setName(View view, String name) {
            ((TextView)view.findViewById(R.id.text1)).setText(name);
        }

        private void setStatus(View view, String status) {
            ((TextView)view.findViewById(R.id.text2)).setText(status);
        }
    }

}
