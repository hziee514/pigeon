package cn.wrh.smart.dove.domain.bo;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

import cn.wrh.smart.dove.dal.entity.TaskEntity;
import cn.wrh.smart.dove.domain.model.TaskModel;

/**
 * @author bruce.wu
 * @date 2018/7/15
 */
public class TaskBO extends TaskEntity {

    @ColumnInfo(name = "CAGE_SN")
    private String cageSn;

    public String getCageSn() {
        return cageSn;
    }

    public void setCageSn(String cageSn) {
        this.cageSn = cageSn;
    }
}
