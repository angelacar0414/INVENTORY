import { useEffect, useRef, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";

import { perfilService } from "../profile/services/perfilService";
import { Icono } from "./Iconos";
import {
  EVENTO_PERFIL,
  guardarResumenPerfil,
  leerResumenPerfil,
  obtenerIniciales,
} from "./resumenPerfil";
import type { IResumenPerfil } from "./resumenPerfil";
import { useCerrarSesion } from "./useCerrarSesion";

// ---------------------------------------------------------
// BARRA SUPERIOR
// ---------------------------------------------------------
// Aparece en todas las pantallas internas (el Sidebar la monta).
// Tiene tres partes:
//   1. Saludo al usuario.
//   2. Buscador de productos: al enviar, lleva a /productos con
//      el texto buscado (?buscar=...).
//   3. Menú del usuario: avatar con iniciales, nombre y rol. Al
//      hacer clic se abre un menú con "Mi perfil" y "Cerrar sesión".
// ---------------------------------------------------------

export function Topbar() {
  const navigate = useNavigate();
  const cerrarSesion = useCerrarSesion();
  const [parametros] = useSearchParams();

  const username = sessionStorage.getItem("usuario") || "";
  const rol = sessionStorage.getItem("rol") || "";

  // Datos del perfil: primero intentamos con lo guardado y, si no hay,
  // los pedimos al Backend una sola vez.
  const [resumen, setResumen] = useState<IResumenPerfil | null>(() =>
    leerResumenPerfil(username)
  );

  // Si estamos en /productos?buscar=algo, mostramos "algo" en el buscador.
  const [busqueda, setBusqueda] = useState(parametros.get("buscar") || "");

  const [menuAbierto, setMenuAbierto] = useState(false);
  const contenedorMenu = useRef<HTMLDivElement>(null);

  // 1. Cargar el nombre completo si todavía no lo tenemos.
  useEffect(() => {
    if (resumen || !username) return;
    let activo = true;
    perfilService
      .consultar()
      .then((p) => {
        if (!activo) return;
        guardarResumenPerfil({
          username: p.username,
          nombre: p.nombre,
          apellido: p.apellido,
          email: p.email,
        });
      })
      .catch(() => {
        // Si falla, la barra funciona igual mostrando el username.
      });
    return () => {
      activo = false;
    };
  }, [resumen, username]);

  // 2. Escuchar los cambios del perfil (cuando se edita en "Mi perfil").
  useEffect(() => {
    const alActualizar = (e: Event) => {
      setResumen((e as CustomEvent<IResumenPerfil>).detail);
    };
    window.addEventListener(EVENTO_PERFIL, alActualizar);
    return () => window.removeEventListener(EVENTO_PERFIL, alActualizar);
  }, []);

  // 3. Cerrar el menú al hacer clic afuera o al presionar Escape.
  useEffect(() => {
    if (!menuAbierto) return;
    const alHacerClic = (e: MouseEvent) => {
      if (!contenedorMenu.current?.contains(e.target as Node)) {
        setMenuAbierto(false);
      }
    };
    const alPresionar = (e: KeyboardEvent) => {
      if (e.key === "Escape") setMenuAbierto(false);
    };
    document.addEventListener("mousedown", alHacerClic);
    document.addEventListener("keydown", alPresionar);
    return () => {
      document.removeEventListener("mousedown", alHacerClic);
      document.removeEventListener("keydown", alPresionar);
    };
  }, [menuAbierto]);

  function buscar(e: FormEvent) {
    e.preventDefault();
    const texto = busqueda.trim();
    navigate(texto ? `/productos?buscar=${encodeURIComponent(texto)}` : "/productos");
  }

  const nombreCompleto = resumen
    ? `${resumen.nombre} ${resumen.apellido}`.trim() || username
    : username;
  const primerNombre = resumen?.nombre.trim().split(" ")[0] || username;
  // "ADMINISTRADOR" -> "Administrador"
  const rolLegible = rol ? rol.charAt(0) + rol.slice(1).toLowerCase() : "";

  return (
    <header className="topbar">
      <div className="topbar-saludo">
        <strong>Hola, {primerNombre}</strong>
        <small>Tu sistema de inventario</small>
      </div>

      <form className="topbar-busqueda" role="search" onSubmit={buscar}>
        <input
          type="text"
          aria-label="Buscar productos"
          placeholder="Buscar productos por código o nombre..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
        />
        <button type="submit" aria-label="Buscar">
          <Icono nombre="buscar" tamano={18} />
        </button>
      </form>

      <div className="topbar-perfil" ref={contenedorMenu}>
        <button
          type="button"
          className="topbar-perfil-boton"
          aria-haspopup="menu"
          aria-expanded={menuAbierto}
          onClick={() => setMenuAbierto(!menuAbierto)}
        >
          <span className="topbar-perfil-texto">
            <strong>{nombreCompleto}</strong>
            <small>{rolLegible}</small>
          </span>
          <span className="topbar-avatar" aria-hidden="true">
            {obtenerIniciales(resumen, username)}
          </span>
          <span className="topbar-flecha" aria-hidden="true">
            <Icono nombre="abajo" tamano={16} />
          </span>
        </button>

        {menuAbierto && (
          <div className="topbar-menu" role="menu">
            <div className="topbar-menu-cabecera">
              <strong>{nombreCompleto}</strong>
              <span>{resumen?.email || username}</span>
            </div>
            <Link
              to="/perfil"
              role="menuitem"
              className="topbar-menu-item"
              onClick={() => setMenuAbierto(false)}
            >
              <Icono nombre="usuario" tamano={18} />
              Mi perfil
            </Link>
            <button
              type="button"
              role="menuitem"
              className="topbar-menu-item"
              onClick={cerrarSesion}
            >
              <Icono nombre="salir" tamano={18} />
              Cerrar sesión
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
