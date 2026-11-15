package com.parking.repository.custom.impl;

import com.parking.model.Staff;
import com.parking.repository.custom.StaffRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StaffRepositoryImpl implements StaffRepository {

    @Override
    public boolean create(Staff staff, Connection connection) throws SQLException {
        String sql = "INSERT INTO staff (name, email, phone, `role`, shift) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getRole());
            ps.setString(5, staff.getShift());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) staff.setStaffId(keys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public List<Staff> findAll(Connection connection) throws SQLException {
        List<Staff> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM staff ORDER BY staff_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStaff(rs));
        }
        return list;
    }

    @Override
    public Optional<Staff> findById(Integer id, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM staff WHERE staff_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapStaff(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public boolean update(Integer id, Staff staff, Connection connection) throws SQLException {
        String sql = "UPDATE staff SET name = ?, email = ?, phone = ?, `role` = ?, shift = ? WHERE staff_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getRole());
            ps.setString(5, staff.getShift());
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM staff WHERE staff_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Staff mapStaff(ResultSet rs) throws SQLException {
        return new Staff(rs.getInt("staff_id"), rs.getString("name"), rs.getString("email"),
                rs.getString("phone"), rs.getString("role"), rs.getString("shift"));
    }
}
