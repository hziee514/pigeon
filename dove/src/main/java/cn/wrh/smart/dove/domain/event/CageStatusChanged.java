package cn.wrh.smart.dove.domain.event;

import cn.wrh.smart.dove.dal.entity.CageEntity;

/**
 * @author bruce.wu
 * @date 2018/7/18
 */
public class CageStatusChanged {

    private final CageEntity entity;

    public CageStatusChanged(CageEntity entity) {
        this.entity = entity;
    }

    public CageEntity getEntity() {
        return entity;
    }
}
