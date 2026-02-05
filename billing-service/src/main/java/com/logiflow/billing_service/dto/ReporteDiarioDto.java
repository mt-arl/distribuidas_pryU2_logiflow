package com.logiflow.billing_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteDiarioDto {
    private LocalDate fecha;
    private Long totalFacturas;
    private BigDecimal montoTotal;
    private BigDecimal montoPromedio;

    public ReporteDiarioDto() {
    }

    public ReporteDiarioDto(LocalDate fecha, Long totalFacturas, BigDecimal montoTotal, BigDecimal montoPromedio) {
        this.fecha = fecha;
        this.totalFacturas = totalFacturas;
        this.montoTotal = montoTotal;
        this.montoPromedio = montoPromedio;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getTotalFacturas() {
        return totalFacturas;
    }

    public void setTotalFacturas(Long totalFacturas) {
        this.totalFacturas = totalFacturas;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoPromedio() {
        return montoPromedio;
    }

    public void setMontoPromedio(BigDecimal montoPromedio) {
        this.montoPromedio = montoPromedio;
    }

    public static class Builder {
        private LocalDate fecha;
        private Long totalFacturas;
        private BigDecimal montoTotal;
        private BigDecimal montoPromedio;

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder totalFacturas(Long totalFacturas) {
            this.totalFacturas = totalFacturas;
            return this;
        }

        public Builder montoTotal(BigDecimal montoTotal) {
            this.montoTotal = montoTotal;
            return this;
        }

        public Builder montoPromedio(BigDecimal montoPromedio) {
            this.montoPromedio = montoPromedio;
            return this;
        }

        public ReporteDiarioDto build() {
            return new ReporteDiarioDto(fecha, totalFacturas, montoTotal, montoPromedio);
        }
    }
}
