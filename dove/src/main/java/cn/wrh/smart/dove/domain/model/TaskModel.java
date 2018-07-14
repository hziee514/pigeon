package cn.wrh.smart.dove.domain.model;

import java.util.Date;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public interface TaskModel {

    int getId();

    int getCageId();

    int getEggId();

    Type getType();

    Date getCreatedAt();

    Date getFinishedAt();

    enum Type {

        /**
         * 没下蛋的要检查下蛋
         * 孵化后第14天后要检查下蛋
         */
        First,

        /**
         * 下第一个蛋后2天内（明天，后天）下第二个蛋
         */
        Second,

        /**
         * 下第二个蛋后第4天检查蛋的好坏
         */
        Bad,

        /**
         * 下第二个蛋后第17天检查孵化了没有
         */
        Hatch,

        /**
         * 孵化后第28天出栏
         */
        Sell,

    }

}
