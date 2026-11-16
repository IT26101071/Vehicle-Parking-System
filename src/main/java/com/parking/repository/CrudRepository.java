package com.parking.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    boolean create(T entity, Connection connection) throws SQLException;
    List<T> findAll(Connection connection) throws SQLException;
    Optional<T> findById(ID id, Connection connection) throws SQLException;
    boolean update(ID id, T entity, Connection connection) throws SQLException;
    boolean deleteById(ID id, Connection connection) throws SQLException;
}
