package cn.wrh.smart.dove.view;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.function.IntConsumer;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.widget.MyExpandableListAdapter;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListDelegate extends AbstractListDelegate {

    private ActionListener actionListener;

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_task;
    }

    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
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
                        switch (which) {
                            case 0: //已经确认
                                doConfirm(view, groupPosition, childPosition);
                                break;
                            case 1: //明天在看
                                doFinish(view, groupPosition, childPosition);
                                break;
                        }
                    })
                    .show();
            return true;
        }

        private void doConfirm(View view, int groupPosition, int childPosition) {
            Flowable.just(1)
                    .subscribeOn(Schedulers.io())
                    .map(i -> {
                        actionListener.onConfirm(groupPosition, childPosition);
                        return i;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(i -> bindChild(view, groupPosition, childPosition));
        }

        private void doFinish(View view, int groupPosition, int childPosition) {
            Flowable.just(1)
                    .subscribeOn(Schedulers.io())
                    .map(i -> {
                        actionListener.onFinish(groupPosition, childPosition);
                        return i;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(i -> bindChild(view, groupPosition, childPosition));
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
