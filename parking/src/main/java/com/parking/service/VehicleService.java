package com.parking.service;

import com.parking.model.Vehicle;
import java.sql.SQLException;
import java.util.List;

public interface VehicleService {
    Vehicle createVehicle(Vehicle vehicle) throws SQLException;
    List<Vehicle> getAllVehicles() throws SQLException;
    Vehicle getVehicleById(String vehicleNumber) throws SQLException;
    boolean updateVehicle(String vehicleNumber, Vehicle vehicle) throws SQLException;
    boolean deleteVehicle(String vehicleNumber) throws SQLException;
}
