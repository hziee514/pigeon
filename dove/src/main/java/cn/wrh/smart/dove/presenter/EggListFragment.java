package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wrh.smart.dove.AppExecutors;
import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.EggBO;
import cn.wrh.smart.dove.domain.bo.GroupBO;
import cn.wrh.smart.dove.domain.model.EggModel;
import cn.wrh.smart.dove.view.EggListDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class EggListFragment extends BaseFragment<EggListDelegate> {

    public static EggListFragment newInstance() {
        return new EggListFragment();
    }

    private final List<String> groups = new ArrayList<>();
    private final List<List<Object>> data = new ArrayList<>();

    public EggListFragment() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onAdd();
                return true;
            case R.id.action_grouping:
                onGrouping();
                return true;
        }
        return false;
    }

    private void onAdd() {

    }

    private void onGrouping() {

    }

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        getViewDelegate().setupList(groups, data);

        reload();
    }

    private void reload() {
        AppExecutors.getInstance().diskIO(this::loadAll);
    }

    private void loadAll() {
        List<EggBO> items = getDatabase().eggDao().withoutStage(EggModel.Stage.Sold);
        Map<String, List<Object>> result;
    }

    /**
     * key -> stage
     * value -> sn, dt
     *
     * @param items data set
     * @return grouped
     */
    private Map<String, List<Object>> groupingByStage(List<EggBO> items) {
        String[] stages = getResources().getStringArray(R.array.egg_stages);

        Multimap<String, EggBO> multimap = ArrayListMultimap.create();
        items.forEach(item -> {
            String group = stages[item.getStage().ordinal()];
            multimap.put(group, item);
        });
        Map<String, List<Object>> result = new HashMap<>();
        multimap.keySet().forEach(key -> {
            Collection<EggBO> children = multimap.get(key);
            result.put(new GroupBO(key, items.size()).toString(), new ArrayList<>(items));
        });
        return result;
    }

    /**
     * key -> dt
     * fields -> sn, stage
     *
     * @param items data set
     * @return grouped
     */
    private Map<String, List<Object>> groupingByDate(List<EggBO> items) {
        return null;
    }

}
