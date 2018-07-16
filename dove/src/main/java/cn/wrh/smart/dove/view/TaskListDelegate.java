package cn.wrh.smart.dove.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.wrh.smart.dove.AppExecutors;
import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListDelegate extends AbstractViewDelegate {

    private ExpandableListView list;
    private MyExpandableListAdapter adapter;
    private ActionListener actionListener;

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_expandable_listview;
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_task;
    }

    @Override
    public void onInit() {
        list = findViewById(R.id.list);
    }

    public void setupList(final List<String> groups, List<List<Object>> data) {
        adapter = new TaskExpandableListAdapter(groups, data);
        list.setAdapter(adapter);
    }

    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    public void updateList() {
        adapter.notifyDataSetInvalidated();
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

    public interface ActionListener {

        void onConfirm(int groupPosition, int childPosition);

        void onFinish(int groupPosition, int childPosition);

    }

    class TaskExpandableListAdapter extends MyExpandableListAdapter {

        TaskExpandableListAdapter(List<String> groups, List<List<Object>> children) {
            super(getActivity(), groups, children);
        }

        @Override
        protected void bindChild(View view, int groupPosition, int childPosition) {
            TaskBO bo = (TaskBO)getChild(groupPosition, childPosition);
            setName(view, bo.getCageSn());
            setType(view, bo.getType());
            setStatus(view, bo.getStatus());
            setDisclosure(view, bo.getEggId());
        }

        @Override
        protected void onItemClick(View view, int groupPosition, int childPosition) {
            Toast.makeText(getActivity(), "onItemClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected boolean onItemLongClick(View view, int groupPosition, int childPosition) {
            new AlertDialog.Builder(getActivity())
                    .setItems(R.array.task_actions, (dialog, which) -> {
                        dialog.dismiss();
                        if (actionListener == null) {
                            return;
                        }
                        Runnable runnable = () -> finishTask(view, groupPosition, childPosition);
                        onAction(which, groupPosition, childPosition, runnable);
                    })
                    .show();
            return true;
        }

        private void onAction(int which, int groupPosition, int childPosition, Runnable runnable) {
            AppExecutors executors = AppExecutors.getInstance();
            switch (which) {
                case 0: //已经确认
                    executors.diskIO(() -> {
                        actionListener.onConfirm(groupPosition, childPosition);
                        runnable.run();
                    });
                case 1: //明天在看
                    executors.diskIO(() -> {
                        actionListener.onFinish(groupPosition, childPosition);
                        runnable.run();
                    });
                    break;
            }
        }

        private void finishTask(View view, int groupPosition, int childPosition) {
            AppExecutors.getInstance().mainThread(() -> bindChild(view, groupPosition, childPosition));
        }

        private void setName(View view, String name) {
            ((TextView)view.findViewById(R.id.text1)).setText(name);
        }

        private void setType(View view, TaskModel.Type type) {
            String[] types = view.getResources().getStringArray(R.array.task_types);
            ((TextView)view.findViewById(R.id.text2)).setText(types[type.ordinal()]);
        }

        private void setStatus(View view, TaskModel.Status status) {
            ImageView image1 = view.findViewById(R.id.image1);
            image1.setVisibility(View.VISIBLE);
            image1.setImageResource(status == TaskModel.Status.Waiting
                    ? R.drawable.ic_task_waiting : R.drawable.ic_task_finished);
        }

        private void setDisclosure(View view, int eggId) {
            ImageView image2 = view.findViewById(R.id.image2);
            image2.setVisibility(eggId > 0 ? View.VISIBLE : View.INVISIBLE);
        }

    }

}
