package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.Router;
import cn.wrh.smart.dove.domain.bo.EggBO;
import cn.wrh.smart.dove.domain.event.SingleEggEdited;
import cn.wrh.smart.dove.domain.model.EggModel;
import cn.wrh.smart.dove.domain.vo.EggVO;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.view.EggListDelegate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static cn.wrh.smart.dove.domain.vo.EggVO.GROUP_DATE;

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

    private int groupMethod = GROUP_DATE;
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
        Router.route(getActivity(), AddEggActivity.class);
    }

    private void onGrouping() {
        getViewDelegate().showGroupDialog(groupMethod, this::onGroupSelected);
    }

    private void onGroupSelected(int which) {
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
        getViewDelegate().setOnItemClick(this::onItemClicked);

        reload();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onUnload() {
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSingleEggEdited(SingleEggEdited e) {
        reload();
    }

    /**
     * group by stage:
     *  key -> stage
     *  value -> sn, dt
     *
     * group by date:
     *  key -> dt
     *  fields -> sn, stage
     */
    private void reload() {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> getDatabase().eggDao().withoutStage(EggModel.Stage.Sold))
                .observeOn(Schedulers.computation())
                .concatMap(Flowable::fromIterable)
                .map(a -> getEggVO(a, groupMethod))
                .toMultimap(a -> isGroupByDate() ? a.getDate() : a.getStageText())
                .map(TreeMap::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateAll);
    }

    private boolean isGroupByDate() {
        return groupMethod == GROUP_DATE;
    }

    private void updateAll(Map<String, Collection<EggVO>> grouped) {
        this.groups.clear();
        this.data.clear();

        grouped.forEach((key, value) -> {
            this.groups.add(key);
            this.data.add(new ArrayList<>(value));
        });

        getViewDelegate().updateList();
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

    private void onItemClicked(Tuple<Integer, Integer> args) {
        EggVO vo = (EggVO)data.get(args.getFirst()).get(args.getSecond());
        Bundle bundle = new Bundle();
        bundle.putInt(Router.EXTRA_EGG_ID, vo.getId());
        Router.route(getActivity(), AddEggActivity.class, bundle);
    }

}
