package com.inventory.client.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.client.entity.ClientEntity;

/**
 * Repository del módulo Clientes.
 * No validamos nada acá, solo hablamos con la base de datos. Al extender
 * de JpaRepository ya tenemos findAll(), save(), findById(), etc. listos,
 * y solo agregamos las consultas propias que necesitamos para el buscador
 * y para revisar duplicados.
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    // Para el listado normal de la pantalla (solo los activos)
    List<ClientEntity> findByActivoTrue();

    // Para el buscador de la pantalla de clientes
    List<ClientEntity> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    // La usamos en el Service para revisar si el documento ya existe
    Optional<ClientEntity> findByDocumento(String documento);

    boolean existsByDocumentoAndActivoTrue(String documento);
}
