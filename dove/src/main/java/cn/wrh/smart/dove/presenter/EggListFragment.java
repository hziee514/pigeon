package cn.wrh.smart.dove.presenter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cn.wrh.smart.dove.AppExecutors;
import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.domain.bo.EggBO;
import cn.wrh.smart.dove.domain.bo.GroupBO;
import cn.wrh.smart.dove.domain.model.EggModel;
import cn.wrh.smart.dove.domain.vo.EggVO;
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

    private int groupMethod = EggVO.GROUP_DATE;
    private String[] stages;

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
            case R.id.action_refresh:
                reload();
                return true;
        }
        return false;
    }

    private void onAdd() {

    }

    private void onGrouping() {
        getViewDelegate().showGroupDialog(groupMethod, this::onGroupSelected);
    }

    private void onGroupSelected(DialogInterface dialogInterface, int which) {
        if (groupMethod == which) {
            return;
        }
        groupMethod = which;
        reload();
    }

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        stages = getResources().getStringArray(R.array.egg_stages);
        getViewDelegate().setupList(groups, data);

        reload();
    }

    private void reload() {
        AppExecutors.getInstance().diskIO(this::loadAll);
    }

    private boolean isGroupByDate() {
        return groupMethod == EggVO.GROUP_DATE;
    }

    private void loadAll() {
        List<EggBO> items = getDatabase().eggDao().withoutStage(EggModel.Stage.Sold);
        Map<String, List<Object>> grouped = isGroupByDate() ? groupingByDate(items) : groupingByStage(items);
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

    /**
     * key -> stage
     * value -> sn, dt
     *
     * @param items data set
     * @return grouped
     */
    private Map<String, List<Object>> groupingByStage(List<EggBO> items) {
        Multimap<String, EggVO> multimap = ArrayListMultimap.create();
        items.forEach(item -> {
            String group = stages[item.getStage().ordinal()];
            multimap.put(group, getEggVO(item, EggVO.GROUP_STAGE));
        });
        Map<String, List<Object>> result = new TreeMap<>();
        multimap.keySet().forEach(key -> {
            Collection<EggVO> children = multimap.get(key);
            result.put(new GroupBO(key, children.size()).toString(), new ArrayList<>(children));
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
        Multimap<String, EggVO> multimap = ArrayListMultimap.create();
        items.forEach(item -> {
            EggVO vo = getEggVO(item, EggVO.GROUP_DATE);
            multimap.put(vo.getDate(), vo);
        });
        Map<String, List<Object>> result = new TreeMap<>();
        multimap.keySet().forEach(key -> {
            Collection<EggVO> children = multimap.get(key);
            result.put(new GroupBO(key, children.size()).toString(), new ArrayList<>(children));
        });
        return result;
    }

    private EggVO getEggVO(EggBO bo, int groupMethod) {
        EggVO vo = new EggVO();
        vo.setId(bo.getId());
        vo.setCageId(bo.getCageId());
        vo.setCageSn(bo.getCageSn());
        vo.setStageText(stages[bo.getStage().ordinal()]);
        vo.setDate(getStageDate(bo));
        vo.setGroupMethod(groupMethod);
        return vo;
    }

    private String getStageDate(EggBO bo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        switch (bo.getStage()) {
            case Laid1:
            case Laid2:
                return sdf.format(bo.getLayingAt());
            case Reviewed:
                return sdf.format(bo.getReviewAt());
            case Hatched:
                return sdf.format(bo.getHatchAt());
            case Sold:
                return sdf.format(bo.getSoldAt());
        }
        throw new NullPointerException();
    }

}
