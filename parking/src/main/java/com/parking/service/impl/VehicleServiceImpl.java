package com.parking.service.impl;

import com.parking.model.Vehicle;
import com.parking.repository.custom.VehicleRepository;
import com.parking.service.VehicleService;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class VehicleServiceImpl implements VehicleService {
    private static final Set<String> TYPES = Set.of("CAR", "BIKE", "VAN", "BUS", "THREE_WHEELER", "TRUCK");
    private final VehicleRepository vehicleRepository;
    private final DataSource dataSource;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, DataSource dataSource) {
        this.vehicleRepository = vehicleRepository;
        this.dataSource = dataSource;
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) throws SQLException {
        validateVehicle(vehicle);
        vehicle.setVehicleNumber(normalizeNumber(vehicle.getVehicleNumber()));
        try (Connection connection = dataSource.getConnection()) {
            if (vehicleRepository.findById(vehicle.getVehicleNumber(), connection).isPresent())
                throw new IllegalArgumentException("Vehicle number already registered");
            if (!vehicleRepository.create(vehicle, connection))
                throw new IllegalStateException("Vehicle creation failed");
            return vehicle;
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return vehicleRepository.findAll(connection);
        }
    }

    @Override
    public Vehicle getVehicleById(String vehicleNumber) throws SQLException {
        String id = normalizeNumber(vehicleNumber);
        try (Connection connection = dataSource.getConnection()) {
            return vehicleRepository.findById(id, connection)
                    .orElseThrow(() -> new NoSuchElementException("Vehicle not found: " + id));
        }
    }

    @Override
    public boolean updateVehicle(String vehicleNumber, Vehicle vehicle) throws SQLException {
        String id = normalizeNumber(vehicleNumber);
        validateVehicle(vehicle);
        if (!id.equals(normalizeNumber(vehicle.getVehicleNumber())))
            throw new IllegalArgumentException("Vehicle number cannot be changed during update");
        try (Connection connection = dataSource.getConnection()) {
            return vehicleRepository.update(id, vehicle, connection);
        }
    }

    @Override
    public boolean deleteVehicle(String vehicleNumber) throws SQLException {
        String id = normalizeNumber(vehicleNumber);
        try (Connection connection = dataSource.getConnection()) {
            return vehicleRepository.deleteById(id, connection);
        }
    }

    private String normalizeNumber(String number) {
        if (number == null || number.isBlank())
            throw new IllegalArgumentException("Vehicle number is required");
        String normalized = number.trim().toUpperCase(java.util.Locale.ROOT);
        if (normalized.length() > 20 || !normalized.matches("[A-Z0-9-]+"))
            throw new IllegalArgumentException("Invalid vehicle number format");
        return normalized;
    }

    private void validateVehicle(Vehicle v) {
        if (v == null) throw new IllegalArgumentException("Vehicle cannot be null");
        normalizeNumber(v.getVehicleNumber());
        if (v.getVehicleType() == null || !TYPES.contains(v.getVehicleType()))
            throw new IllegalArgumentException("Invalid vehicle type. Use: " + TYPES);
        if (v.getBrand() == null || v.getBrand().isBlank())
            throw new IllegalArgumentException("Brand is required");
        if (v.getModel() == null || v.getModel().isBlank())
            throw new IllegalArgumentException("Model is required");
        if (v.getColor() == null || v.getColor().isBlank())
            throw new IllegalArgumentException("Color is required");
        if (v.getOwnerName() == null || v.getOwnerName().isBlank())
            throw new IllegalArgumentException("Owner name is required");
        if (v.getContactNumber() == null || !v.getContactNumber().matches("[0-9+ -]{7,20}"))
            throw new IllegalArgumentException("Valid contact number is required");
    }
}
