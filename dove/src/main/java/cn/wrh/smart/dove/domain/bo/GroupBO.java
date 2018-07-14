package cn.wrh.smart.dove.domain.bo;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class GroupBO {

    /**
     * group name
     */
    private final String name;

    /**
     * group item count
     */
    private final int count;

    public GroupBO(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return name + "[" + count + "]";
    }
}
