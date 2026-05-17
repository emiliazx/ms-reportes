package com.costuras.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Respuesta unificada de MS-Reportes.
 * Contiene los 4 reportes calculados a partir de una sola consulta a MS-Venta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {

    private LocalDate desde;
    private LocalDate hasta;
    private Integer totalVentas;

    // Reporte 1 - Total ingresos (solo ventas PAGADAS)
    private BigDecimal totalIngresos;

    // Reporte 2 - Ventas por período agrupadas por día
    private Map<String, ResumenDia> ventasPorDia;

    // Reporte 3 - Productos más vendidos (ranking por cant total)
    private List<ProductoVendido> productosMasVendidos;

    // Reporte 4 - Distribución por estado
    private Map<String, Long> distribucionEstados;

    // ─── Clases internas ────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenDia {
        private String fecha;           // "2025-03-15"
        private Long cantidadVentas;
        private BigDecimal totalDia;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoVendido {
        private String idProducto;
        private Long cantidadTotal;     // suma de cant de todos los items
        private BigDecimal ingresoTotal;
    }
}
