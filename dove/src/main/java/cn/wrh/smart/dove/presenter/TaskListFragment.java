package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.Router;
import cn.wrh.smart.dove.dal.dao.TaskDao;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.dal.entity.TaskEntity;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.event.SingleEggEdited;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.util.DateUtils;
import cn.wrh.smart.dove.util.EggLifecycle;
import cn.wrh.smart.dove.util.TaskBuilder;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.view.TaskListDelegate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static cn.wrh.smart.dove.Router.EXTRA_EGG_ID;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListFragment extends BaseFragment<TaskListDelegate>
        implements TaskListDelegate.ActionListener {

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    private static final String TAG = "TaskListFragment";

    private static final int FILTER_ALL = 2;
    private static final String STATE_FILTER = "filter";

    private int currentFilter = FILTER_ALL;

    private final List<String> groups = new ArrayList<>();
    private final List<List<Object>> data = new ArrayList<>();

    public TaskListFragment() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_FILTER, currentFilter);
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
            case R.id.action_refresh:
                reload();
                return true;
        }
        return false;
    }

    private void onReset() {
        getViewDelegate().showResetConfirm(this::doReset);
    }

    private void doReset() {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .subscribe(i -> {
                    final TaskBuilder builder = new TaskBuilder(getDatabase());

                    builder.clear();

                    builder.buildFirst1();
                    builder.buildFirst2();
                    builder.buildSecond();
                    builder.buildReview();
                    builder.buildHatch();
                    builder.buildSell();

                    reload();
                });
    }

    private void onFilter() {
        getViewDelegate().showFilterDialog(currentFilter, this::onFilterSelected);
    }

    private void onFilterSelected(int which) {
        if (currentFilter == which) {
            return;
        }
        currentFilter = which;
        reload();
    }

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        getViewDelegate().setupList(groups, data);
        getViewDelegate().setActionListener(this);
        getViewDelegate().setOnItemClick(this::onItemClicked);

        if (state != null) {
            currentFilter = state.getInt(STATE_FILTER, FILTER_ALL);
        }

        reload();
    }

    private void reload() {
        Log.i(TAG, "current filter: " + currentFilter);
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> queryFiltered())
                .subscribeOn(Schedulers.computation())
                .concatMap(Flowable::fromIterable)
                .toMultimap(bo -> bo.getCageSn().substring(0, 5))
                .map(TreeMap::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateAll);
    }

    private List<TaskBO> queryFiltered() {
        if (currentFilter == FILTER_ALL) {
            return getDatabase().taskDao().today();
        } else {
            return getDatabase().taskDao().today(TaskModel.Status.values()[currentFilter]);
        }
    }

    private void updateAll(Map<String, Collection<TaskBO>> grouped) {
        this.groups.clear();
        this.data.clear();

        grouped.forEach((key, value) -> {
            this.groups.add(key);
            this.data.add(new ArrayList<>(value));
        });

        getViewDelegate().updateList();
    }

    @Override
    public void onConfirm(int groupPosition, int childPosition) {
        TaskBO bo = (TaskBO)data.get(groupPosition).get(childPosition);
        EggLifecycle lifecycle = EggLifecycle.create(getDatabase());
        EggEntity entity = null;
        switch (bo.getType()) {
            case Lay1:
                entity = lifecycle.toLaid1(bo);
                break;
            case Lay2:
                entity = lifecycle.toLaid2(bo);
                break;
            case Review:
                entity = lifecycle.toReviewed(bo);
                break;
            case Hatch:
                entity = lifecycle.toHatched(bo);
                break;
            case Sell:
                entity = lifecycle.toSold(bo);
                break;
        }
        EventBus.getDefault().post(new SingleEggEdited(entity));
        onFinish(groupPosition, childPosition);
    }

    @Override
    public void onFinish(int groupPosition, int childPosition) {
        Date now = DateUtils.now().toDate();

        TaskBO bo = (TaskBO)data.get(groupPosition).get(childPosition);
        bo.setFinishedAt(now);
        bo.setStatus(TaskModel.Status.Finished);

        TaskDao dao = getDatabase().taskDao();
        TaskEntity entity = dao.fetch(bo.getId());
        entity.setFinishedAt(now);
        entity.setStatus(TaskModel.Status.Finished);
        dao.update(entity);
    }

    public void onItemClicked(Tuple<Integer, Integer> args) {
        TaskBO bo = (TaskBO)data.get(args.getFirst()).get(args.getSecond());
        if (bo.getEggId() > 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_EGG_ID, bo.getEggId());
            Router.route(getActivity(), AddEggActivity.class, bundle);
        }
    }

}
