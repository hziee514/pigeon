package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.Date;

import cn.wrh.smart.dove.AppExecutors;
import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.dal.dao.CageDao;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.domain.event.BatchCageAddedEvent;
import cn.wrh.smart.dove.domain.model.CageModel;
import cn.wrh.smart.dove.util.DateUtils;
import cn.wrh.smart.dove.view.AddCageDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/12
 */
public class AddCageActivity extends BaseActivity<AddCageDelegate> {

    private static final String TAG = "AddCageActivity";

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        getViewDelegate().setOnClickListener(this::onClickSave, R.id.btn_save);
    }

    private void onClickSave(@SuppressWarnings("unused") View view) {
        String roomId = getViewDelegate().getRoomId();
        String groupId = getViewDelegate().getGroupId();
        String layerId = getViewDelegate().getLayerId();
        int first = getViewDelegate().getFirst();
        int last = getViewDelegate().getLast();
        int status = getViewDelegate().getStatus();
        if (first > last) {
            toast(R.string.first_cant_gt_last);
            return;
        }
        CageModel.Status s = CageModel.Status.values()[status];
        new AppExecutors().diskIO(() -> batchAddCage(roomId, groupId, layerId, first, last, s));
    }

    private void batchAddCage(String roomId, String groupId, String layerId, int first, int last, CageModel.Status status) {
        final AppDatabase database = getDatabase();
        final CageDao dao = database.cageDao();
        final DecimalFormat df = new DecimalFormat("00");
        database.runInTransaction(() -> {
            Date now = DateUtils.now();
            for (int i = first; i <= last; i++) {
                CageEntity entity = new CageEntity();
                String sn = roomId + "-" + groupId + "-" + layerId + df.format(i);
                entity.setSerialNumber(sn);
                entity.setStatus(status);
                entity.setCreatedAt(now);
                dao.insert(entity);
                Log.d(TAG, "insert cage: " + sn);
            }
            EventBus.getDefault().post(new BatchCageAddedEvent(last - first + 1));
        });
        finish();
    }

}
