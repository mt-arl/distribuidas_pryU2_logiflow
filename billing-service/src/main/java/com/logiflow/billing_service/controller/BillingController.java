package com.logiflow.billing_service.controller;

import com.logiflow.billing_service.dto.CalculateFareRequest;
import com.logiflow.billing_service.dto.InvoiceResponseDto;
import com.logiflow.billing_service.dto.ReporteDiarioDto;
import com.logiflow.billing_service.dto.ReporteTipoVehiculoDto;
import com.logiflow.billing_service.dto.ReporteZonaDto;
import com.logiflow.billing_service.service.BillingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@Validated
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateFare(@Valid @RequestBody CalculateFareRequest request) {
        BigDecimal amount = billingService.calculateFare(request);
        return ResponseEntity.ok(amount);
    }

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDto> createInvoice(
            @RequestParam(name = "customerId") String customerId,
            @Valid @RequestBody CalculateFareRequest request) {
        InvoiceResponseDto dto = billingService.createInvoice(customerId, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reportes/diario")
    public ResponseEntity<List<ReporteDiarioDto>> getReporteDiario(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<ReporteDiarioDto> reporte = billingService.getReporteDiario(fecha);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/reportes/zona")
    public ResponseEntity<List<ReporteZonaDto>> getReportePorZona() {
        List<ReporteZonaDto> reporte = billingService.getReportePorZona();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/reportes/tipo-vehiculo")
    public ResponseEntity<List<ReporteTipoVehiculoDto>> getReportePorTipoVehiculo() {
        List<ReporteTipoVehiculoDto> reporte = billingService.getReportePorTipoVehiculo();
        return ResponseEntity.ok(reporte);
    }
}
