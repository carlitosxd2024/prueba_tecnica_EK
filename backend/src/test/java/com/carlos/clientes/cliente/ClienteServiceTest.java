package com.carlos.clientes.cliente;

import com.carlos.clientes.common.CorreoDuplicadoException;
import com.carlos.clientes.common.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void creaClienteConCorreoNormalizado() {
        ClienteRequest request = new ClienteRequest("  Ana ", " Pérez ", " ANA@ejemplo.com ", " 555 123 ");
        when(repository.existsByCorreoIgnoreCase("ana@ejemplo.com")).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponse response = service.crear(request);

        assertEquals("Ana", response.nombres());
        assertEquals("ana@ejemplo.com", response.correo());
        assertEquals("555 123", response.telefono());
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void noCreaClienteSiElCorreoYaExiste() {
        ClienteRequest request = new ClienteRequest("Ana", "Pérez", "ana@ejemplo.com", "555123");
        when(repository.existsByCorreoIgnoreCase("ana@ejemplo.com")).thenReturn(true);

        assertThrows(CorreoDuplicadoException.class, () -> service.crear(request));

        verify(repository, never()).save(any(Cliente.class));
    }

    @Test
    void devuelveErrorSiNoEncuentraCliente() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(42L));
    }
}
