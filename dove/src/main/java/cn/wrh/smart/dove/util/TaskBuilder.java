package cn.wrh.smart.dove.util;

import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.domain.model.EggModel;

import static cn.wrh.smart.dove.domain.model.CageModel.Status.Idle;
import static cn.wrh.smart.dove.domain.model.EggModel.Stage.Laid2;
import static cn.wrh.smart.dove.domain.model.EggModel.Stage.Reviewed;
import static cn.wrh.smart.dove.domain.model.TaskModel.Status.Waiting;
import static cn.wrh.smart.dove.domain.model.TaskModel.Type.Hatch;
import static cn.wrh.smart.dove.domain.model.TaskModel.Type.Lay1;
import static cn.wrh.smart.dove.domain.model.TaskModel.Type.Lay2;
import static cn.wrh.smart.dove.domain.model.TaskModel.Type.Review;
import static cn.wrh.smart.dove.domain.model.TaskModel.Type.Sell;

/**
 * @author bruce.wu
 * @date 2018/7/15
 */
public class TaskBuilder {

    private final AppDatabase database;

    public TaskBuilder(AppDatabase database) {
        this.database = database;
    }

    public void clear() {
        database.exec("DELETE FROM T_TASK");
    }

    /**
     * 已出栏或没下蛋的要检查下蛋
     */
    public void buildFirst1() {
        String sql = "INSERT INTO T_TASK(CAGE_ID, EGG_ID, TYPE, CREATED_AT, STATUS) " +
                "SELECT a.ID AS CAGE_ID, 0 AS EGG_ID, ? AS TYPE, datetime('now','localtime') AS CREATED_AT, ? AS STATUS " +
                "FROM T_CAGE a " +
                "WHERE a.STATUS != ? " +
                "AND a.ID NOT IN(SELECT DISTINCT CAGE_ID FROM T_EGG x WHERE x.STAGE != ?)";
        database.exec(sql, Lay1, Waiting, Idle, EggModel.Stage.Sold);
    }

    /**
     * 孵化后第14天后要检查下蛋
     */
    public void buildFirst2() {
        String sql = "INSERT INTO T_TASK(CAGE_ID, EGG_ID, TYPE, CREATED_AT, STATUS) " +
                "SELECT DISTINCT a.ID AS CAGE_ID, 0 AS EGG_ID, ? AS TYPE, datetime('now','localtime') AS CREATED_AT, ? AS STATUS " +
                "FROM T_CAGE a, T_EGG b " +
                "WHERE a.STATUS != ? AND a.id = b.CAGE_ID " +
                "AND b.STAGE = ? AND date(b.HATCH_AT,'14 day') < date('now','localtime')";
        database.exec(sql, Lay1, Waiting, Idle, EggModel.Stage.Hatched);
    }

    /**
     * 下第一个蛋后2天内（明天，后天）下第二个蛋
     */
    public void buildSecond() {
        String sql = "INSERT INTO T_TASK(CAGE_ID, EGG_ID, TYPE, CREATED_AT, STATUS) " +
                "SELECT DISTINCT a.ID AS CAGE_ID, b.ID AS EGG_ID, ? AS TYPE, datetime('now','localtime') AS CREATED_AT, ? AS STATUS " +
                "FROM T_CAGE a, T_EGG b " +
                "WHERE a.STATUS != ? " +
                "AND b.COUNT = 1 AND a.id = b.CAGE_ID " +
                "AND b.STAGE = ? " +
                "AND date(b.LAYING_AT) < date('now','localtime') " +
                "AND date(b.LAYING_AT, '2 day') >= date('now','localtime')";
        database.exec(sql, Lay2, Waiting, Idle, EggModel.Stage.Laid1);
    }

    /**
     * 下第二个蛋后第4天检查蛋的好坏
     */
    public void buildReview() {
        String sql = "INSERT INTO T_TASK(CAGE_ID, EGG_ID, TYPE, CREATED_AT, STATUS) " +
                "SELECT DISTINCT a.ID AS CAGE_ID, b.ID AS EGG_ID, ? AS TYPE, datetime('now','localtime') AS CREATED_AT, ? AS STATUS " +
                "FROM T_CAGE a, T_EGG b " +
                "WHERE a.STATUS != ? " +
                "AND b.COUNT = 2 AND a.id = b.CAGE_ID " +
                "AND b.STAGE = ? " +
                "AND date(b.LAYING_AT, '3 day') < date('now','localtime') " +
                "AND date(b.LAYING_AT, '17 day') > date('now','localtime') ";
        database.exec(sql, Review, Waiting, Idle, Laid2);
    }

    /**
     * 下第二个蛋后第17天检查孵化了没有
     */
    public void buildHatch() {
        String sql = "INSERT INTO T_TASK(CAGE_ID, EGG_ID, TYPE, CREATED_AT, STATUS) " +
                "SELECT DISTINCT a.ID AS CAGE_ID, b.ID AS EGG_ID, ? AS TYPE, datetime('now','localtime') AS CREATED_AT, ? AS STATUS " +
                "FROM T_CAGE a, T_EGG b " +
                "WHERE a.STATUS != ? " +
                "AND b.COUNT = 2 AND a.id = b.CAGE_ID " +
                "AND (b.STAGE = ? OR b.STAGE = ?)" +
                "AND date(b.LAYING_AT, '17 day') <= date('now','localtime')";
        database.exec(sql, Hatch, Waiting, Idle, Laid2, Reviewed);
    }

    /**
     * 孵化后第28天出栏
     */
    public void buildSell() {
        String sql = "INSERT INTO T_TASK(CAGE_ID, EGG_ID, TYPE, CREATED_AT, STATUS) " +
                "SELECT DISTINCT a.ID AS CAGE_ID, b.ID AS EGG_ID, ? AS TYPE, datetime('now','localtime') AS CREATED_AT, ? AS STATUS " +
                "FROM T_CAGE a, T_EGG b " +
                "WHERE a.STATUS != ? " +
                "AND b.COUNT = 2 AND a.id = b.CAGE_ID " +
                "AND b.STAGE = ? " +
                "AND date(b.HATCH_AT, '27 day') <= date('now','localtime')";
        database.exec(sql, Sell, Waiting, Idle, EggModel.Stage.Hatched);
    }

}
