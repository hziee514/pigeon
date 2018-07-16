package cn.wrh.smart.dove.domain.model;

import java.util.Date;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public interface EggModel {

    int getId();

    /**
     * @return 笼位ID
     */
    int getCageId();

    /**
     * @return 已下蛋个数
     */
    int getCount();

    /**
     * @return 下蛋时间
     */
    Date getLayingAt();

    /**
     * @return 巡检时间
     */
    Date getReviewAt();

    /**
     * @return 孵化时间
     */
    Date getHatchAt();

    /**
     * @return 出栏时间
     */
    Date getSoldAt();

    /**
     * @return 阶段
     */
    Stage getStage();

    enum Stage {

        /**
         * 下了第一颗蛋
         */
        Laid1,

        /**
         * 下了第二颗蛋
         */
        Laid2,

        /**
         * 已巡检
         */
        Reviewed,

        /**
         * 已孵化
         */
        Hatched,

        /**
         * 已出栏
         */
        Sold,

    }

}
