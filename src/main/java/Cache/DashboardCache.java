package Cache;

import DTO.ProjectDashboardDTO;
import DTO.TaskDashboardDTO;

import java.util.ArrayList;
import java.util.List;

public class DashboardCache {
    private static final DashboardCache instance = new DashboardCache();

    private List<TaskDashboardDTO> tasks = new ArrayList<>();
    private long lastFetchTime = 0;

    private DashboardCache (){

    }

    public static DashboardCache getInstance(){
        return instance;
    }

    public List<TaskDashboardDTO> getTasks() {
        return tasks;
    }


    public long getLastFetchTime() {
        return lastFetchTime;
    }

    public synchronized void setData(List<TaskDashboardDTO> tasks){
        this.tasks = tasks;
        this.lastFetchTime = System.currentTimeMillis();

    }
    public synchronized void clear(){
        tasks.clear();
        lastFetchTime = 0;
    }
}
