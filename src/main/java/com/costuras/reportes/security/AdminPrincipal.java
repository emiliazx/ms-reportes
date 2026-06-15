package com.costuras.reportes.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPrincipal {

    private Integer id;
    private String username;
    private String role;
}
