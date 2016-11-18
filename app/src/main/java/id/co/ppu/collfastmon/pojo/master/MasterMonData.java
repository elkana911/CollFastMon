package id.co.ppu.collfastmon.pojo.master;

import java.util.List;

/**
 * Created by Eric on 08-Sep-16.
 */
public class MasterMonData extends MasterData{

    private List<MstTaskType> task;

    public List<MstTaskType> getTask() {
        return task;
    }

    public void setTask(List<MstTaskType> task) {
        this.task = task;
    }
}
