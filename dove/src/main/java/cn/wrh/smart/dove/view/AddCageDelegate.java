package cn.wrh.smart.dove.view;

import android.widget.ArrayAdapter;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/12
 */
public class AddCageDelegate extends AbstractViewDelegate {

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_add_cage;
    }

    @Override
    public void onInit() {
        getSpinner(R.id.room).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item, getResources().getStringArray(R.array.rooms)));

        getSpinner(R.id.group).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item,
                getResources().getStringArray(R.array.groups)));

        getSpinner(R.id.layer).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item,
                getResources().getStringArray(R.array.layers)));

        getSpinner(R.id.first).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item,
                getResources().getStringArray(R.array.sns)));

        getSpinner(R.id.last).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item,
                getResources().getStringArray(R.array.sns)));
        getSpinner(R.id.last).setSelection(12 - 1);

        getSpinner(R.id.status).setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.spinner_dropdown_item,
                getResources().getStringArray(R.array.cage_status)));
        getSpinner(R.id.status).setSelection(CageModel.Status.Healthy.ordinal());
    }

    public String getRoomId() {
        return getSpinner(R.id.room).getSelectedItem().toString();
    }

    public String getGroupId() {
        return getSpinner(R.id.group).getSelectedItem().toString();
    }

    public String getLayerId() {
        return getSpinner(R.id.layer).getSelectedItem().toString();
    }

    public int getFirst() {
        return getSpinner(R.id.first).getSelectedItemPosition() + 1;
    }

    public int getLast() {
        return getSpinner(R.id.last).getSelectedItemPosition() + 1;
    }

    public int getStatus() {
        return getSpinner(R.id.status).getSelectedItemPosition();
    }

}
