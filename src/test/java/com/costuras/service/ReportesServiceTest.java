package com.costuras.service;

import com.costuras.reportes.client.VentaClient;
import com.costuras.reportes.dto.ReporteResponse;
import com.costuras.reportes.dto.VentaInternaResponse;
import com.costuras.reportes.service.ReportesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportesServiceTest {

    @Mock                          // ← corregido, era @MockitoBean
    private VentaClient ventaClient;

    @InjectMocks
    private ReportesService reportesService;

    private LocalDate desde;
    private LocalDate hasta;

    @BeforeEach
    void setUp() {
        desde = LocalDate.of(2025, 1, 1);
        hasta = LocalDate.of(2025, 1, 31);
    }

    @Test
    void generarReporte_sinVentas_retornaTotalesEnCero() {
        when(ventaClient.getVentasEnRango(any(), eq(desde), eq(hasta))).thenReturn(List.of());

        ReporteResponse result = reportesService.generarReporte("Bearer token", desde, hasta);

        assertEquals(0, result.getTotalVentas());
        assertEquals(BigDecimal.ZERO, result.getTotalIngresos());
        assertTrue(result.getProductosMasVendidos().isEmpty());
    }

    @Test
    void generarReporte_conVentasPagadas_sumaTotalIngresos() {
        VentaInternaResponse venta = new VentaInternaResponse();  // ← setter si no tiene @Builder
        venta.setIdVenta("v1");
        venta.setIdCliente(1);
        venta.setFecha(LocalDateTime.of(2025, 1, 10, 10, 0));
        venta.setEstado("PAGADA");
        venta.setTotal(new BigDecimal("50000"));
        venta.setItems(List.of());

        when(ventaClient.getVentasEnRango(any(), eq(desde), eq(hasta)))
                .thenReturn(List.of(venta));

        ReporteResponse result = reportesService.generarReporte("Bearer token", desde, hasta);

        assertEquals(1, result.getTotalVentas());
        assertEquals(new BigDecimal("50000"), result.getTotalIngresos());
    }

    @Test
    void generarReporte_ventaNoPageada_noSumaAIngresos() {
        VentaInternaResponse venta = new VentaInternaResponse();  // ← corregido sintaxis
        venta.setIdVenta("v2");
        venta.setFecha(LocalDateTime.of(2025, 1, 15, 12, 0));
        venta.setEstado("CANCELADA");
        venta.setTotal(new BigDecimal("20000"));
        venta.setItems(List.of());

        when(ventaClient.getVentasEnRango(any(), eq(desde), eq(hasta)))
                .thenReturn(List.of(venta));

        ReporteResponse result = reportesService.generarReporte("Bearer token", desde, hasta);

        assertEquals(1, result.getTotalVentas());
        assertEquals(BigDecimal.ZERO, result.getTotalIngresos());
    }

    @Test
    void generarReporte_distribuyeEstadosCorrectamente() {
        VentaInternaResponse v1 = new VentaInternaResponse();
        v1.setIdVenta("v1");
        v1.setFecha(LocalDateTime.now());
        v1.setEstado("PAGADA");
        v1.setTotal(BigDecimal.TEN);
        v1.setItems(List.of());

        VentaInternaResponse v2 = new VentaInternaResponse();
        v2.setIdVenta("v2");
        v2.setFecha(LocalDateTime.now());
        v2.setEstado("CANCELADA");
        v2.setTotal(BigDecimal.ZERO);
        v2.setItems(List.of());

        when(ventaClient.getVentasEnRango(any(), eq(desde), eq(hasta)))
                .thenReturn(List.of(v1, v2));

        ReporteResponse result = reportesService.generarReporte("Bearer token", desde, hasta);

        assertEquals(1L, result.getDistribucionEstados().get("PAGADA"));
        assertEquals(1L, result.getDistribucionEstados().get("CANCELADA"));
    }
}