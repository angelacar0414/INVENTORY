package com.inventory.client.service;

import com.inventory.client.dto.ClientDTO;
import com.inventory.client.entity.ClientEntity;
import com.inventory.client.mapper.ClientMapper;
import com.inventory.client.repository.ClientRepository;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Acá vive la lógica de negocio del módulo Clientes. Antes de guardar
 * revisamos que el documento no esté repetido en otro cliente activo,
 * y antes de desactivar revisamos que no esté ya desactivado. El
 * Controller nunca decide esto directamente, siempre nos lo delega a
 * nosotros.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    // Consulto todos los clientes registrados, incluidos los inactivos
    public List<ClientDTO> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Consulto solo los clientes activos, es lo que usa la lista principal
    public List<ClientDTO> findAllActive() {
        return clientRepository.findByActivoTrue()
                .stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO findById(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con id " + id));
        return clientMapper.toDTO(entity);
    }

    // Busco clientes por nombre (búsqueda parcial), para el campo
    // "Buscar cliente" del Frontend.
    public List<ClientDTO> buscarPorNombre(String nombre) {
        return clientRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Registro un nuevo cliente
    public ClientDTO create(ClientDTO dto) {
        validarDocumentoDuplicado(dto.getDocumento(), null);

        ClientEntity entity = clientMapper.toEntity(dto);
        entity.setIdCliente(null); // por si llega un id desde el formulario, lo ignoramos
        entity.setActivo(true);

        ClientEntity guardado = clientRepository.save(entity);
        return clientMapper.toDTO(guardado);
    }

    // Edito un cliente existente
    public ClientDTO update(Long id, ClientDTO dto) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con id " + id));

        validarDocumentoDuplicado(dto.getDocumento(), id);

        clientMapper.updateEntityFromDTO(dto, entity);
        ClientEntity actualizado = clientRepository.save(entity);
        return clientMapper.toDTO(actualizado);
    }

    // Desactivo el cliente (eliminación lógica: pongo activo = false)
    public void deactivate(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con id " + id));

        if (Boolean.FALSE.equals(entity.getActivo())) {
            throw new DuplicateResourceException("El cliente ya se encuentra desactivado");
        }

        entity.setActivo(false);
        clientRepository.save(entity);
    }

    // Vuelvo a activar el cliente, por si se desactivó por error
    public void reactivate(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con id " + id));

        entity.setActivo(true);
        clientRepository.save(entity);
    }

    // Reviso que el documento no esté repetido en otro cliente activo.
    // Si el documento viene vacío no hay nada que validar, porque en el
    // modelo de datos solo el nombre es obligatorio.
    private void validarDocumentoDuplicado(String documento, Long idActual) {
        if (documento == null || documento.isBlank()) {
            return;
        }

        clientRepository.findByDocumento(documento).ifPresent(existente -> {
            boolean esOtroCliente = idActual == null || !existente.getIdCliente().equals(idActual);
            if (esOtroCliente && Boolean.TRUE.equals(existente.getActivo())) {
                throw new DuplicateResourceException("Ya existe un cliente registrado con ese documento");
            }
        });
    }
}
