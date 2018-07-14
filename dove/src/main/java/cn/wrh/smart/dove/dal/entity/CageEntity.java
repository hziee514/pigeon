package cn.wrh.smart.dove.dal.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import cn.wrh.smart.dove.domain.model.CageModel;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
@Entity(tableName = "CAGE", indices = {
        @Index(value = {"SERIAL_NUMBER"}, unique = true)
})
public class CageEntity implements CageModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "SERIAL_NUMBER")
    private String serialNumber;

    private Status status;

    @ColumnInfo(name = "CREATED_AT")
    private Date createdAt;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getSerialNumber() {
        return this.serialNumber;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Ignore
    public CageEntity() {

    }

    public CageEntity(int id, String serialNumber, Status status, Date createdAt) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public CageEntity(CageModel model) {
        this.id = model.getId();
        this.serialNumber = model.getSerialNumber();
        this.status = model.getStatus();
        this.createdAt = model.getCreatedAt();
    }

}
