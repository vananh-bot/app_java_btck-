package DAO;

import Model.User;

import java.util.List;

public interface UserInterfaceDao <T>{
    int insert(T t);
    int update(T t);
    int deleteById(int id);
    List<T> selectAll();
    T findByName(String name);
    T findByEmail(String email);
    T findById(int id);
}
