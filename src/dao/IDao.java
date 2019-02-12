package dao;

import java.util.List;

public interface IDao<E> {

    void add(E o);

    List<E> getAll();

    E getById(int id);

    <T> void update(int id, T param);

    void delete(int id);
}
