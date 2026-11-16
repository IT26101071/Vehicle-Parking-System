package com.parking.repository.custom.impl;

import com.parking.model.PartPayment;
import com.parking.repository.custom.PartPaymentRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PartPaymentRepositoryImpl implements PartPaymentRepository {

    @Override
    public boolean create(PartPayment payment, Connection connection) throws SQLException {
        String sql = "INSERT INTO part_payments (payment_id, vehicle_number, customer_name, total_amount, paid_amount, balance_amount, payment_method, payment_status, payment_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, payment.getPaymentId());
            ps.setString(2, payment.getVehicleNumber());
            ps.setString(3, payment.getCustomerName());
            ps.setBigDecimal(4, payment.getTotalAmount());
            ps.setBigDecimal(5, payment.getPaidAmount());
            ps.setBigDecimal(6, payment.getBalanceAmount());
            ps.setString(7, payment.getPaymentMethod());
            ps.setString(8, payment.getPaymentStatus());
            ps.setTimestamp(9, Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(10, payment.getNotes());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<PartPayment> findAll(Connection connection) throws SQLException {
        List<PartPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM part_payments ORDER BY payment_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapPartPayment(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<PartPayment> findById(String paymentId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM part_payments WHERE payment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapPartPayment(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<PartPayment> findByVehicleNumber(String vehicleNumber, Connection connection) throws SQLException {
        List<PartPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM part_payments WHERE vehicle_number = ? ORDER BY payment_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehicleNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPartPayment(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<PartPayment> findByStatus(String status, Connection connection) throws SQLException {
        List<PartPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM part_payments WHERE payment_status = ? ORDER BY payment_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPartPayment(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean update(String paymentId, PartPayment payment, Connection connection) throws SQLException {
        String sql = "UPDATE part_payments SET vehicle_number = ?, customer_name = ?, total_amount = ?, paid_amount = ?, balance_amount = ?, payment_method = ?, payment_status = ?, payment_date = ?, notes = ? WHERE payment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, payment.getVehicleNumber());
            ps.setString(2, payment.getCustomerName());
            ps.setBigDecimal(3, payment.getTotalAmount());
            ps.setBigDecimal(4, payment.getPaidAmount());
            ps.setBigDecimal(5, payment.getBalanceAmount());
            ps.setString(6, payment.getPaymentMethod());
            ps.setString(7, payment.getPaymentStatus());
            ps.setTimestamp(8, Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(9, payment.getNotes());
            ps.setString(10, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(String paymentId, Connection connection) throws SQLException {
        String sql = "DELETE FROM part_payments WHERE payment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    private PartPayment mapPartPayment(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("payment_date");
        return new PartPayment(
                rs.getString("payment_id"),
                rs.getString("vehicle_number"),
                rs.getString("customer_name"),
                rs.getBigDecimal("total_amount"),
                rs.getBigDecimal("paid_amount"),
                rs.getBigDecimal("balance_amount"),
                rs.getString("payment_method"),
                rs.getString("payment_status"),
                ts != null ? ts.toLocalDateTime() : null,
                rs.getString("notes")
        );
    }
}
