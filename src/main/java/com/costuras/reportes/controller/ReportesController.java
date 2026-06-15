package com.costuras.reportes.controller;

import com.costuras.reportes.dto.ReporteResponse;
import com.costuras.reportes.service.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReportesService reportesService;

    
    @GetMapping
    public ResponseEntity<ReporteResponse> getReporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestHeader("Authorization") String bearerToken
    ) {
        ReporteResponse reporte = reportesService.generarReporte(bearerToken, desde, hasta);
        return ResponseEntity.ok(reporte);
    }
}
