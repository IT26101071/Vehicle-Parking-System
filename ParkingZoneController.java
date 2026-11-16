package com.parking.controller;

import com.parking.dto.CreateParkingZoneRequest;
import com.parking.model.ParkingZone;
import com.parking.service.ParkingZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ParkingZoneController {
    private final ParkingZoneService zoneService;

    public ParkingZoneController(ParkingZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public ResponseEntity<ParkingZone> createZone(@RequestBody CreateParkingZoneRequest request) throws SQLException {
        ParkingZone created = zoneService.createZone(toZone(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ParkingZone>> getAllZones() throws SQLException {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/{zoneCode}")
    public ResponseEntity<ParkingZone> getZoneById(@PathVariable String zoneCode) throws SQLException {
        return ResponseEntity.ok(zoneService.getZoneById(zoneCode));
    }

    @PutMapping("/{zoneCode}")
    public ResponseEntity<Void> updateZone(@PathVariable String zoneCode,
                                           @RequestBody CreateParkingZoneRequest request) throws SQLException {
        return zoneService.updateZone(zoneCode, toZone(request))
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{zoneCode}")
    public ResponseEntity<Void> deleteZone(@PathVariable String zoneCode) throws SQLException {
        return zoneService.deleteZone(zoneCode)
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private ParkingZone toZone(CreateParkingZoneRequest r) {
        return new ParkingZone(r.getZoneCode(), r.getZoneName(), r.getFloorNumber(), r.getDescription());
    }
}
