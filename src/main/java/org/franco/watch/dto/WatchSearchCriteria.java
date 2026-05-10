package org.franco.watch.dto;

import java.math.BigDecimal;

public record WatchSearchCriteria(
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean featured,
        Boolean published,
        String search) {
}
