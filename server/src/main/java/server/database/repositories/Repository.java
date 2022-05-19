package server.database.repositories;

import java.util.Stack;

public interface Repository<T> {
    boolean save(T obj);
    Stack<T> getAll();
    T getById(int id);
    boolean deleteById(int id);
    boolean updateById(int id, T obj);
}
