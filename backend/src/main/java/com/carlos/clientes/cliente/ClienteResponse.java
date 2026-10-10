package com.carlos.clientes.cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nombres,
        String apellidos,
        String correo,
        String telefono,
        LocalDateTime fechaCreacion
) {
    public static ClienteResponse desde(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.getFechaCreacion()
        );
    }
}
