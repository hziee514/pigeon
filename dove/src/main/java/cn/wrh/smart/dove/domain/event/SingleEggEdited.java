package cn.wrh.smart.dove.domain.event;

import cn.wrh.smart.dove.dal.entity.EggEntity;

/**
 * @author bruce.wu
 * @date 2018/7/17
 */
public class SingleEggEdited {

    private final EggEntity entity;

    public SingleEggEdited(EggEntity entity) {
        this.entity = entity;
    }

    public EggEntity getEntity() {
        return entity;
    }
}
