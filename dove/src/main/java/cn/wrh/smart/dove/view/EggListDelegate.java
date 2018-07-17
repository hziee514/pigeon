package cn.wrh.smart.dove.view;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.function.IntConsumer;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.vo.EggVO;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class EggListDelegate extends AbstractListDelegate {

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_egg;
    }

    @Override
    protected MyExpandableListAdapter createAdapter(List<String> groups, List<List<Object>> data) {
        return new EggExpandableListAdapter(groups, data);
    }

    public void showGroupDialog(int selected, final IntConsumer consumer) {
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.egg_groups, selected, (dialog, which) -> {
                    dialog.dismiss();
                    consumer.accept(which);
                })
                .show();
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
