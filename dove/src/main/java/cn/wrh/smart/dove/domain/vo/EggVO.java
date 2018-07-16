package cn.wrh.smart.dove.domain.vo;

/**
 * @author bruce.wu
 * @date 2018/7/16
 */
public class EggVO {

    public static final int GROUP_STAGE = 0;
    public static final int GROUP_DATE = 1;

    private int id;

    private int cageId;

    private String cageSn;

    private String date;

    private String stageText;

    private int groupMethod;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCageId() {
        return cageId;
    }

    public void setCageId(int cageId) {
        this.cageId = cageId;
    }

    public String getCageSn() {
        return cageSn;
    }

    public void setCageSn(String cageSn) {
        this.cageSn = cageSn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStageText() {
        return stageText;
    }

    public void setStageText(String stageText) {
        this.stageText = stageText;
    }

    public int getGroupMethod() {
        return groupMethod;
    }

    public void setGroupMethod(int groupMethod) {
        this.groupMethod = groupMethod;
    }
}
