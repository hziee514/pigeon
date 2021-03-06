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

    public EggEntity toLaid1(TaskBO task) {
        EggEntity entity = new EggEntity();
        entity.setCageId(task.getCageId());
        entity.setCount(1);
        entity.setLayingAt(DateUtils.now());
        entity.setStage(EggModel.Stage.Laid1);
        database.eggDao().insert(entity);
        return entity;
    }

    public EggEntity toLaid2(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setCount(2);
        entity.setLayingAt(DateUtils.now());
        entity.setStage(EggModel.Stage.Laid2);
        dao.update(entity);
        return entity;
    }

    public EggEntity toReviewed(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setReviewAt(DateUtils.now());
        entity.setStage(EggModel.Stage.Reviewed);
        dao.update(entity);
        return entity;
    }

    public EggEntity toHatched(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setHatchAt(DateUtils.now());
        entity.setStage(EggModel.Stage.Hatched);
        dao.update(entity);
        return entity;
    }

    public EggEntity toSold(TaskBO task) {
        EggDao dao = database.eggDao();
        EggEntity entity = dao.fetch(task.getEggId());
        entity.setSoldAt(DateUtils.now());
        entity.setStage(EggModel.Stage.Sold);
        dao.update(entity);
        return entity;
    }

    public EggEntity forward(TaskBO bo) {
        EggEntity entity = null;
        switch (bo.getType()) {
            case Lay1:
                entity = toLaid1(bo);
                break;
            case Lay2:
                entity = toLaid2(bo);
                break;
            case Review:
                entity = toReviewed(bo);
                break;
            case Hatch:
                entity = toHatched(bo);
                break;
            case Sell:
                entity = toSold(bo);
                break;
        }
        return entity;
    }

}
