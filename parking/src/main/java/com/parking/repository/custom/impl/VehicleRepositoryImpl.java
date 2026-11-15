package com.parking.repository.custom.impl;

import com.parking.model.Vehicle;
import com.parking.repository.custom.VehicleRepository;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {
    @Override
    public boolean create(Vehicle vehicle, Connection connection) throws SQLException {
        String sql = "INSERT INTO vehicles (vehicle_number, vehicle_type, brand, model, color, owner_name, contact_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehicle.getVehicleNumber());
            ps.setString(2, vehicle.getVehicleType());
            ps.setString(3, vehicle.getBrand());
            ps.setString(4, vehicle.getModel());
            ps.setString(5, vehicle.getColor());
            ps.setString(6, vehicle.getOwnerName());
            ps.setString(7, vehicle.getContactNumber());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Vehicle> findAll(Connection connection) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM vehicles ORDER BY vehicle_number");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) vehicles.add(mapVehicle(rs));
        }
        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String vehicleNumber, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM vehicles WHERE vehicle_number = ?")) {
            ps.setString(1, vehicleNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapVehicle(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public boolean update(String vehicleNumber, Vehicle vehicle, Connection connection) throws SQLException {
        String sql = "UPDATE vehicles SET vehicle_type = ?, brand = ?, model = ?, color = ?, owner_name = ?, contact_number = ? WHERE vehicle_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehicle.getVehicleType());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setString(4, vehicle.getColor());
            ps.setString(5, vehicle.getOwnerName());
            ps.setString(6, vehicle.getContactNumber());
            ps.setString(7, vehicleNumber);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(String vehicleNumber, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM vehicles WHERE vehicle_number = ?")) {
            ps.setString(1, vehicleNumber);
            return ps.executeUpdate() > 0;
        }
    }

    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(rs.getString("vehicle_number"), rs.getString("vehicle_type"),
                rs.getString("brand"), rs.getString("model"), rs.getString("color"),
                rs.getString("owner_name"), rs.getString("contact_number"));
    }
}
