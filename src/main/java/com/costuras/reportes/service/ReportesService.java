package com.costuras.reportes.service;

import com.costuras.reportes.client.VentaClient;
import com.costuras.reportes.dto.ReporteResponse;
import com.costuras.reportes.dto.VentaInternaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportesService {

    private final VentaClient ventaClient;

    /**
    
     * @param bearerToken token JWT del ADMIN (se reenvía a MS-Venta)
     * @param desde       fecha inicio
     * @param hasta       fecha fin
     */
    public ReporteResponse generarReporte(String bearerToken, LocalDate desde, LocalDate hasta) {

      
        List<VentaInternaResponse> ventas = ventaClient.getVentasEnRango(bearerToken, desde, hasta);

        
        BigDecimal totalIngresos = ventas.stream()
                .filter(v -> "PAGADA".equals(v.getEstado()))
                .map(VentaInternaResponse::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

       
        Map<String, ReporteResponse.ResumenDia> ventasPorDia = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getFecha().toLocalDate().toString(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> ReporteResponse.ResumenDia.builder()
                                        .fecha(lista.get(0).getFecha().toLocalDate().toString())
                                        .cantidadVentas((long) lista.size())
                                        .totalDia(lista.stream()
                                                .filter(v -> "PAGADA".equals(v.getEstado()))
                                                .map(VentaInternaResponse::getTotal)
                                                .filter(Objects::nonNull)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                                        .build()
                        )
                ));

        
        Map<String, long[]> acumuladoPorProducto = new HashMap<>();

        for (VentaInternaResponse venta : ventas) {
            if (venta.getItems() == null) continue;
            for (VentaInternaResponse.ItemVentaDto item : venta.getItems()) {
                acumuladoPorProducto.merge(
                        item.getIdProducto(),
                        new long[]{item.getCantidad()},
                        (existing, nuevo) -> {
                            existing[0] += nuevo[0];
                            return existing;
                        }
                );
            }
        }

      
        Map<String, BigDecimal> ingresoPorProducto = new HashMap<>();
        for (VentaInternaResponse venta : ventas) {
            if (venta.getItems() == null) continue;
            for (VentaInternaResponse.ItemVentaDto item : venta.getItems()) {
                BigDecimal subtotal = item.getPrecio()
                        .multiply(BigDecimal.valueOf(item.getCantidad()));
                ingresoPorProducto.merge(item.getIdProducto(), subtotal, BigDecimal::add);
            }
        }

        List<ReporteResponse.ProductoVendido> productosMasVendidos = acumuladoPorProducto
                .entrySet().stream()
                .map(e -> ReporteResponse.ProductoVendido.builder()
                        .idProducto(e.getKey())
                        .cantidadTotal(e.getValue()[0])
                        .ingresoTotal(ingresoPorProducto.getOrDefault(e.getKey(), BigDecimal.ZERO))
                        .build())
                .sorted(Comparator.comparingLong(ReporteResponse.ProductoVendido::getCantidadTotal).reversed())
                .toList();

      
        Map<String, Long> distribucionEstados = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getEstado() != null ? v.getEstado() : "DESCONOCIDO",
                        Collectors.counting()
                ));

       
        return ReporteResponse.builder()
                .desde(desde)
                .hasta(hasta)
                .totalVentas(ventas.size())
                .totalIngresos(totalIngresos)
                .ventasPorDia(ventasPorDia)
                .productosMasVendidos(productosMasVendidos)
                .distribucionEstados(distribucionEstados)
                .build();
    }
}
