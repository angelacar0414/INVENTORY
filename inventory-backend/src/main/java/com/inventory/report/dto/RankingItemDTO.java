package com.inventory.report.dto;

/**
 * Un elemento de un ranking (por ejemplo, los productos con más
 * unidades movidas).
 */
public record RankingItemDTO(
        String nombre,
        long valor
) {
}
