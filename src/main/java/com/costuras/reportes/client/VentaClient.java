package com.costuras.reportes.client;

import com.costuras.reportes.dto.VentaInternaResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class VentaClient {

    @Value("${venta.url}")
    private String ventaUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
   
     * @param bearerToken  "Bearer eyJhbG..."  (se reenvía tal cual)
     * @param desde        fecha inicio del rango
     * @param hasta        fecha fin del rango
     */
    public List<VentaInternaResponse> getVentasEnRango(
            String bearerToken,
            LocalDate desde,
            LocalDate hasta
    ) {
        try {
            String url = ventaUrl + "/ventas/internas?desde=" + desde + "&hasta=" + hasta;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", bearerToken)   // reenvía el token del ADMIN
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<VentaInternaResponse>>() {}
                );
            }

            log.warn("MS-Venta respondió con status {} al consultar ventas internas",
                    response.statusCode());
            return List.of();

        } catch (Exception e) {
            log.error("Error al consultar MS-Venta /internas: {}", e.getMessage());
            throw new RuntimeException("No se pudo obtener datos de ventas: " + e.getMessage(), e);
        }
    }
}
