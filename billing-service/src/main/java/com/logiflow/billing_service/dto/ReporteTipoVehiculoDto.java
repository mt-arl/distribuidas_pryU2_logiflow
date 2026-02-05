package com.logiflow.billing_service.dto;

import java.math.BigDecimal;

public class ReporteTipoVehiculoDto {
    private String tipoVehiculo;
    private Long totalFacturas;
    private BigDecimal montoTotal;
    private BigDecimal montoPromedio;

    public ReporteTipoVehiculoDto() {
    }

    public ReporteTipoVehiculoDto(String tipoVehiculo, Long totalFacturas, BigDecimal montoTotal,
            BigDecimal montoPromedio) {
        this.tipoVehiculo = tipoVehiculo;
        this.totalFacturas = totalFacturas;
        this.montoTotal = montoTotal;
        this.montoPromedio = montoPromedio;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
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
        private String tipoVehiculo;
        private Long totalFacturas;
        private BigDecimal montoTotal;
        private BigDecimal montoPromedio;

        public Builder tipoVehiculo(String tipoVehiculo) {
            this.tipoVehiculo = tipoVehiculo;
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

        public ReporteTipoVehiculoDto build() {
            return new ReporteTipoVehiculoDto(tipoVehiculo, totalFacturas, montoTotal, montoPromedio);
        }
    }
}
