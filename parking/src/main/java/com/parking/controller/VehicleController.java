package com.parking.controller;

import com.parking.dto.CreateVehicleRequest;
import com.parking.model.Vehicle;
import com.parking.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody CreateVehicleRequest request) throws SQLException {
        Vehicle created = vehicleService.createVehicle(toVehicle(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() throws SQLException {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{vehicleNumber}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String vehicleNumber) throws SQLException {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleNumber));
    }

    @PutMapping("/{vehicleNumber}")
    public ResponseEntity<Void> updateVehicle(@PathVariable String vehicleNumber,
                                              @RequestBody CreateVehicleRequest request) throws SQLException {
        return vehicleService.updateVehicle(vehicleNumber, toVehicle(request))
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{vehicleNumber}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String vehicleNumber) throws SQLException {
        return vehicleService.deleteVehicle(vehicleNumber)
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Vehicle toVehicle(CreateVehicleRequest r) {
        return new Vehicle(r.getVehicleNumber(), r.getVehicleType(), r.getBrand(),
                r.getModel(), r.getColor(), r.getOwnerName(), r.getContactNumber());
    }
}
