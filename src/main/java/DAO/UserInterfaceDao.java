package DAO;

import java.util.List;

public interface UserInterfaceDao <T>{
    int insert(T t);
    int update(T t);
    int deleteById(int id);
    List<T> selectAll();
    T selectById(int id);
    T selectByEmail(String email);
}
