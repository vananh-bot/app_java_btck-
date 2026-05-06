package Cache;

import DTO.ProjectDashboardDTO;
import DTO.TaskDashboardDTO;

import java.util.ArrayList;
import java.util.List;

public class DashboardCache {
    private static final DashboardCache instance = new DashboardCache();

    private List<TaskDashboardDTO> tasks = new ArrayList<>();
    private List<ProjectDashboardDTO> projects = new ArrayList<>();
    private long lastFetchTime = 0;

    private DashboardCache (){

    }

    public static DashboardCache getInstance(){
        return instance;
    }

    public List<TaskDashboardDTO> getTasks() {
        return tasks;
    }

    public List<ProjectDashboardDTO> getProjects() {
        return projects;
    }

    public long getLastFetchTime() {
        return lastFetchTime;
    }

    public synchronized void setData(List<TaskDashboardDTO> tasks, List<ProjectDashboardDTO> projects){
        this.tasks = tasks;
        this.projects = projects;
        this.lastFetchTime = System.currentTimeMillis();

    }
    public synchronized void clear(){
        tasks.clear();
        projects.clear();
        lastFetchTime = 0;
    }
}
