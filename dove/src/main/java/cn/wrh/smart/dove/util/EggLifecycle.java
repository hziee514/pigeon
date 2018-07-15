package cn.wrh.smart.dove.util;

import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.dal.dao.EggDao;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.EggModel;

/**
 * @author bruce.wu
 * @date 2018/7/15
 */
public class EggLifecycle {

    public static EggLifecycle create(AppDatabase database) {
        return new EggLifecycle(database);
    }

    private final AppDatabase database;

    private EggLifecycle(AppDatabase database) {
        this.database = database;
    }

    public void toLaid1(TaskBO task) {
        EggEntity egg = new EggEntity();
        egg.setCageId(task.getCageId());
        egg.setCount(1);
        egg.setLayingAt(DateUtils.now().toDate());
        egg.setStage(EggModel.Stage.Laid1);
        database.eggDao().insert(egg);
    }

    public void toLaid2(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setCount(2);
        entity.setLayingAt(DateUtils.now().toDate());
        entity.setStage(EggModel.Stage.Laid2);
        dao.update(entity);
    }

    public void toReviewed(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setReviewAt(DateUtils.now().toDate());
        entity.setStage(EggModel.Stage.Reviewed);
        dao.update(entity);
    }

    public void toHatched(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setHatchAt(DateUtils.now().toDate());
        entity.setStage(EggModel.Stage.Hatched);
        dao.update(entity);
    }

    public void toSold(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setSoldAt(DateUtils.now().toDate());
        entity.setStage(EggModel.Stage.Sold);
        dao.update(entity);
    }

}