package com.parking.service.impl;

import com.parking.model.PartPayment;
import com.parking.repository.custom.PartPaymentRepository;
import com.parking.service.PartPaymentService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class PartPaymentServiceImpl implements PartPaymentService {

    private static final Set<String> PAYMENT_METHODS = Set.of("CASH", "CARD", "BANK_TRANSFER", "ONLINE");
    private static final Set<String> STATUSES = Set.of("UNPAID", "PARTIAL", "PAID");

    private final PartPaymentRepository partPaymentRepository;
    private final DataSource dataSource;

    public PartPaymentServiceImpl(PartPaymentRepository partPaymentRepository, DataSource dataSource) {
        this.partPaymentRepository = partPaymentRepository;
        this.dataSource = dataSource;
    }

    @Override
    public PartPayment createPartPayment(PartPayment payment) throws SQLException {
        validateAndCalculate(payment, true);
        try (Connection connection = dataSource.getConnection()) {
            if (partPaymentRepository.findById(payment.getPaymentId(), connection).isPresent()) {
                throw new IllegalArgumentException("Payment ID already exists: " + payment.getPaymentId());
            }
            if (!partPaymentRepository.create(payment, connection)) {
                throw new IllegalStateException("Failed to create part payment record");
            }
            return payment;
        }
    }

    @Override
    public List<PartPayment> getAllPartPayments() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return partPaymentRepository.findAll(connection);
        }
    }

    @Override
    public PartPayment getPartPaymentById(String paymentId) throws SQLException {
        String id = normalizeId(paymentId);
        try (Connection connection = dataSource.getConnection()) {
            return partPaymentRepository.findById(id, connection)
                    .orElseThrow(() -> new NoSuchElementException("Part payment record not found: " + id));
        }
    }

    @Override
    public List<PartPayment> getPartPaymentsByVehicle(String vehicleNumber) throws SQLException {
        String vehicle = normalizeVehicleNumber(vehicleNumber);
        try (Connection connection = dataSource.getConnection()) {
            return partPaymentRepository.findByVehicleNumber(vehicle, connection);
        }
    }

    @Override
    public List<PartPayment> getPartPaymentsByStatus(String status) throws SQLException {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Payment status is required");
        }
        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException("Invalid status. Use: " + STATUSES);
        }
        try (Connection connection = dataSource.getConnection()) {
            return partPaymentRepository.findByStatus(normalizedStatus, connection);
        }
    }

    @Override
    public boolean updatePartPayment(String paymentId, PartPayment payment) throws SQLException {
        String id = normalizeId(paymentId);
        payment.setPaymentId(id);
        validateAndCalculate(payment, false);
        try (Connection connection = dataSource.getConnection()) {
            if (partPaymentRepository.findById(id, connection).isEmpty()) {
                throw new NoSuchElementException("Part payment record not found: " + id);
            }
            return partPaymentRepository.update(id, payment, connection);
        }
    }

    @Override
    public PartPayment addInstallment(String paymentId, BigDecimal additionalAmount, String paymentMethod, String notes) throws SQLException {
        String id = normalizeId(paymentId);
        if (additionalAmount == null || additionalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Installment amount must be greater than zero");
        }
        try (Connection connection = dataSource.getConnection()) {
            PartPayment existing = partPaymentRepository.findById(id, connection)
                    .orElseThrow(() -> new NoSuchElementException("Part payment record not found: " + id));

            if ("PAID".equals(existing.getPaymentStatus())) {
                throw new IllegalArgumentException("This payment is already settled in full");
            }

            BigDecimal newPaid = existing.getPaidAmount().add(additionalAmount).setScale(2, RoundingMode.HALF_UP);
            if (newPaid.compareTo(existing.getTotalAmount()) > 0) {
                throw new IllegalArgumentException("Total paid amount (" + newPaid + ") cannot exceed total amount (" + existing.getTotalAmount() + ")");
            }

            existing.setPaidAmount(newPaid);
            if (paymentMethod != null && !paymentMethod.isBlank()) {
                existing.setPaymentMethod(validateMethod(paymentMethod));
            }
            if (notes != null && !notes.isBlank()) {
                existing.setNotes(notes.trim());
            }
            existing.setPaymentDate(LocalDateTime.now());
            recalculateBalanceAndStatus(existing);

            if (!partPaymentRepository.update(id, existing, connection)) {
                throw new IllegalStateException("Failed to record installment payment");
            }
            return existing;
        }
    }

    @Override
    public boolean deletePartPayment(String paymentId) throws SQLException {
        String id = normalizeId(paymentId);
        try (Connection connection = dataSource.getConnection()) {
            return partPaymentRepository.deleteById(id, connection);
        }
    }

    private void validateAndCalculate(PartPayment p, boolean isCreate) {
        if (p == null) {
            throw new IllegalArgumentException("Part payment payload cannot be null");
        }
        if (isCreate && (p.getPaymentId() == null || p.getPaymentId().isBlank())) {
            String autoId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
            p.setPaymentId(autoId);
        } else {
            p.setPaymentId(normalizeId(p.getPaymentId()));
        }

        p.setVehicleNumber(normalizeVehicleNumber(p.getVehicleNumber()));

        if (p.getCustomerName() == null || p.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        p.setCustomerName(p.getCustomerName().trim());

        if (p.getTotalAmount() == null || p.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        if (p.getPaidAmount() == null || p.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Paid amount cannot be negative");
        }
        if (p.getPaidAmount().compareTo(p.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("Paid amount cannot exceed total amount");
        }

        p.setTotalAmount(p.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        p.setPaidAmount(p.getPaidAmount().setScale(2, RoundingMode.HALF_UP));
        p.setPaymentMethod(validateMethod(p.getPaymentMethod()));
        p.setPaymentDate(LocalDateTime.now());
        if (p.getNotes() != null) {
            p.setNotes(p.getNotes().trim());
        }

        recalculateBalanceAndStatus(p);
    }

    private void recalculateBalanceAndStatus(PartPayment p) {
        BigDecimal balance = p.getTotalAmount().subtract(p.getPaidAmount()).setScale(2, RoundingMode.HALF_UP);
        p.setBalanceAmount(balance);

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            p.setPaymentStatus("PAID");
        } else if (p.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            p.setPaymentStatus("UNPAID");
        } else {
            p.setPaymentStatus("PARTIAL");
        }
    }

    private String validateMethod(String method) {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("Payment method is required. Use: " + PAYMENT_METHODS);
        }
        String normalized = method.trim().toUpperCase(Locale.ROOT);
        if (!PAYMENT_METHODS.contains(normalized)) {
            throw new IllegalArgumentException("Invalid payment method. Use: " + PAYMENT_METHODS);
        }
        return normalized;
    }

    private String normalizeId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() > 20 || !normalized.matches("[A-Z0-9-]+")) {
            throw new IllegalArgumentException("Invalid payment ID format");
        }
        return normalized;
    }

    private String normalizeVehicleNumber(String number) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Vehicle number is required");
        }
        String normalized = number.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() > 20 || !normalized.matches("[A-Z0-9-]+")) {
            throw new IllegalArgumentException("Invalid vehicle number format");
        }
        return normalized;
    }
}
