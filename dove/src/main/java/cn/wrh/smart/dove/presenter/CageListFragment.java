package cn.wrh.smart.dove.presenter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wrh.smart.dove.AppExecutors;
import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.Router;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.domain.bo.GroupBO;
import cn.wrh.smart.dove.domain.event.BatchCageAddedEvent;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.view.CageListDelegate;

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
        }
        return false;
    }

    private void onAdd() {
        Router.route(getActivity(), AddCageActivity.class);
    }

    private void onFilter() {
        getViewDelegate().showFilterDialog(currentFilter, this::onFilterSelected);
    }

    private void onFilterSelected(DialogInterface dialog, int which) {
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
    public void onBatchCageAdded(BatchCageAddedEvent e) {
        Log.d(TAG, "onBatchCageAdded: " + e.getCount());
        reload();
    }

    private void reload() {
        Log.i(TAG, "current filter: " + currentFilter);
        if (currentFilter == FILTER_ALL) {
            AppExecutors.getInstance().diskIO(this::loadAll);
        } else {
            CageModel.Status status = CageModel.Status.values()[currentFilter];
            AppExecutors.getInstance().diskIO(() -> loadAll(status));
        }
    }

    private void loadAll() {
        List<CageEntity> entities = getDatabase().cageDao().all();
        Map<String, List<Object>> grouped = grouping(entities);
        AppExecutors.getInstance().mainThread(() -> updateAll(grouped));
    }

    private void loadAll(CageModel.Status status) {
        List<CageEntity> entities = getDatabase().cageDao().loadByStatus(status);
        Map<String, List<Object>> grouped = grouping(entities);
        AppExecutors.getInstance().mainThread(() -> updateAll(grouped));
    }

    private void updateAll(Map<String, List<Object>> grouped) {
        this.groups.clear();
        this.data.clear();

        grouped.forEach((key, value) -> {
            this.groups.add(key);
            this.data.add(value);
        });

        getViewDelegate().updateList();
    }

    private Map<String, List<Object>> grouping(List<CageEntity> entities) {
        Multimap<String, CageEntity> multimap = ArrayListMultimap.create();
        entities.forEach(entity -> {
            String group = entity.getSerialNumber().substring(0, 5);
            multimap.put(group, entity);
        });
        Map<String, List<Object>> result = new HashMap<>();
        multimap.keySet().forEach(key -> {
            Collection<CageEntity> items = multimap.get(key);
            result.put(new GroupBO(key, items.size()).toString(), new ArrayList<>(items));
        });
        return result;
    }

}
