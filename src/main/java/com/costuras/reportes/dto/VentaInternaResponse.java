package com.costuras.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Espejo del VentaInternaResponse de MS-Venta.
 * MS-Reportes deserializa la respuesta de MS-Venta en esta clase.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaInternaResponse {

    private String idVenta;
    private Integer idCliente;
    private LocalDateTime fecha;
    private String estado;           // "PAGADA", "PENDIENTE", "CANCELADA"
    private BigDecimal total;
    private List<ItemVentaDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVentaDto {
        private String idProducto;
        private Integer cantidad;
        private BigDecimal precio;
    }
}
