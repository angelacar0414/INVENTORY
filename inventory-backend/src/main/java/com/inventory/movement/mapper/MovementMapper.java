package com.inventory.movement.mapper;

import com.inventory.auth.UsuarioEntity;
import com.inventory.client.entity.ClientEntity;
import com.inventory.movement.dto.MovementDTO;
import com.inventory.movement.entity.MovementEntity;
import com.inventory.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper encargado de convertir un MovementEntity en un MovementDTO.
 *
 * A diferencia de otros módulos, aquí solo existe la conversión
 * Entity -> DTO. La conversión contraria no se necesita porque el
 * MovementService construye la entidad paso a paso: el tipo, los stocks,
 * la fecha y el usuario los calcula el backend y no se toman del
 * frontend.
 */
@Component
public class MovementMapper {

    /**
     * Convierte una entidad MovementEntity en un MovementDTO.
     *
     * Además de los identificadores, se incluyen los nombres del producto,
     * del usuario y del cliente para que el frontend pueda mostrar el
     * historial sin hacer consultas adicionales.
     *
     * @param entity movimiento registrado
     * @return DTO con la información del movimiento
     */
    public MovementDTO toDTO(MovementEntity entity) {

        MovementDTO dto = new MovementDTO();

        // ---------------------------------------------------------
        // INFORMACIÓN BÁSICA DEL MOVIMIENTO
        // ---------------------------------------------------------

        dto.setIdMovimiento(entity.getIdMovimiento());
        dto.setCantidad(entity.getCantidad());
        dto.setStockAnterior(entity.getStockAnterior());
        dto.setStockPosterior(entity.getStockPosterior());
        dto.setFechaMovimiento(entity.getFechaMovimiento());
        dto.setObservacion(entity.getObservacion());

        /*
         * El tipo se convierte de Enum a String para facilitar
         * su transporte mediante JSON.
         */
        if (entity.getTipoMovimiento() != null) {
            dto.setTipoMovimiento(entity.getTipoMovimiento().name());
        }

        // ---------------------------------------------------------
        // PRODUCTO
        // ---------------------------------------------------------

        ProductEntity producto = entity.getProducto();

        if (producto != null) {
            dto.setIdProducto(producto.getIdProducto());
            dto.setCodigoProducto(producto.getCodigo());
            dto.setNombreProducto(producto.getNombre());
        }

        // ---------------------------------------------------------
        // USUARIO
        // ---------------------------------------------------------

        UsuarioEntity usuario = entity.getUsuario();

        if (usuario != null) {
            dto.setIdUsuario(usuario.getIdUsuario());
            dto.setNombreUsuario(construirNombreUsuario(usuario));
        }

        // ---------------------------------------------------------
        // CLIENTE (solo existe en las salidas)
        // ---------------------------------------------------------

        ClientEntity cliente = entity.getCliente();

        if (cliente != null) {
            dto.setIdCliente(cliente.getIdCliente());
            dto.setNombreCliente(cliente.getNombre());
        }

        return dto;
    }

    /**
     * Construye el nombre que se muestra del usuario.
     *
     * Usa "nombre apellido" cuando existen. Si el usuario no tiene ninguno
     * de los dos, se usa su username para que la columna nunca quede vacía.
     */
    private String construirNombreUsuario(UsuarioEntity usuario) {

        String nombre = usuario.getNombre() != null
                ? usuario.getNombre().trim() : "";

        String apellido = usuario.getApellido() != null
                ? usuario.getApellido().trim() : "";

        String completo = (nombre + " " + apellido).trim();

        return completo.isEmpty() ? usuario.getUsername() : completo;
    }
}
