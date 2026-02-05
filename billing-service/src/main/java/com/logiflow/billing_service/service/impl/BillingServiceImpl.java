package com.logiflow.billing_service.service.impl;

import com.logiflow.billing_service.dto.CalculateFareRequest;
import com.logiflow.billing_service.dto.InvoiceResponseDto;
import com.logiflow.billing_service.dto.ReporteDiarioDto;
import com.logiflow.billing_service.dto.ReporteTipoVehiculoDto;
import com.logiflow.billing_service.dto.ReporteZonaDto;
import com.logiflow.billing_service.enums.InvoiceState;
import com.logiflow.billing_service.model.Invoice;
import com.logiflow.billing_service.service.BillingService;
import com.logiflow.billing_service.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillingServiceImpl implements BillingService {

    // parámetros simples para el cálculo
    private static final BigDecimal BASE_FARE = new BigDecimal("2.50");
    private static final BigDecimal PER_KM = new BigDecimal("1.20");
    private static final BigDecimal PER_MIN = new BigDecimal("0.25");

    // Factores dinámicos por zona
    private static final Map<String, BigDecimal> ZONA_FACTOR = Map.of(
            "URBANA", new BigDecimal("1.0"),
            "SUBURBANA", new BigDecimal("1.2"),
            "RURAL", new BigDecimal("1.5"));

    // Factores dinámicos por tipo de vehículo
    private static final Map<String, BigDecimal> VEHICULO_FACTOR = Map.of(
            "MOTORIZADO", new BigDecimal("1.0"),
            "LIVIANO", new BigDecimal("1.3"),
            "CAMION", new BigDecimal("2.0"));

    private final InvoiceRepository invoiceRepository;

    public BillingServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public BigDecimal calculateFare(CalculateFareRequest request) {
        BigDecimal distance = request.getDistanceKm() == null ? BigDecimal.ZERO : request.getDistanceKm();
        BigDecimal duration = request.getDurationMin() == null ? BigDecimal.ZERO : request.getDurationMin();

        // Tarifa base
        BigDecimal fare = BASE_FARE
                .add(PER_KM.multiply(distance))
                .add(PER_MIN.multiply(duration));

        // Aplicar factor dinámico por zona
        String zona = request.getZona() != null ? request.getZona().toUpperCase() : "URBANA";
        BigDecimal zonaFactor = ZONA_FACTOR.getOrDefault(zona, BigDecimal.ONE);
        fare = fare.multiply(zonaFactor);

        // Aplicar factor dinámico por tipo de vehículo
        String tipoVehiculo = request.getTipoVehiculo() != null ? request.getTipoVehiculo().toUpperCase()
                : "MOTORIZADO";
        BigDecimal vehiculoFactor = VEHICULO_FACTOR.getOrDefault(tipoVehiculo, BigDecimal.ONE);
        fare = fare.multiply(vehiculoFactor);

        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public InvoiceResponseDto createInvoice(String customerId, CalculateFareRequest request) {
        BigDecimal amount = calculateFare(request);
        Invoice invoice = new Invoice();
        invoice.setCustomerId(customerId);
        invoice.setAmount(amount);
        invoice.setState(InvoiceState.BORRADOR);
        invoice.setZona(request.getZona() != null ? request.getZona().toUpperCase() : "URBANA");
        invoice.setTipoVehiculo(
                request.getTipoVehiculo() != null ? request.getTipoVehiculo().toUpperCase() : "MOTORIZADO");

        Invoice saved = invoiceRepository.save(invoice);

        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setId(saved.getId());
        dto.setAmount(saved.getAmount());
        dto.setCustomerId(saved.getCustomerId());
        dto.setState(saved.getState());
        dto.setCreatedAt(saved.getCreatedAt());

        return dto;
    }

    @Override
    public List<ReporteDiarioDto> getReporteDiario(LocalDate fecha) {
        List<Invoice> invoices = invoiceRepository.findAll();

        Map<LocalDate, List<Invoice>> groupedByDate = invoices.stream()
                .filter(inv -> {
                    LocalDate invDate = inv.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                    return fecha == null || invDate.equals(fecha);
                })
                .collect(Collectors.groupingBy(inv -> inv.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate()));

        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    List<Invoice> dayInvoices = entry.getValue();
                    BigDecimal total = dayInvoices.stream()
                            .map(Invoice::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal promedio = dayInvoices.isEmpty() ? BigDecimal.ZERO
                            : total.divide(new BigDecimal(dayInvoices.size()), 2, RoundingMode.HALF_UP);

                    return ReporteDiarioDto.builder()
                            .fecha(entry.getKey())
                            .totalFacturas((long) dayInvoices.size())
                            .montoTotal(total)
                            .montoPromedio(promedio)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteZonaDto> getReportePorZona() {
        List<Invoice> invoices = invoiceRepository.findAll();

        Map<String, List<Invoice>> groupedByZona = invoices.stream()
                .collect(Collectors.groupingBy(inv -> inv.getZona() != null ? inv.getZona() : "DESCONOCIDA"));

        return groupedByZona.entrySet().stream()
                .map(entry -> {
                    List<Invoice> zonaInvoices = entry.getValue();
                    BigDecimal total = zonaInvoices.stream()
                            .map(Invoice::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return ReporteZonaDto.builder()
                            .zona(entry.getKey())
                            .totalFacturas((long) zonaInvoices.size())
                            .montoTotal(total)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteTipoVehiculoDto> getReportePorTipoVehiculo() {
        List<Invoice> invoices = invoiceRepository.findAll();

        Map<String, List<Invoice>> groupedByTipo = invoices.stream()
                .collect(Collectors
                        .groupingBy(inv -> inv.getTipoVehiculo() != null ? inv.getTipoVehiculo() : "DESCONOCIDO"));

        return groupedByTipo.entrySet().stream()
                .map(entry -> {
                    List<Invoice> tipoInvoices = entry.getValue();
                    BigDecimal total = tipoInvoices.stream()
                            .map(Invoice::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal promedio = tipoInvoices.isEmpty() ? BigDecimal.ZERO
                            : total.divide(new BigDecimal(tipoInvoices.size()), 2, RoundingMode.HALF_UP);

                    return ReporteTipoVehiculoDto.builder()
                            .tipoVehiculo(entry.getKey())
                            .totalFacturas((long) tipoInvoices.size())
                            .montoTotal(total)
                            .montoPromedio(promedio)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
