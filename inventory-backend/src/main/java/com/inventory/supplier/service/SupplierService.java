package com.inventory.supplier.service;

import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.supplier.dto.SupplierDTO;
import com.inventory.supplier.entity.SupplierEntity;
import com.inventory.supplier.mapper.SupplierMapper;
import com.inventory.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aquí vive toda la lógica de negocio del módulo de Proveedores.
 *
 * Antes de guardar o modificar un proveedor, reviso las reglas
 * necesarias (por ejemplo, que no esté duplicado), y me apoyo en
 * SupplierMapper para traducir la información entre Entity y DTO.
 *
 * Reutilizo las mismas excepciones compartidas (DuplicateResourceException
 * y ResourceNotFoundException) que ya usamos en el módulo de Categorías,
 * para que todos los módulos respondan los errores de la misma manera.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    /**
     * Registro un nuevo proveedor en el sistema.
     * Antes de guardar, reviso que no exista ya un proveedor con el
     * mismo nombre o el mismo correo electrónico.
     */
    public SupplierDTO create(SupplierDTO dto) {
        if (supplierRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe un proveedor registrado con ese nombre.");
        }
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()
                && supplierRepository.existsByCorreoIgnoreCase(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un proveedor registrado con ese correo.");
        }

        SupplierEntity nuevoProveedor = supplierMapper.toEntity(dto);
        nuevoProveedor.setActivo(true); // Todo proveedor nuevo queda activo por defecto.

        SupplierEntity guardado = supplierRepository.save(nuevoProveedor);
        return supplierMapper.toDTO(guardado);
    }

    /**
     * Consulto todos los proveedores registrados, sin importar su estado.
     */
    public List<SupplierDTO> findAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consulto únicamente los proveedores que están activos.
     */
    public List<SupplierDTO> findAllActive() {
        return supplierRepository.findByActivoTrue()
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busco un proveedor específico por su id.
     * Si no existe, lanzo una excepción para que el Frontend reciba
     * un mensaje claro en lugar de un error genérico.
     */
    public SupplierDTO findById(Long id) {
        SupplierEntity proveedor = buscarProveedorOLanzarError(id);
        return supplierMapper.toDTO(proveedor);
    }

    /**
     * Busco proveedores cuyo nombre coincida (parcialmente) con el
     * texto de búsqueda que recibo.
     */
    public List<SupplierDTO> buscarPorNombre(String nombre) {
        return supplierRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizo la información de un proveedor existente.
     * Si el nuevo nombre ya lo tiene otro proveedor distinto, rechazo
     * el cambio para no generar duplicados.
     */
    public SupplierDTO update(Long id, SupplierDTO dto) {
        SupplierEntity proveedorExistente = buscarProveedorOLanzarError(id);

        boolean nombreCambio = !proveedorExistente.getNombre().equalsIgnoreCase(dto.getNombre());
        if (nombreCambio && supplierRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe otro proveedor registrado con ese nombre.");
        }

        supplierMapper.actualizarEntity(proveedorExistente, dto);
        SupplierEntity actualizado = supplierRepository.save(proveedorExistente);
        return supplierMapper.toDTO(actualizado);
    }

    /**
     * Desactivo un proveedor (eliminación lógica). El registro no se
     * borra de la base de datos, solo cambia su estado a inactivo, tal
     * como lo define el proyecto para conservar el historial.
     *
     * Nota para más adelante: cuando ya tengamos el módulo de Productos
     * desarrollado, aquí se debería validar que el proveedor no tenga
     * productos activos asociados antes de desactivarlo, tal como lo
     * describe el RF-24 del proyecto.
     */
    public void deactivate(Long id) {
        SupplierEntity proveedor = buscarProveedorOLanzarError(id);
        proveedor.setActivo(false);
        supplierRepository.save(proveedor);
    }

    /**
     * Reactivo un proveedor que había sido desactivado anteriormente.
     */
    public void reactivate(Long id) {
        SupplierEntity proveedor = buscarProveedorOLanzarError(id);
        proveedor.setActivo(true);
        supplierRepository.save(proveedor);
    }

    // Método privado que reutilizo en varios puntos del Service para no
    // repetir la misma búsqueda y el mismo mensaje de error.
    private SupplierEntity buscarProveedorOLanzarError(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un proveedor con el id " + id));
    }
}
