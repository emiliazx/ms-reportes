package com.costuras.reportes.controller;

import com.costuras.reportes.dto.ReporteResponse;
import com.costuras.reportes.service.ReportesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación de reportes de ventas para administradores")
public class ReportesController {

    private final ReportesService reportesService;

    @Operation(summary = "Generar reporte de ventas",
               description = "Genera un reporte detallado con ingresos totales, ventas por día, productos más vendidos y distribución por estado, dentro de un rango de fechas. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Fechas inválidas o faltantes"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<ReporteResponse> getReporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestHeader("Authorization") String bearerToken) {
        return ResponseEntity.ok(reportesService.generarReporte(bearerToken, desde, hasta));
    }
}