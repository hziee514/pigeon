package cn.wrh.smart.dove.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.base.Preconditions;

import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.view.snippet.DividerDecoration;
import cn.wrh.smart.dove.view.snippet.ItemViewHolder;
import cn.wrh.smart.dove.view.snippet.MainAdapter;
import cn.wrh.smart.dove.view.snippet.OnItemClickListener;

/**
 * @author bruce.wu
 * @date 2018/7/12
 */
public class CageListDelegate extends AbstractViewDelegate {

    private RecyclerView list;

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_cage;
    }

    @Override
    public void onInit() {
        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.addItemDecoration(new DividerDecoration(getResources()));
    }

    public MainAdapter newAdapter(final List<Object> data, final OnItemClickListener listener) {
        MainAdapter adapter = new MainAdapter(getActivity(), data)
                .setFactory(new CageViewHolderFactory())
                .setItemClickListener(listener);
        list.setAdapter(adapter);
        return adapter;
    }

    public void showFilterDialog(int selected, DialogInterface.OnClickListener clickListener) {
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.cage_filters, selected, (dialog, which) -> {
                    dialog.dismiss();
                    clickListener.onClick(dialog, which);
                })
                .show();
    }

    static class CageViewHolder extends ItemViewHolder {

        CageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(Object o) {
            super.bind(o);
            Preconditions.checkArgument(o instanceof  CageEntity,
                    "Invalid type: " + o.getClass().getSimpleName());
            final CageEntity entity = (CageEntity)o;
            setName(entity.getSerialNumber());
            setStatus(entity.getStatus());
        }

        private void setStatus(CageModel.Status status) {
            int color = Color.BLACK;
            if (status == CageModel.Status.Healthy) {
                color = Color.GREEN;
            } else if (status == CageModel.Status.Sickly) {
                color = Color.RED;
            }
            final String[] STATUS = itemView.getResources().getStringArray(R.array.cage_status);
            text2.setText(STATUS[status.ordinal()]);
            text2.setTextColor(color);
        }

    }

    static class CageViewHolderFactory implements MainAdapter.ItemViewHolderFactory {
        @Override
        public ItemViewHolder create(View view) {
            return new CageViewHolder(view);
        }
    }

}
