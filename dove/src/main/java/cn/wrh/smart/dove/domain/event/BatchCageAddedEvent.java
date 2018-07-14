package cn.wrh.smart.dove.domain.event;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class BatchCageAddedEvent {

    private final int count;

    public BatchCageAddedEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
