package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Collections;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.domain.bo.EggBO;
import cn.wrh.smart.dove.domain.event.SingleEggEdited;
import cn.wrh.smart.dove.util.DateUtils;
import cn.wrh.smart.dove.util.Tuple;
import cn.wrh.smart.dove.view.AddEggDelegate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static cn.wrh.smart.dove.Router.EXTRA_EGG_ID;

public class AddEggActivity extends BaseActivity<AddEggDelegate> {

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        int eggId = getEggId();
        if (eggId > 0) {
            doInit(eggId);
            findViewById(R.id.btn_del).setVisibility(View.VISIBLE);
        } else {
            doInit();
        }
        getViewDelegate().setOnClickListener(this::onClickDateEditor, R.id.laying, R.id.review, R.id.hatch);
        getViewDelegate().setOnClickListener(this::onClickSave, R.id.btn_save);
        getViewDelegate().setOnClickListener(this::onClickDelete, R.id.btn_del);
    }

    private void doInit(int id) {
        if (id <= 0) {
            finish();
            return;
        }
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> {
                    final EggBO egg = getDatabase().eggDao().findById(id);
                    return new Tuple<>(egg, Collections.singletonList(egg.getCageSn()));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tuple -> {
                    final AddEggDelegate delegate = getViewDelegate();
                    final EggBO bo = tuple.getFirst();
                    delegate.setCageSns(tuple.getSecond(), bo.getCageSn());
                    delegate.setCount(bo.getCount());
                    delegate.setLayingDt(DateUtils.getDateForEditor(bo.getLayingAt()));
                    delegate.setReviewDt(DateUtils.getDateForEditor(bo.getReviewAt()));
                    delegate.setHatchDt(DateUtils.getDateForEditor(bo.getHatchAt()));
                });
    }

    private int getEggId() {
        return getIntent().getIntExtra(EXTRA_EGG_ID, 0);
    }

    private void doInit() {
        getViewDelegate().setLayingDt(DateUtils.getDateForEditor());
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> getDatabase().cageDao().querySnsInUsing())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> getViewDelegate().setCageSns(a));
    }

    private void onClickDateEditor(View view) {
        final EditText editor = (EditText)view;
        final Calendar c = DateUtils.getCalendarForEditor(editor.getText().toString());
        getViewDelegate().showDatePicker(c, datePicker -> editor.setText(DateUtils.getDateForEditor(datePicker)));
    }

    private void onClickSave(@SuppressWarnings("unused") View view) {
        final AddEggDelegate delegate = getViewDelegate();
        String sn = delegate.getSn();
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> getDatabase().cageDao().getIdBySn(sn))
                .subscribe(cageId -> {
                    int eggId = getEggId();
                    EggEntity entity = new EggEntity();
                    entity.setId(eggId);
                    entity.setCageId(cageId);
                    entity.setLayingAt(delegate.getLayingDt());
                    entity.setCount(delegate.getCount());
                    entity.setReviewAt(delegate.getReviewDt());
                    entity.setHatchAt(delegate.getHatchDt());
                    entity.determineStage();
                    if (eggId > 0) {
                        getDatabase().eggDao().update(entity);
                    } else {
                        getDatabase().eggDao().insert(entity);
                    }

                    EventBus.getDefault().post(new SingleEggEdited(entity));

                    finish();
                });
    }

    private void onClickDelete(@SuppressWarnings("unused") View view) {
        getViewDelegate().showDeleteConfirm(this::onDeleteConfirm);
    }

    private void onDeleteConfirm() {
        Flowable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(a -> {
                    getDatabase().eggDao().delete(getEggId());

                    EventBus.getDefault().post(new SingleEggEdited(null));

                    finish();
                });
    }

}
