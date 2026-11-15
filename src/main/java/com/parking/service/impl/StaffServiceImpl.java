package com.parking.service.impl;

import com.parking.model.Staff;
import com.parking.repository.custom.StaffRepository;
import com.parking.service.StaffService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final DataSource dataSource;

    public StaffServiceImpl(StaffRepository staffRepository, DataSource dataSource) {
        this.staffRepository = staffRepository;
        this.dataSource = dataSource;
    }

    @Override
    public Staff createStaff(Staff staff) throws SQLException {
        validate(staff);
        try (Connection connection = dataSource.getConnection()) {
            if (!staffRepository.create(staff, connection))
                throw new IllegalStateException("Staff creation failed");
            return staff;
        }
    }

    @Override
    public List<Staff> getAllStaff() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return staffRepository.findAll(connection);
        }
    }

    @Override
    public Staff getStaffById(Integer staffId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return staffRepository.findById(staffId, connection)
                    .orElseThrow(() -> new NoSuchElementException("Staff member not found: " + staffId));
        }
    }

    @Override
    public boolean updateStaff(Integer staffId, Staff staff) throws SQLException {
        validate(staff);
        try (Connection connection = dataSource.getConnection()) {
            return staffRepository.update(staffId, staff, connection);
        }
    }

    @Override
    public boolean deleteStaff(Integer staffId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return staffRepository.deleteById(staffId, connection);
        }
    }

    private void validate(Staff s) {
        if (s == null) throw new IllegalArgumentException("Staff details are required");
        if (s.getName() == null || s.getName().isBlank() || s.getName().length() > 100)
            throw new IllegalArgumentException("Enter a valid name");
        if (s.getRole() == null || s.getRole().isBlank() || s.getRole().length() > 50)
            throw new IllegalArgumentException("Enter a valid role");
        if (s.getShift() != null && s.getShift().length() > 30)
            throw new IllegalArgumentException("Shift is too long");
        if (s.getPhone() != null && !s.getPhone().isBlank() && !s.getPhone().matches("[0-9+ -]{7,20}"))
            throw new IllegalArgumentException("Enter a valid phone number");
        if (s.getEmail() != null && !s.getEmail().isBlank()
                && (s.getEmail().length() > 100 || !s.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")))
            throw new IllegalArgumentException("Enter a valid email");
    }
}
