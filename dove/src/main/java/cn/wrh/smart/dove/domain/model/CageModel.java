package cn.wrh.smart.dove.domain.model;

import java.util.Date;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public interface CageModel {

    int getId();

    /**
     * @return 笼位编号: XX-XX-XXX
     */
    String getSerialNumber();

    /**
     * @return 状态
     */
    Status getStatus();

    /**
     * @return 添加时间
     */
    Date getCreatedAt();

    enum Status {

        /**
         * 空闲
         */
        Idle,

        /**
         * 健康
         */
        Healthy,

        /**
         * 生病
         */
        Sickly,

    }

}
