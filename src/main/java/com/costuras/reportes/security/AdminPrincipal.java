package com.costuras.reportes.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa al ADMIN autenticado extraído del JWT.
 * Queda en el SecurityContext para que los servicios lo consuman.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPrincipal {

    private Integer id;
    private String username;
    private String role;
}
