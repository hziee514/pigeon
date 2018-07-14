package cn.wrh.smart.dove.dal.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import cn.wrh.smart.dove.domain.model.TaskModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
@Entity(tableName = "TASK")
public class TaskEntity implements TaskModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "CAGE_ID")
    private int cageId;

    @ColumnInfo(name = "EGG_ID")
    private int eggId;

    private Type type;

    @ColumnInfo(name = "CREATED_AT")
    private Date createdAt;

    @ColumnInfo(name = "FINISHED_AT")
    private Date finishedAt;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getCageId() {
        return this.cageId;
    }

    @Override
    public int getEggId() {
        return this.eggId;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public Date getFinishedAt() {
        return this.finishedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCageId(int cageId) {
        this.cageId = cageId;
    }

    public void setEggId(int eggId) {
        this.eggId = eggId;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Ignore
    public TaskEntity() {

    }

    public TaskEntity(int id, int cageId, int eggId, Type type, Date createdAt, Date finishedAt) {
        this.id = id;
        this.cageId = cageId;
        this.eggId = eggId;
        this.type = type;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
    }

    public TaskEntity(TaskModel model) {
        this(model.getId(), model.getCageId(), model.getEggId(),
                model.getType(), model.getCreatedAt(), model.getFinishedAt());
    }

}
