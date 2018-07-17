package cn.wrh.smart.dove.presenter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.Router;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.domain.event.BatchCageAdded;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.view.CageListDelegate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author bruce.wu
 * @date 2018/7/12
 */
public class CageListFragment extends BaseFragment<CageListDelegate> {

    public static CageListFragment newInstance() {
        return new CageListFragment();
    }

    private static final String TAG = "CageListFragment";

    private static final int FILTER_ALL = 3;
    private static final String STATE_FILTER = "filter";

    private int currentFilter = FILTER_ALL;

    private final List<String> groups = new ArrayList<>();
    private final List<List<Object>> data = new ArrayList<>();

    public CageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_FILTER, currentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onAdd();
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

    private void onAdd() {
        Router.route(getActivity(), AddCageActivity.class);
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
    public void onBatchCageAdded(BatchCageAdded e) {
        Log.d(TAG, "onBatchCageAdded: " + e.getCount());
        reload();
    }

    @SuppressLint("CheckResult")
    private void reload() {
        Log.i(TAG, "current filter: " + currentFilter);
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> queryFiltered())
                .observeOn(Schedulers.computation())
                .concatMap(Flowable::fromIterable)
                .toMultimap(entity -> entity.getSerialNumber().substring(0, 5))
                .map(TreeMap::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateAll);
    }

    private List<CageEntity> queryFiltered() {
        if (currentFilter == FILTER_ALL) {
            return getDatabase().cageDao().query();
        } else {
            return getDatabase().cageDao().query(CageModel.Status.values()[currentFilter]);
        }
    }

    private void updateAll(Map<String, Collection<CageEntity>> grouped) {
        this.groups.clear();
        this.data.clear();

        grouped.forEach((key, value) -> {
            this.groups.add(key);
            this.data.add(new ArrayList<>(value));
        });

        getViewDelegate().updateList();
    }

}
