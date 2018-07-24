package cn.wrh.smart.dove.view;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListDelegate extends AbstractListDelegate {

    private Consumer<Tuple<Integer, Integer>> onItemClick;
    private Consumer<Tuple<Integer, Integer>> onTaskConfirm;
    private Consumer<Tuple<Integer, Integer>> onTaskFinish;

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_task;
    }

    public TaskListDelegate setOnItemClick(Consumer<Tuple<Integer, Integer>> listener) {
        this.onItemClick = listener;
        return this;
    }

    public TaskListDelegate setOnTaskConfirm(Consumer<Tuple<Integer, Integer>> listener) {
        this.onTaskConfirm = listener;
        return this;
    }

    public TaskListDelegate setOnTaskFinish(Consumer<Tuple<Integer, Integer>> listener) {
        this.onTaskFinish = listener;
        return this;
    }

    @Override
    protected MyExpandableListAdapter createAdapter(List<String> groups, List<List<Object>> data) {
        return new TaskExpandableListAdapter(groups, data);
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

    public void showFilterDialog(int selected, final IntConsumer consumer) {
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.task_filters, selected, (dialog, which) -> {
                    dialog.dismiss();
                    consumer.accept(which);
                })
                .show();
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
            if (onItemClick != null) {
                onItemClick.accept(new Tuple<>(groupPosition, childPosition));
            }
        }

        @Override
        protected boolean onItemLongClick(View view, int groupPosition, int childPosition) {
            new AlertDialog.Builder(getActivity())
                    .setItems(R.array.task_actions, (dialog, which) -> {
                        dialog.dismiss();
                        switch (which) {
                            case 0: //已经确认
                                doConfirm(groupPosition, childPosition);
                                break;
                            case 1: //明天在看
                                doFinish(groupPosition, childPosition);
                                break;
                        }
                    })
                    .show();
            return true;
        }

        private void doConfirm(int groupPosition, int childPosition) {
            if (onTaskConfirm != null) {
                onTaskConfirm.accept(new Tuple<>(groupPosition, childPosition));
            }
        }

        private void doFinish(int groupPosition, int childPosition) {
            if (onTaskFinish != null) {
                onTaskFinish.accept(new Tuple<>(groupPosition, childPosition));
            }
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
