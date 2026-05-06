package Cache;

import Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskCache {
    private static final TaskCache instance = new TaskCache();

    private final Map<Integer, Task> taskCache = new ConcurrentHashMap<>();

    public TaskCache(){}

    public static TaskCache getInstance(){
        return instance;
    }

    public List<Task> getAll(){
        return new ArrayList<>(taskCache.values());
    }

    public void put(Task task){
        if(task == null) return;
        taskCache.put(task.getId(), task);
    }

    public Task get(int id){
        return taskCache.get(id);
    }
    public void clear(){
        taskCache.clear();
    }
    public void update(Task task){
        if(task == null) return;
        Task existingTask = taskCache.get(task.getId());

        if(existingTask != null){
            existingTask.setTitle(task.getTitle());
            existingTask.setStatus(task.getStatus());
            existingTask.setPriority(task.getPriority());
            existingTask.setDeadline(task.getDeadline());
            existingTask.setDescription(task.getDescription());
            existingTask.setUpdatedAt(task.getUpdatedAt());
        } else {
            taskCache.put(task.getId(), task);
        }
    }


}
