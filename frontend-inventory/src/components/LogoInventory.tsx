// ---------------------------------------------------------
// LOGO DE INVENTORY (caja con check)
// ---------------------------------------------------------
// Dibujé el icono del logo como SVG para que se vea nítido en
// cualquier tamaño y para poder cambiarle el color según el
// fondo: en el sidebar (fondo oscuro) usamos un azul más claro
// y el texto en blanco.
// ---------------------------------------------------------

interface ILogoInventoryProps {
  // Alto del icono en píxeles.
  tamano?: number;
  // Si es true, se muestra la palabra "Inventory" debajo del icono.
  conTexto?: boolean;
}

export function LogoInventory({ tamano = 64, conTexto = true }: ILogoInventoryProps) {
  return (
    <div className="logo-inventory">
      <svg
        width={tamano}
        height={tamano}
        viewBox="315 165 395 385"
        fill="none"
        stroke="currentColor"
        strokeWidth="22"
        strokeLinecap="round"
        strokeLinejoin="round"
        role="img"
        aria-label="Logo de Inventory"
        className="logo-inventory-icono"
      >
        {/* contorno de la caja */}
        <path d="M345 280 L493 190 L652 275 L652 330" />
        <path d="M345 280 L345 440 L487 522" />
        {/* borde frontal superior y arista central */}
        <path d="M345 280 L495 360 L645 280" />
        <path d="M487 365 V522" />
        {/* cinta de la caja */}
        <path d="M410 305 L555 222" />
        <path d="M410 305 V345" />
        {/* esquina inferior derecha y check */}
        <path d="M487 522 L545 490" />
        <path d="M552 417 L598 462 L690 372" />
      </svg>
      {conTexto && <span className="logo-inventory-texto">Inventory</span>}
    </div>
  );
}
