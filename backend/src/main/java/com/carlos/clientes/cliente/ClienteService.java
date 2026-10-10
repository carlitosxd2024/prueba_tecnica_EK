package com.carlos.clientes.cliente;

import com.carlos.clientes.common.CorreoDuplicadoException;
import com.carlos.clientes.common.RecursoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(ClienteResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return ClienteResponse.desde(buscarPorId(id));
    }

    public ClienteResponse crear(ClienteRequest request) {
        validarCorreoDisponible(request.correo(), null);
        Cliente cliente = new Cliente(
                request.nombres().trim(),
                request.apellidos().trim(),
                normalizarCorreo(request.correo()),
                request.telefono().trim()
        );
        Cliente guardado = repository.save(cliente);
        log.info("Cliente creado con id {}", guardado.getId());
        return ClienteResponse.desde(guardado);
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarPorId(id);
        validarCorreoDisponible(request.correo(), id);
        cliente.actualizar(
                request.nombres().trim(),
                request.apellidos().trim(),
                normalizarCorreo(request.correo()),
                request.telefono().trim()
        );
        Cliente guardado = repository.save(cliente);
        log.info("Cliente actualizado con id {}", guardado.getId());
        return ClienteResponse.desde(guardado);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
        log.info("Cliente eliminado con id {}", id);
    }

    private Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente con id " + id));
    }

    private void validarCorreoDisponible(String correo, Long id) {
        String correoNormalizado = normalizarCorreo(correo);
        boolean existe = id == null
                ? repository.existsByCorreoIgnoreCase(correoNormalizado)
                : repository.existsByCorreoIgnoreCaseAndIdNot(correoNormalizado, id);
        if (existe) {
            throw new CorreoDuplicadoException("Ya existe un cliente con ese correo");
        }
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
