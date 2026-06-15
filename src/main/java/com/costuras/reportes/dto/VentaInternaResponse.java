package com.costuras.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaInternaResponse {

    private String idVenta;
    private Integer idCliente;
    private LocalDateTime fecha;
    private String estado;          
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
