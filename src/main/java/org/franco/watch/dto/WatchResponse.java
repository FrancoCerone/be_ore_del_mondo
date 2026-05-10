package org.franco.watch.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.franco.watch.entity.MovementType;
import org.franco.watch.entity.WatchCondition;

public record WatchResponse(
        Long id,
        UUID uuid,
        String name,
        String slug,
        String description,
        String shortDescription,
        String brand,
        String model,
        BigDecimal price,
        String currency,
        WatchCondition condition,
        Integer year,
        String referenceNumber,
        MovementType movement,
        String caseMaterial,
        String strapMaterial,
        BigDecimal diameter,
        String waterResistance,
        Integer stock,
        Boolean featured,
        Boolean published,
        String seoTitle,
        String seoDescription,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<WatchImageResponse> images) {
}
