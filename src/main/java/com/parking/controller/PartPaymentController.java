package com.parking.controller;

import com.parking.dto.InstallmentRequest;
import com.parking.dto.PartPaymentRequest;
import com.parking.model.PartPayment;
import com.parking.service.PartPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/part-payments")
public class PartPaymentController {

    private final PartPaymentService partPaymentService;

    public PartPaymentController(PartPaymentService partPaymentService) {
        this.partPaymentService = partPaymentService;
    }

    @PostMapping
    public ResponseEntity<PartPayment> createPartPayment(@RequestBody PartPaymentRequest request) throws SQLException {
        PartPayment created = partPaymentService.createPartPayment(toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PartPayment>> getAllPartPayments(
            @RequestParam(required = false) String vehicleNumber,
            @RequestParam(required = false) String status) throws SQLException {
        if (vehicleNumber != null && !vehicleNumber.isBlank()) {
            return ResponseEntity.ok(partPaymentService.getPartPaymentsByVehicle(vehicleNumber));
        }
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(partPaymentService.getPartPaymentsByStatus(status));
        }
        return ResponseEntity.ok(partPaymentService.getAllPartPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PartPayment> getPartPaymentById(@PathVariable String paymentId) throws SQLException {
        return ResponseEntity.ok(partPaymentService.getPartPaymentById(paymentId));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<Void> updatePartPayment(@PathVariable String paymentId,
                                                  @RequestBody PartPaymentRequest request) throws SQLException {
        return partPaymentService.updatePartPayment(paymentId, toEntity(request))
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{paymentId}/installment")
    public ResponseEntity<PartPayment> addInstallment(@PathVariable String paymentId,
                                                      @RequestBody InstallmentRequest request) throws SQLException {
        PartPayment updated = partPaymentService.addInstallment(
                paymentId,
                request.getAdditionalAmount(),
                request.getPaymentMethod(),
                request.getNotes()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePartPayment(@PathVariable String paymentId) throws SQLException {
        return partPaymentService.deletePartPayment(paymentId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private PartPayment toEntity(PartPaymentRequest req) {
        PartPayment payment = new PartPayment();
        payment.setPaymentId(req.getPaymentId());
        payment.setVehicleNumber(req.getVehicleNumber());
        payment.setCustomerName(req.getCustomerName());
        payment.setTotalAmount(req.getTotalAmount());
        payment.setPaidAmount(req.getPaidAmount());
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setNotes(req.getNotes());
        return payment;
    }
}
