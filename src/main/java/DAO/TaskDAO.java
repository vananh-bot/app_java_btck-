package DAO;

import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements TaskInterfaceDAO<Task>{

    @Override
    public int insert(Task task){
        return 1;
    }
    @Override
    public int deleteById(int id){
        return 1;
    }

    @Override
    public int edit(Task task){
        return 1;
    }

    @Override
    public Task getById(int id){
        Task t = new Task();
        return t;
    }

    @Override
    public List<Task> getTasksByProjectId(int projectId){
        List<Task> t = new ArrayList<>();
        return t;
    }

    @Override
    public List<Task> getTasksByPriority(String priority){

        List<Task> t = new ArrayList<>();
        return t;
    }
    @Override
    public List<Task> getTasksBySearch(String name){

        List<Task> t = new ArrayList<>();
        return t;
    }

    @Override
    public List<Task> getUpcomingDeadlines(){

        List<Task> t = new ArrayList<>();
        return t;
    }

    @Override
    public List<Task> getTasksByStatus(String status){

        List<Task> t = new ArrayList<>();
        return t;
    }
}
