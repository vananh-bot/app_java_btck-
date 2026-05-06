package Cache;

import DTO.ProjectDashboardDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectCache {
    private static final ProjectCache instance = new ProjectCache();

    private final Map<Integer, ProjectDashboardDTO> cache = new ConcurrentHashMap<>();

    public ProjectCache(){}

    public static ProjectCache getInstance(){
        return instance;
    }

    public List<ProjectDashboardDTO> getAll(){
        return new ArrayList<>(cache.values());
    }

    public void put(ProjectDashboardDTO project){
        if(project == null) return;
        cache.put(project.getId(), project);
    }
    public void putList(List<ProjectDashboardDTO> projectList){
        if(projectList.isEmpty()) return;
        for(ProjectDashboardDTO p : projectList){
            put(p);
        }
    }

    public ProjectDashboardDTO get(int id){
        return cache.get(id);
    }
    public void clear(){
        cache.clear();
    }
}
