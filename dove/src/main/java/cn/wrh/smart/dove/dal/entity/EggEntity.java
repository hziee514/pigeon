package cn.wrh.smart.dove.dal.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import cn.wrh.smart.dove.domain.model.EggModel;
import cn.wrh.smart.dove.util.DateUtils;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
@Entity(tableName = "T_EGG")
public class EggEntity implements EggModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "CAGE_ID")
    private int cageId;

    private int count;

    @ColumnInfo(name = "LAYING_AT")
    private Date layingAt;

    @ColumnInfo(name = "REVIEW_AT")
    private Date reviewAt;

    @ColumnInfo(name = "HATCH_AT")
    private Date hatchAt;

    @ColumnInfo(name = "SOLD_AT")
    private Date soldAt;

    private Stage stage;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getCageId() {
        return this.cageId;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public Date getLayingAt() {
        return this.layingAt;
    }

    @Override
    public Date getReviewAt() {
        return this.reviewAt;
    }

    @Override
    public Date getHatchAt() {
        return this.hatchAt;
    }

    @Override
    public Date getSoldAt() {
        return this.soldAt;
    }

    @Override
    public Stage getStage() {
        return this.stage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCageId(int cageId) {
        this.cageId = cageId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setLayingAt(Date layingAt) {
        this.layingAt = layingAt;
    }

    public void setReviewAt(Date reviewAt) {
        this.reviewAt = reviewAt;
    }

    public void setHatchAt(Date hatchAt) {
        this.hatchAt = hatchAt;
    }

    public void setSoldAt(Date soldAt) {
        this.soldAt = soldAt;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Ignore
    public EggEntity() {

    }

    public EggEntity(int id, int cageId, int count,
                     Date layingAt, Date reviewAt, Date hatchAt, Date soldAt, Stage stage) {
        this.id = id;
        this.cageId = cageId;
        this.count = count;
        this.layingAt = layingAt;
        this.reviewAt = reviewAt;
        this.hatchAt = hatchAt;
        this.soldAt = soldAt;
        this.stage = stage;
    }

    public EggEntity(EggModel model) {
        this.id = model.getId();
        this.cageId = model.getCageId();
        this.count = model.getCount();
        this.layingAt = model.getLayingAt();
        this.reviewAt = model.getReviewAt();
        this.hatchAt = model.getHatchAt();
        this.soldAt = model.getSoldAt();
        this.stage = model.getStage();
    }

    public String getStageDt() {
        switch (stage) {
            case Laid1:
            case Laid2:
                return DateUtils.getDateForEditor(layingAt);
            case Reviewed:
                return DateUtils.getDateForEditor(reviewAt);
            case Hatched:
                return DateUtils.getDateForEditor(hatchAt);
            case Sold:
                return DateUtils.getDateForEditor(soldAt);
            default:
                throw new IllegalStateException("Invalid stage");
        }
    }

    public void determineStage() {
        this.setStage(determineStage(this));
    }

    public static Stage determineStage(EggEntity entity) {
        if (entity.getSoldAt() != null) {
            return Stage.Sold;
        }
        if (entity.getHatchAt() != null) {
            return Stage.Hatched;
        }
        if (entity.getReviewAt() != null) {
            return Stage.Reviewed;
        }
        if (entity.getLayingAt() != null && entity.getCount() == 2){
            return Stage.Laid2;
        }
        if (entity.getLayingAt() != null && entity.getCount() == 1) {
            return Stage.Laid1;
        }
        throw new RuntimeException("invalid state");
    }

}
