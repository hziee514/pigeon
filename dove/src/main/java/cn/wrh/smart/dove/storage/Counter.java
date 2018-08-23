package cn.wrh.smart.dove.storage;

/**
 * @author bruce.wu
 * @date 2018/7/30
 */
public class Counter {

    private int totalCage = 0;

    private int totalEgg = 0;

    void incCage() {
        totalCage++;
    }

    void incEgg() {
        totalEgg++;
    }

    public int getTotalCage() {
        return totalCage;
    }

    public int getTotalEgg() {
        return totalEgg;
    }
}
