package cn.wrh.smart.dove.domain.event;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class BatchCageAdded {

    private final int count;

    public BatchCageAdded(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
