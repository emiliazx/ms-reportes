package com.costuras.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {

    private LocalDate desde;
    private LocalDate hasta;
    private Integer totalVentas;

    
    private BigDecimal totalIngresos;

    private Map<String, ResumenDia> ventasPorDia;

   
    private List<ProductoVendido> productosMasVendidos;

    
    private Map<String, Long> distribucionEstados;

   
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenDia {
        private String fecha;           
        private Long cantidadVentas;
        private BigDecimal totalDia;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoVendido {
        private String idProducto;
        private Long cantidadTotal;     
        private BigDecimal ingresoTotal;
    }
}
