package cn.wrh.smart.dove.domain.bo;

import java.util.Date;

import cn.wrh.smart.dove.domain.model.TaskModel;

/**
 * @author bruce.wu
 * @date 2018/7/15
 */
public class TaskBO {

    private int id;

    private int cageId;

    private String cageSn;

    private int eggId;

    private TaskModel.Type type;

    private Date finishedAt;

    private TaskModel.Status status;

    public int getId() {
        return id;
    }

    public int getCageId() {
        return cageId;
    }

    public String getCageSn() {
        return cageSn;
    }

    public int getEggId() {
        return eggId;
    }

    public TaskModel.Type getType() {
        return type;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public TaskModel.Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCageId(int cageId) {
        this.cageId = cageId;
    }

    public void setCageSn(String cageSn) {
        this.cageSn = cageSn;
    }

    public void setEggId(int eggId) {
        this.eggId = eggId;
    }

    public void setType(TaskModel.Type type) {
        this.type = type;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void setStatus(TaskModel.Status status) {
        this.status = status;
    }
}
