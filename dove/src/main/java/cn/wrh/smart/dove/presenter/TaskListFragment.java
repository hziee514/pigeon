package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import cn.wrh.smart.dove.domain.event.RestoreCompleted;
import cn.wrh.smart.dove.domain.event.SingleEggEdited;
import cn.wrh.smart.dove.domain.model.TaskModel;
import cn.wrh.smart.dove.util.DateUtils;
import cn.wrh.smart.dove.util.EggLifecycle;
import cn.wrh.smart.dove.util.GroupUtils;
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
public class TaskListFragment extends BaseFragment<TaskListDelegate> {

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
                .map(i -> {
                    final TaskBuilder builder = new TaskBuilder(getDatabase());

                    builder.clear();

                    builder.buildFirst1();
                    builder.buildFirst2();
                    builder.buildSecond();
                    builder.buildReview();
                    builder.buildHatch();
                    builder.buildSell();

                    reload();

                    return i;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscription -> getViewDelegate().showWaiting())
                .doOnComplete(getViewDelegate()::hideWaiting)
                .doOnError(tr -> getViewDelegate().hideWaiting())
                .subscribe();
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
        //getViewDelegate().setActionListener(this);
        getViewDelegate().setOnItemClick(this::onItemClicked)
                .setOnTaskConfirm(this::onTaskConfirmed)
                .setOnTaskFinish(this::onTaskFinished);

        if (state != null) {
            currentFilter = state.getInt(STATE_FILTER, FILTER_ALL);
        }

        reload();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onUnload() {
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRestoreCompleted(RestoreCompleted e) {
        reload();
    }

    private void reload() {
        Log.i(TAG, "current filter: " + currentFilter);
        //noinspection Convert2MethodRef
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> queryFiltered())
                .subscribeOn(Schedulers.computation())
                .concatMap(Flowable::fromIterable)
                .toMultimap(bo -> bo.getCageSn().substring(0, 5),
                        entity -> entity,
                        () -> new TreeMap<>(),
                        key -> new ArrayList<>())
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
            this.groups.add(GroupUtils.getGroupText(key, value.size()));
            this.data.add(new ArrayList<>(value));
        });

        getViewDelegate().updateList();
    }

    public void onItemClicked(Tuple<Integer, Integer> args) {
        TaskBO bo = (TaskBO)data.get(args.getFirst()).get(args.getSecond());
        if (bo.getEggId() > 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_EGG_ID, bo.getEggId());
            Router.route(getActivity(), AddEggActivity.class, bundle);
        }
    }

    public void onTaskConfirmed(Tuple<Integer, Integer> args) {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .subscribe(i -> {
                    final TaskBO bo = (TaskBO)data.get(args.getFirst()).get(args.getSecond());
                    EggEntity entity = EggLifecycle.create(getDatabase()).forward(bo);
                    EventBus.getDefault().post(new SingleEggEdited(entity));
                    onTaskFinished(args);
                });
    }

    public void onTaskFinished(Tuple<Integer, Integer> args) {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .subscribe(i -> {
                    TaskBO bo = (TaskBO)data.get(args.getFirst()).get(args.getSecond());
                    Date now = DateUtils.now();
                    TaskDao dao = getDatabase().taskDao();
                    TaskEntity entity = dao.fetch(bo.getId());
                    entity.setFinishedAt(now);
                    entity.setStatus(TaskModel.Status.Finished);
                    dao.update(entity);
                    reload();
                });
    }

}
