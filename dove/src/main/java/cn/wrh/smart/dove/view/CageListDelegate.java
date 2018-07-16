package cn.wrh.smart.dove.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/12
 */
public class CageListDelegate extends AbstractListDelegate {

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_cage;
    }

    @Override
    protected MyExpandableListAdapter createAdapter(List<String> groups, List<List<Object>> data) {
        return new CageExpandableListAdapter(groups, data);
    }

    public void showFilterDialog(int selected, DialogInterface.OnClickListener clickListener) {
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.cage_filters, selected, (dialog, which) -> {
                    dialog.dismiss();
                    clickListener.onClick(dialog, which);
                })
                .show();
    }

    class CageExpandableListAdapter extends MyExpandableListAdapter {

        CageExpandableListAdapter(List<String> groups, List<List<Object>> children) {
            super(getActivity(), groups, children);
        }

        @Override
        protected void bindChild(View view, int groupPosition, int childPosition) {
            CageEntity entity = (CageEntity)getChild(groupPosition, childPosition);
            setName(view, entity.getSerialNumber());
            setStatus(view, entity.getStatus());
        }

        private void setName(View view, String name) {
            ((TextView)view.findViewById(R.id.text1)).setText(name);
        }

        private void setStatus(View view, CageModel.Status status) {
            int color = Color.BLACK;
            if (status == CageModel.Status.Healthy) {
                color = Color.GREEN;
            } else if (status == CageModel.Status.Sickly) {
                color = Color.RED;
            }
            String[] array = view.getResources().getStringArray(R.array.cage_status);
            TextView text2 = view.findViewById(R.id.text2);
            text2.setText(array[status.ordinal()]);
            text2.setTextColor(color);
        }
    }

}
