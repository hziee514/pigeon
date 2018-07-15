package cn.wrh.smart.dove.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.base.Preconditions;

import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.view.snippet.DividerDecoration;
import cn.wrh.smart.dove.view.snippet.ItemViewHolder;
import cn.wrh.smart.dove.view.snippet.MainAdapter;
import cn.wrh.smart.dove.view.snippet.OnItemClickListener;
import cn.wrh.smart.dove.view.snippet.OnItemLongClickListener;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListDelegate extends AbstractViewDelegate {

    private RecyclerView list;

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_task;
    }

    @Override
    public void onInit() {
        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.addItemDecoration(new DividerDecoration(getResources()));
    }

    public MainAdapter newAdapter(final List<Object> data,
                                  final OnItemClickListener listener,
                                  final OnItemLongClickListener longClickListener) {
        MainAdapter adapter = new MainAdapter(getActivity(), data)
                .setFactory(new TaskViewHolderFactory())
                .setItemClickListener(listener)
                .setItemLongClickListener(longClickListener);
        list.setAdapter(adapter);
        return adapter;
    }

    public void showResetConfirm(final Runnable runnable) {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.reset_confirm)
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    runnable.run();
                })
                .show();
    }

    public void showFilterDialog(int selected, final DialogInterface.OnClickListener clickListener) {
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.task_filters, selected, (dialog, which) -> {
                    dialog.dismiss();
                    clickListener.onClick(dialog, which);
                })
                .show();
    }

    public void showActionDialog(DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity())
                .setItems(R.array.task_actions, (dialog, which) -> {
                    dialog.dismiss();
                    listener.onClick(dialog, which);
                })
                .show();
    }

    static class TaskViewHolder extends ItemViewHolder {

        TaskViewHolder(View itemView) {
            super(itemView);
            image1.setVisibility(View.VISIBLE);
        }

        @Override
        protected void bind(Object o) {
            super.bind(o);
            Preconditions.checkArgument(o instanceof TaskBO,
                    "Invalid type: " + o.getClass().getSimpleName());
            final TaskBO bo = (TaskBO)o;
            setName(bo.getCageSn());
            setType(bo.getType());
            setStatus(bo.getStatus());
            setDisclosure(bo.getEggId());
        }

        private void setType(TaskModel.Type type) {
            final String[] TYPES = itemView.getResources().getStringArray(R.array.task_types);
            text2.setText(TYPES[type.ordinal()]);
        }

        private void setStatus(TaskModel.Status status) {
            if (status == TaskModel.Status.Waiting) {
                image1.setImageResource(R.drawable.ic_task_waiting);
            } else {
                image1.setImageResource(R.drawable.ic_task_finished);
            }
        }

        private void setDisclosure(int eggId) {
            image2.setVisibility(eggId > 0 ? View.VISIBLE : View.GONE);
        }

    }

    static class TaskViewHolderFactory implements MainAdapter.ItemViewHolderFactory {
        @Override
        public ItemViewHolder create(View view) {
            return new TaskViewHolder(view);
        }
    }

}
