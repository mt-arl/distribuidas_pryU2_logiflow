package com.logiflow.billing_service.dto;

import java.math.BigDecimal;

public class ReporteZonaDto {
    private String zona;
    private Long totalFacturas;
    private BigDecimal montoTotal;

    public ReporteZonaDto() {
    }

    public ReporteZonaDto(String zona, Long totalFacturas, BigDecimal montoTotal) {
        this.zona = zona;
        this.totalFacturas = totalFacturas;
        this.montoTotal = montoTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
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

    public static class Builder {
        private String zona;
        private Long totalFacturas;
        private BigDecimal montoTotal;

        public Builder zona(String zona) {
            this.zona = zona;
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

        public ReporteZonaDto build() {
            return new ReporteZonaDto(zona, totalFacturas, montoTotal);
        }
    }
}
