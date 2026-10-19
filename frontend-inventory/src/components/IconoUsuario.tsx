// Icono de usuario (cabeza y hombros, solo contorno) para el login.
// Reemplaza al emoji que teníamos antes.
export function IconoUsuario({ tamano = 120 }: { tamano?: number }) {
  return (
    <svg
      width={tamano}
      height={tamano}
      viewBox="0 0 120 120"
      fill="none"
      stroke="currentColor"
      strokeWidth="8"
      strokeLinecap="round"
      strokeLinejoin="round"
      role="img"
      aria-label="Usuario"
    >
      <circle cx="60" cy="36" r="24" />
      <path d="M16 104 V98 C16 78 34 64 60 64 C86 64 104 78 104 98 V104 C104 106 102 108 100 108 H20 C18 108 16 106 16 104 Z" />
    </svg>
  );
}
