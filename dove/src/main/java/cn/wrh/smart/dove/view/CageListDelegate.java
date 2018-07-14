package cn.wrh.smart.dove.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.base.Preconditions;

import java.util.LinkedList;
import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.view.snippet.DividerDecoration;
import cn.wrh.smart.dove.view.snippet.ItemViewHolder;
import cn.wrh.smart.dove.view.snippet.MainAdapter;
import cn.wrh.smart.dove.view.snippet.OnItemClickListener;

/**
 * @author bruce.wu
 * @date 2018/7/12
 */
public class CageListDelegate extends AbstractViewDelegate implements OnItemClickListener {

    private final List<Object> data = new LinkedList<>();

    private RecyclerView list;
    private MainAdapter adapter;

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
        adapter = new MainAdapter(getActivity(), data)
                .setFactory(new CageViewHolderFactory());

        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.addItemDecoration(new DividerDecoration(getResources()));
        list.setAdapter(adapter);
    }

    public void setData(List<Object> data) {
        this.data.clear();
        this.data.addAll(data);
        this.adapter.notifyDataSetChanged();
    }

    public void showFilterDialog(int selected, DialogInterface.OnClickListener clickListener) {
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.cage_filters, selected, clickListener)
                .show();
    }

    @Override
    public void onItemClick(Object item) {

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

    }

    static class CageViewHolderFactory implements MainAdapter.ItemViewHolderFactory {
        @Override
        public ItemViewHolder create(View view) {
            return new CageViewHolder(view);
        }
    }

}
