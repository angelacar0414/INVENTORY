package com.inventory.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inventory.auth.UsuarioEntity;
import com.inventory.auth.UsuarioRepository;
import com.inventory.user.dto.UserCreateDTO;
import com.inventory.user.dto.UserDTO;
import com.inventory.user.mapper.UserMapper;

/**
 * Servicio encargado de gestionar las operaciones
 * del módulo de usuarios.
 *
 * Reutiliza la entidad y el repositorio existentes
 * del módulo de autenticación.
 *
 * @author Dario Bustamante
 * @version 1.0
 */
@Service
public class UserService {

    // Repositorio que permite acceder a los usuarios en la base de datos.
    private final UsuarioRepository usuarioRepository;

    // Mapper utilizado para convertir entidades en DTOs.
    private final UserMapper userMapper;

    // Codificador utilizado para almacenar las contraseñas de forma segura.
    private final PasswordEncoder passwordEncoder;


    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor del servicio.
     *
     * Spring inyecta automáticamente las dependencias necesarias.
     */
    public UserService(
            UsuarioRepository usuarioRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    // ==================== CREAR USUARIO ====================

    /**
     * Registra un nuevo usuario desde el módulo de usuarios.
     *
     * Verifica que el username y el correo electrónico
     * no estén registrados antes de guardar el usuario.
     *
     * La contraseña se almacena utilizando BCrypt.
     *
     * @param dto información del nuevo usuario
     * @return usuario creado en formato DTO
     */
    public UserDTO crearUsuario(UserCreateDTO dto) {

        // Verifica que el username no esté registrado.
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException(
                    "El username ya está registrado"
            );
        }

        // Verifica que el correo electrónico no esté registrado.
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "El correo electrónico ya está registrado"
            );
        }

        // Crea una nueva entidad de usuario.
        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());

        // La contraseña nunca se guarda directamente.
        usuario.setContraseñaEncriptada(
                passwordEncoder.encode(dto.getContraseña())
        );

        // Si no se especifica un rol, se asigna OPERADOR por defecto.
        if (dto.getRol() == null || dto.getRol().isBlank()) {
            usuario.setRol("OPERADOR");
        } else {
            usuario.setRol(dto.getRol());
        }

        // El usuario se crea activo.
        usuario.setActivo(true);

        // Guarda el usuario en la base de datos.
        UsuarioEntity usuarioGuardado =
                usuarioRepository.save(usuario);

        // Convierte la entidad guardada en DTO para la respuesta.
        return userMapper.toDTO(usuarioGuardado);
    }


    // ==================== CONSULTAR USUARIOS ====================

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de usuarios en formato DTO
     */
    public List<UserDTO> listarUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }


    // ==================== BUSCAR POR ID ====================

    /**
     * Busca un usuario utilizando su identificador.
     *
     * @param id identificador del usuario
     * @return usuario encontrado en formato DTO
     * @throws RuntimeException si el usuario no existe
     */
    public UserDTO buscarPorId(Long id) {

        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        return userMapper.toDTO(usuario);
    }


    // ==================== ACTUALIZAR USUARIO ====================

    /**
     * Actualiza los datos básicos de un usuario.
     *
     * La contraseña y el estado activo no se modifican
     * mediante este método.
     *
     * @param id identificador del usuario
     * @param dto nuevos datos del usuario
     * @return usuario actualizado
     */
    public UserDTO actualizarUsuario(Long id, UserDTO dto) {

        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        // Actualiza los datos permitidos mediante el mapper.
        userMapper.updateEntity(dto, usuario);

        // Guarda los cambios en la base de datos.
        UsuarioEntity usuarioActualizado =
                usuarioRepository.save(usuario);

        // Devuelve el usuario actualizado sin contraseña.
        return userMapper.toDTO(usuarioActualizado);
    }


    // ==================== INACTIVAR USUARIO ====================

    /**
     * Inactiva un usuario sin eliminarlo físicamente
     * de la base de datos.
     *
     * @param id identificador del usuario
     * @return usuario actualizado
     */
    public UserDTO inactivarUsuario(Long id) {

        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        // Cambia el estado del usuario a inactivo.
        usuario.setActivo(false);

        // Guarda el nuevo estado.
        UsuarioEntity usuarioActualizado =
                usuarioRepository.save(usuario);

        return userMapper.toDTO(usuarioActualizado);
    }


    // ==================== REACTIVAR USUARIO ====================

    /**
     * Reactiva un usuario que se encontraba inactivo.
     *
     * @param id identificador del usuario
     * @return usuario actualizado
     */
    public UserDTO reactivarUsuario(Long id) {

        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        // Cambia el estado del usuario a activo.
        usuario.setActivo(true);

        // Guarda el nuevo estado.
        UsuarioEntity usuarioActualizado =
                usuarioRepository.save(usuario);

        return userMapper.toDTO(usuarioActualizado);
    }
}