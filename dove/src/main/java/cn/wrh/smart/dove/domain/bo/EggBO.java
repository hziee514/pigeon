package cn.wrh.smart.dove.domain.bo;

import android.arch.persistence.room.ColumnInfo;

import cn.wrh.smart.dove.dal.entity.EggEntity;

/**
 * @author bruce.wu
 * @date 2018/7/16
 */
public class EggBO extends EggEntity {

    @ColumnInfo(name = "CAGE_SN")
    private String cageSn;

    public String getCageSn() {
        return cageSn;
    }

    public void setCageSn(String cageSn) {
        this.cageSn = cageSn;
    }
}
