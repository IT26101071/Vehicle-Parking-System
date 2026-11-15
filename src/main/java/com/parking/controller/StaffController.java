package com.parking.controller;

import com.parking.dto.CreateStaffRequest;
import com.parking.model.Staff;
import com.parking.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody CreateStaffRequest request) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(toStaff(request)));
    }

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() throws SQLException {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Integer staffId) throws SQLException {
        return ResponseEntity.ok(staffService.getStaffById(staffId));
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<Void> updateStaff(@PathVariable Integer staffId,
                                            @RequestBody CreateStaffRequest request) throws SQLException {
        return staffService.updateStaff(staffId, toStaff(request))
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer staffId) throws SQLException {
        return staffService.deleteStaff(staffId)
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Staff toStaff(CreateStaffRequest r) {
        return new Staff(null, r.getName(), r.getEmail(), r.getPhone(), r.getRole(), r.getShift());
    }
}
