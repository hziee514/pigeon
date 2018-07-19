package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.Router;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.domain.event.CageStatusChanged;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.view.CageInfoDelegate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static cn.wrh.smart.dove.Router.EXTRA_CAGE_ID;
import static cn.wrh.smart.dove.Router.EXTRA_EGG_ID;

public class CageInfoActivity extends BaseActivity<CageInfoDelegate> {

    private final List<String> groups = new ArrayList<>();
    private final List<List<Object>> data = new ArrayList<>();

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        final int id = getCageId();
        if (id <= 0) {
            finish();
            return;
        }
        initCage(id);
    }

    private void initCage(final int id) {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> getDatabase().cageDao().fetch(id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entity -> {
                    getViewDelegate().setCageSn(entity.getSerialNumber());
                    getViewDelegate().setStatus(entity.getStatus());
                    getViewDelegate().watchStatus(this::onStatusChanged);
                });
    }

    private void loadHistory(final int cageId) {
        getViewDelegate().setupList(groups, data);
        getViewDelegate().setOnItemClick(this::onItemClicked);

        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> getDatabase().eggDao().queryByCageId(cageId))
                .flatMap(Flowable::fromIterable)
                .toSortedList((l, r) -> r.getStageDt().compareTo(l.getStageDt()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    this.groups.clear();
                    this.data.clear();
                    groups.add(getString(R.string.feed_history) + "[" + list.size() + "]");
                    data.add(new ArrayList<>(list));
                    getViewDelegate().updateList(true);
                });
    }

    private int getCageId() {
        return getIntent().getIntExtra(EXTRA_CAGE_ID, 0);
    }

    private void onStatusChanged(CageModel.Status status) {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> getDatabase().cageDao().fetch(getCageId()))
                .subscribe(a -> {
                    a.setStatus(status);
                    getDatabase().cageDao().update(a);
                    EventBus.getDefault().post(new CageStatusChanged(a));
                });
    }

    private void onItemClicked(Tuple<Integer, Integer> args) {
        EggEntity entity = (EggEntity)data.get(args.getFirst()).get(args.getSecond());
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_EGG_ID, entity.getId());
        Router.route(this, AddEggActivity.class, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(getCageId());
    }
}
