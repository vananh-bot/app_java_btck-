package DAO;

import Model.User;
import Enum.Role;

import java.sql.Connection;
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
