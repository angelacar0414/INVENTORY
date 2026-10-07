package com.inventory.movement.service;

import com.inventory.auth.UsuarioEntity;
import com.inventory.auth.UsuarioRepository;
import com.inventory.client.entity.ClientEntity;
import com.inventory.client.repository.ClientRepository;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.movement.dto.MovementDTO;
import com.inventory.movement.entity.MovementEntity;
import com.inventory.movement.entity.MovementType;
import com.inventory.movement.exception.InvalidMovementException;
import com.inventory.movement.mapper.MovementMapper;
import com.inventory.movement.repository.MovementRepository;
import com.inventory.movement.validator.MovementValidator;
import com.inventory.product.entity.ProductEntity;
import com.inventory.product.entity.ProductStatus;
import com.inventory.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase Service del módulo Movimientos.
 *
 * Aquí se concentra la lógica de negocio de las entradas y salidas del
 * inventario. Es el ÚNICO lugar del sistema que modifica el stock de los
 * productos (el módulo Productos no lo permite al actualizar).
 *
 * Reglas principales:
 *
 * 1. Una ENTRADA aumenta el stock del producto.
 * 2. Una SALIDA disminuye el stock y exige un cliente.
 * 3. Una SALIDA no puede superar el stock disponible.
 * 4. Cada movimiento guarda el stock anterior y posterior.
 * 5. Después de cada movimiento se recalcula el estado del producto.
 * 6. Los movimientos nunca se modifican ni se eliminan.
 *
 * El Service se comunica con:
 *
 * - MovementRepository
 * - ProductRepository
 * - ClientRepository
 * - UsuarioRepository
 * - MovementMapper
 * - MovementValidator
 */
@Service
public class MovementService {

    /**
     * Repositorio encargado de guardar y consultar movimientos.
     */
    private final MovementRepository movementRepository;

    /**
     * Repositorio utilizado para guardar el nuevo stock del producto.
     */
    private final ProductRepository productRepository;

    /**
     * Repositorio utilizado para verificar el cliente de una salida.
     */
    private final ClientRepository clientRepository;

    /**
     * Repositorio utilizado para identificar al usuario que registra
     * el movimiento.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Mapper encargado de convertir MovementEntity en MovementDTO.
     */
    private final MovementMapper movementMapper;

    /**
     * Validator con las reglas de negocio propias de los movimientos.
     */
    private final MovementValidator movementValidator;

    /**
     * Constructor utilizado por Spring para inyectar las dependencias
     * necesarias para el funcionamiento del Service.
     */
    public MovementService(
            MovementRepository movementRepository,
            ProductRepository productRepository,
            ClientRepository clientRepository,
            UsuarioRepository usuarioRepository,
            MovementMapper movementMapper,
            MovementValidator movementValidator) {

        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.usuarioRepository = usuarioRepository;
        this.movementMapper = movementMapper;
        this.movementValidator = movementValidator;
    }

    // =========================================================
    // CONSULTAR HISTORIAL
    // =========================================================

    /**
     * Consulta el historial de movimientos, del más reciente al más
     * antiguo.
     *
     * Ambos filtros son opcionales y se pueden combinar:
     *
     * - criterio: texto que debe coincidir con el nombre o el código
     *   del producto.
     * - tipo: ENTRADA o SALIDA.
     *
     * @param criterio texto del buscador (puede ser null o vacío)
     * @param tipo tipo de movimiento (puede ser null o vacío)
     * @return lista de movimientos que cumplen los filtros
     */
    @Transactional(readOnly = true)
    public List<MovementDTO> findAll(String criterio, String tipo) {

        MovementType tipoFiltro = convertirTipo(tipo);

        boolean hayCriterio = criterio != null && !criterio.isBlank();

        List<MovementEntity> movimientos;

        if (hayCriterio && tipoFiltro != null) {

            movimientos = movementRepository
                    .buscarPorProductoYTipo(criterio.trim(), tipoFiltro);

        } else if (hayCriterio) {

            movimientos = movementRepository
                    .buscarPorProducto(criterio.trim());

        } else if (tipoFiltro != null) {

            movimientos = movementRepository
                    .findByTipoMovimientoOrderByFechaMovimientoDescIdMovimientoDesc(
                            tipoFiltro
                    );

        } else {

            movimientos = movementRepository
                    .findAllByOrderByFechaMovimientoDescIdMovimientoDesc();
        }

        return movimientos
                .stream()
                .map(movementMapper::toDTO)
                .toList();
    }

    // =========================================================
    // CONSULTAR MOVIMIENTO POR ID
    // =========================================================

    /**
     * Busca un movimiento específico mediante su identificador.
     *
     * @param id identificador del movimiento
     * @return información del movimiento encontrado
     */
    @Transactional(readOnly = true)
    public MovementDTO findById(Long id) {

        MovementEntity movimiento = movementRepository
                .findByIdMovimiento(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el movimiento con id " + id
                        )
                );

        return movementMapper.toDTO(movimiento);
    }

    // =========================================================
    // REGISTRAR ENTRADA
    // =========================================================

    /**
     * Registra una ENTRADA de inventario.
     *
     * El stock del producto aumenta en la cantidad indicada. Si el
     * frontend envía un cliente, se ignora, porque las entradas no
     * requieren cliente.
     *
     * @param dto datos enviados por el frontend
     * @param username usuario autenticado que registra el movimiento
     * @return movimiento registrado
     */
    @Transactional
    public MovementDTO registrarEntrada(MovementDTO dto, String username) {

        return registrar(MovementType.ENTRADA, dto, username);
    }

    // =========================================================
    // REGISTRAR SALIDA
    // =========================================================

    /**
     * Registra una SALIDA de inventario.
     *
     * El stock del producto disminuye en la cantidad indicada. El cliente
     * es obligatorio y debe existir y estar activo. Si la cantidad supera
     * el stock disponible, no se registra nada.
     *
     * @param dto datos enviados por el frontend
     * @param username usuario autenticado que registra el movimiento
     * @return movimiento registrado
     */
    @Transactional
    public MovementDTO registrarSalida(MovementDTO dto, String username) {

        return registrar(MovementType.SALIDA, dto, username);
    }

    // =========================================================
    // REGISTRAR MOVIMIENTO (lógica común)
    // =========================================================

    /**
     * Contiene el flujo completo de un movimiento. Las entradas y las
     * salidas comparten los mismos pasos y solo cambian en tres puntos:
     * si se exige cliente, cómo se valida el stock y si el stock sube
     * o baja.
     *
     * Todo ocurre dentro de una única transacción: si cualquier paso
     * falla, no se guarda el movimiento ni se modifica el stock
     * ("no se modificarán los datos cuando la operación falle").
     */
    private MovementDTO registrar(
            MovementType tipo,
            MovementDTO dto,
            String username) {

        boolean esSalida = tipo == MovementType.SALIDA;

        // ---------------------------------------------------------
        // VALIDAR DATOS BÁSICOS
        // ---------------------------------------------------------

        movementValidator.validarCantidad(dto.getCantidad());

        int cantidad = dto.getCantidad();

        // ---------------------------------------------------------
        // IDENTIFICAR USUARIO Y CLIENTE
        // ---------------------------------------------------------

        UsuarioEntity usuario = buscarUsuarioActivo(username);

        ClientEntity cliente = null;

        if (esSalida) {
            movementValidator.validarClienteObligatorio(dto.getIdCliente());
            cliente = buscarClienteActivo(dto.getIdCliente());
        }

        // ---------------------------------------------------------
        // CONSULTAR Y BLOQUEAR EL PRODUCTO
        // ---------------------------------------------------------

        ProductEntity producto =
                buscarProductoActivoConBloqueo(dto.getIdProducto());

        // ---------------------------------------------------------
        // CALCULAR EL NUEVO STOCK
        // ---------------------------------------------------------

        int stockAnterior = producto.getStockActual();
        int stockPosterior;

        if (esSalida) {
            movementValidator.validarStockSuficiente(stockAnterior, cantidad);
            stockPosterior = stockAnterior - cantidad;
        } else {
            movementValidator.validarCapacidad(stockAnterior, cantidad);
            stockPosterior = stockAnterior + cantidad;
        }

        // ---------------------------------------------------------
        // ACTUALIZAR EL PRODUCTO
        // ---------------------------------------------------------

        producto.setStockActual(stockPosterior);
        actualizarEstado(producto);
        productRepository.save(producto);

        // ---------------------------------------------------------
        // REGISTRAR EL MOVIMIENTO EN EL HISTORIAL
        // ---------------------------------------------------------

        MovementEntity movimiento = new MovementEntity();

        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setObservacion(limpiarObservacion(dto.getObservacion()));
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setCliente(cliente);

        MovementEntity guardado = movementRepository.save(movimiento);

        return movementMapper.toDTO(guardado);
    }

    // =========================================================
    // CALCULAR ESTADO DEL PRODUCTO
    // =========================================================

    /**
     * Recalcula el estado del producto después de cambiar su stock.
     *
     * Reglas definidas en el EKB (ADR-021):
     *
     * - Producto inactivo → INACTIVO
     * - Stock igual a cero → AGOTADO
     * - Stock mayor a cero → DISPONIBLE
     *
     * Es la misma regla que aplica ProductService. Se repite aquí
     * porque en ProductService este método es privado.
     *
     * @param producto producto cuyo estado se actualiza
     */
    private void actualizarEstado(ProductEntity producto) {

        if (!Boolean.TRUE.equals(producto.getActivo())) {

            producto.setEstado(ProductStatus.INACTIVO);

        } else if (producto.getStockActual() == null
                || producto.getStockActual() <= 0) {

            producto.setEstado(ProductStatus.AGOTADO);

        } else {

            producto.setEstado(ProductStatus.DISPONIBLE);
        }
    }

    // =========================================================
    // MÉTODOS AUXILIARES DE BÚSQUEDA
    // =========================================================

    /**
     * Busca el producto, lo bloquea para que nadie más modifique su stock
     * mientras dure la transacción y verifica que esté activo.
     *
     * @param id identificador del producto
     * @return producto activo y bloqueado
     */
    private ProductEntity buscarProductoActivoConBloqueo(Long id) {

        if (id == null) {
            throw new ResourceNotFoundException(
                    "El producto es obligatorio."
            );
        }

        ProductEntity producto = movementRepository
                .buscarProductoConBloqueo(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el producto con id " + id
                        )
                );

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new ResourceNotFoundException(
                    "El producto seleccionado se encuentra inactivo."
            );
        }

        return producto;
    }

    /**
     * Busca un cliente por su identificador y verifica que esté activo.
     *
     * @param id identificador del cliente
     * @return cliente activo
     */
    private ClientEntity buscarClienteActivo(Long id) {

        ClientEntity cliente = clientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el cliente con id " + id
                        )
                );

        if (!Boolean.TRUE.equals(cliente.getActivo())) {
            throw new ResourceNotFoundException(
                    "El cliente seleccionado se encuentra inactivo."
            );
        }

        return cliente;
    }

    /**
     * Busca al usuario autenticado por su username y verifica que
     * esté activo.
     *
     * @param username username del usuario con sesión iniciada
     * @return usuario activo
     */
    private UsuarioEntity buscarUsuarioActivo(String username) {

        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el usuario autenticado."
                        )
                );

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResourceNotFoundException(
                    "El usuario autenticado se encuentra inactivo."
            );
        }

        return usuario;
    }

    // =========================================================
    // MÉTODOS AUXILIARES DE CONVERSIÓN
    // =========================================================

    /**
     * Convierte el texto recibido en el filtro "tipo" a MovementType.
     *
     * @param tipo texto recibido (ENTRADA o SALIDA, sin importar
     *             mayúsculas); puede ser null o vacío
     * @return el tipo, o null si no se indicó ninguno
     */
    private MovementType convertirTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            return null;
        }

        try {
            return MovementType.valueOf(tipo.trim().toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new InvalidMovementException(
                    "El tipo de movimiento debe ser ENTRADA o SALIDA."
            );
        }
    }

    /**
     * Quita los espacios sobrantes de la observación y guarda null
     * cuando viene vacía.
     */
    private String limpiarObservacion(String observacion) {

        if (observacion == null || observacion.isBlank()) {
            return null;
        }

        return observacion.trim();
    }
}
