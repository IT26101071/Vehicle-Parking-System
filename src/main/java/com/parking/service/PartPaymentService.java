package com.parking.service;

import com.parking.model.PartPayment;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface PartPaymentService {
    PartPayment createPartPayment(PartPayment payment) throws SQLException;
    List<PartPayment> getAllPartPayments() throws SQLException;
    PartPayment getPartPaymentById(String paymentId) throws SQLException;
    List<PartPayment> getPartPaymentsByVehicle(String vehicleNumber) throws SQLException;
    List<PartPayment> getPartPaymentsByStatus(String status) throws SQLException;
    boolean updatePartPayment(String paymentId, PartPayment payment) throws SQLException;
    PartPayment addInstallment(String paymentId, BigDecimal additionalAmount, String paymentMethod, String notes) throws SQLException;
    boolean deletePartPayment(String paymentId) throws SQLException;
}
