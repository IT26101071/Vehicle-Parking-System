package com.parking.controller;

import com.parking.dto.CreateParkingSlotRequest;
import com.parking.model.ParkingSlot;
import com.parking.service.ParkingSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class ParkingSlotController {
    private final ParkingSlotService slotService;

    public ParkingSlotController(ParkingSlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    public ResponseEntity<ParkingSlot> createSlot(@RequestBody CreateParkingSlotRequest request) throws SQLException {
        ParkingSlot created = slotService.createSlot(toSlot(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAllSlots(@RequestParam(required = false) String status) throws SQLException {
        return ResponseEntity.ok(status == null ? slotService.getAllSlots() : slotService.getSlotsByStatus(status));
    }

    @GetMapping("/{slotNumber}")
    public ResponseEntity<ParkingSlot> getSlotById(@PathVariable String slotNumber) throws SQLException {
        return ResponseEntity.ok(slotService.getSlotById(slotNumber));
    }

    @PutMapping("/{slotNumber}")
    public ResponseEntity<Void> updateSlot(@PathVariable String slotNumber,
                                           @RequestBody CreateParkingSlotRequest request) throws SQLException {
        return slotService.updateSlot(slotNumber, toSlot(request))
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{slotNumber}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String slotNumber) throws SQLException {
        return slotService.deleteSlot(slotNumber)
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private ParkingSlot toSlot(CreateParkingSlotRequest r) {
        return new ParkingSlot(r.getSlotNumber(), r.getZoneCode(), r.getSlotType(),
                r.getStatus(), r.getHourlyRate());
    }

    @GetMapping("/sorted-by-availability")
    public ResponseEntity<List<ParkingSlot>>
    getSlotsSortedByAvailability() throws SQLException {

        return ResponseEntity.ok(
                slotService.getSlotsSortedByAvailability()
        );
    }

    @GetMapping("/next-available")
    public ResponseEntity<ParkingSlot>
    getNextAvailableSlot() throws SQLException {

        return ResponseEntity.ok(
                slotService.getNextAvailableSlot()
        );
    }
}
