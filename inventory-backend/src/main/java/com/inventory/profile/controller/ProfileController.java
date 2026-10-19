package com.inventory.profile.controller;

import com.inventory.profile.dto.PasswordChangeDTO;
import com.inventory.profile.dto.ProfileUpdateDTO;
import com.inventory.profile.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CONTROLLER DEL MÓDULO PERFIL
 * ----------------------------
 * Base: /api/v1/perfil
 *
 *   GET  /api/v1/perfil              consultar mi perfil        (RF-032)
 *   PUT  /api/v1/perfil              actualizar mis datos       (RF-033)
 *   PUT  /api/v1/perfil/contrasena   cambiar mi contraseña      (RF-034)
 *
 * Acceso: cualquier usuario autenticado (Administrador u Operador).
 * SecurityConfig ya lo exige con anyRequest().authenticated().
 * El usuario se obtiene de la sesión, nunca de la URL.
 */
@RestController
@RequestMapping("/api/v1/perfil")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> consultar(Authentication authentication) {
        return ResponseEntity.ok(success("Perfil consultado correctamente",
                profileService.consultar(authentication.getName())));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizar(
            @RequestBody ProfileUpdateDTO datos,
            Authentication authentication) {

        return ResponseEntity.ok(success("Perfil actualizado correctamente",
                profileService.actualizar(authentication.getName(), datos)));
    }

    @PutMapping("/contrasena")
    public ResponseEntity<Map<String, Object>> cambiarContrasena(
            @RequestBody PasswordChangeDTO datos,
            Authentication authentication) {

        profileService.cambiarContrasena(authentication.getName(), datos);
        return ResponseEntity.ok(success("Contraseña actualizada correctamente", null));
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", message);
        body.put("data", data);
        return body;
    }
}
