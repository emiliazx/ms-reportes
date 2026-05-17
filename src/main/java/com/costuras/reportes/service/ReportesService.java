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
     * Punto de entrada único.
     * Hace UNA llamada a MS-Venta y calcula los 4 reportes en memoria.
     *
     * @param bearerToken token JWT del ADMIN (se reenvía a MS-Venta)
     * @param desde       fecha inicio
     * @param hasta       fecha fin
     */
    public ReporteResponse generarReporte(String bearerToken, LocalDate desde, LocalDate hasta) {

        // ── 1. Obtener datos desde MS-Venta ────────────────────────────────
        List<VentaInternaResponse> ventas = ventaClient.getVentasEnRango(bearerToken, desde, hasta);

        // ── 2. Total ingresos (solo PAGADAS) ───────────────────────────────
        BigDecimal totalIngresos = ventas.stream()
                .filter(v -> "PAGADA".equals(v.getEstado()))
                .map(VentaInternaResponse::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ── 3. Ventas por día ──────────────────────────────────────────────
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

        // ── 4. Productos más vendidos ──────────────────────────────────────
        // Aplanar todos los items de todas las ventas y agrupar por idProducto
        Map<String, long[]> acumuladoPorProducto = new HashMap<>();

        for (VentaInternaResponse venta : ventas) {
            if (venta.getItems() == null) continue;
            for (VentaInternaResponse.ItemVentaDto item : venta.getItems()) {
                acumuladoPorProducto.merge(
                        item.getIdProducto(),
                        new long[]{item.getCant()},
                        (existing, nuevo) -> {
                            existing[0] += nuevo[0];
                            return existing;
                        }
                );
            }
        }

        // Calcular ingreso por producto (cant * precio)
        Map<String, BigDecimal> ingresoPorProducto = new HashMap<>();
        for (VentaInternaResponse venta : ventas) {
            if (venta.getItems() == null) continue;
            for (VentaInternaResponse.ItemVentaDto item : venta.getItems()) {
                BigDecimal subtotal = item.getPrecio()
                        .multiply(BigDecimal.valueOf(item.getCant()));
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

        // ── 5. Distribución por estado ─────────────────────────────────────
        Map<String, Long> distribucionEstados = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getEstado() != null ? v.getEstado() : "DESCONOCIDO",
                        Collectors.counting()
                ));

        // ── 6. Armar respuesta ─────────────────────────────────────────────
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
