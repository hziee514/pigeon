package cn.wrh.smart.dove.presenter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.wrh.smart.dove.AppExecutors;
import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.GroupBO;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.util.TaskBuilder;
import cn.wrh.smart.dove.view.TaskListDelegate;
import cn.wrh.smart.dove.view.snippet.MainAdapter;
import cn.wrh.smart.dove.view.snippet.OnItemClickListener;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListFragment extends BaseFragment<TaskListDelegate> implements OnItemClickListener {

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    private static final String TAG = "TaskListFragment";

    private static final int FILTER_ALL = 2;
    private static final String STATE_FILTER = "filter";

    private int currentFilter = FILTER_ALL;

    private final List<Object> data = new ArrayList<>();

    private MainAdapter adapter;

    public TaskListFragment() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                onReset();
                return true;
            case R.id.action_filter:
                onFilter();
                return true;
        }
        return false;
    }

    private void onReset() {
        getViewDelegate().showResetConfirm(this::doReset);
    }

    private void doReset() {
        AppExecutors.getInstance().diskIO(this::doResetInTransaction);
    }

    private void doResetInTransaction() {
        TaskBuilder builder = new TaskBuilder(getDatabase());
        builder.clear();
        builder.buildFirst1();
        builder.buildFirst2();
        builder.buildSecond();
        builder.buildReview();
        builder.buildHatch();
        builder.buildSell();

        reload();
    }

    private void onFilter() {
        getViewDelegate().showFilterDialog(currentFilter, this::onFilterSelected);
    }

    private void onFilterSelected(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (currentFilter == which) {
            return;
        }
        currentFilter = which;
        reload();
    }

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        adapter = getViewDelegate().newAdapter(data, this);
        reload();
    }

    private void reload() {
        Log.i(TAG, "current filter: " + currentFilter);
        if (currentFilter == FILTER_ALL) {
            AppExecutors.getInstance().diskIO(this::loadAllTasks);
        } else {
            TaskModel.Status status = TaskModel.Status.values()[currentFilter];
            AppExecutors.getInstance().diskIO(() -> loadTasks(status));
        }
    }

    private void loadAllTasks() {
        List<TaskBO> tasks = getDatabase().taskDao().all();
        List<Object> grouped = grouping(tasks);
        AppExecutors.getInstance().mainThread(() -> updateTasks(grouped));
    }

    private void loadTasks(TaskModel.Status status) {
        List<TaskBO> tasks = getDatabase().taskDao().loadByStatus(status);
        List<Object> grouped = grouping(tasks);
        AppExecutors.getInstance().mainThread(() -> updateTasks(grouped));
    }

    private void updateTasks(List<Object> grouped) {
        data.clear();
        data.addAll(grouped);
        adapter.notifyDataSetChanged();
    }

    private List<Object> grouping(List<TaskBO> entities) {
        Multimap<String, TaskBO> multimap = ArrayListMultimap.create();
        entities.forEach(entity -> {
            String group = entity.getCageSn().substring(0, 5);
            multimap.put(group, entity);
        });
        List<Object> result = new ArrayList<>();
        multimap.keySet().forEach(key -> {
            Collection<TaskBO> items = multimap.get(key);
            result.add(new GroupBO(key, items.size()));
            result.addAll(items);
        });
        return result;
    }

    @Override
    public void onItemClick(Object item) {

    }
}
