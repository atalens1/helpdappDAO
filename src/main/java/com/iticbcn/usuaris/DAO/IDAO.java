package com.iticbcn.usuaris.DAO;

import java.util.List;
import java.util.Optional;

public interface IDAO<T> {

    Optional<T> get(int id) throws Exception;
    
    List<T> getAll() throws Exception;
    
    void save(T t) throws Exception;
    
    void update(T t) throws Exception;
    
    void delete(T t) throws Exception;

}