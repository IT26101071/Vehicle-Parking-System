package com.parking.repository.custom;

import com.parking.model.PartPayment;
import com.parking.repository.CrudRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PartPaymentRepository extends CrudRepository<PartPayment, String> {
    List<PartPayment> findByVehicleNumber(String vehicleNumber, Connection connection) throws SQLException;
    List<PartPayment> findByStatus(String status, Connection connection) throws SQLException;
}
