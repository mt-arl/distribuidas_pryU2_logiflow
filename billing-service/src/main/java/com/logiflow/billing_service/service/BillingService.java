package com.logiflow.billing_service.service;

import com.logiflow.billing_service.dto.CalculateFareRequest;
import com.logiflow.billing_service.dto.InvoiceResponseDto;
import com.logiflow.billing_service.dto.ReporteDiarioDto;
import com.logiflow.billing_service.dto.ReporteTipoVehiculoDto;
import com.logiflow.billing_service.dto.ReporteZonaDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BillingService {
    BigDecimal calculateFare(CalculateFareRequest request);

    InvoiceResponseDto createInvoice(String customerId, CalculateFareRequest request);

    List<ReporteDiarioDto> getReporteDiario(LocalDate fecha);

    List<ReporteZonaDto> getReportePorZona();

    List<ReporteTipoVehiculoDto> getReportePorTipoVehiculo();
}
