package org.franco.watch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import org.franco.watch.entity.MovementType;
import org.franco.watch.entity.WatchCondition;

public record WatchRequest(
        @NotBlank @Size(max = 180) String name,
        @Size(max = 10_000) String description,
        @Size(max = 500) String shortDescription,
        @NotBlank @Size(max = 120) String brand,
        @NotBlank @Size(max = 120) String model,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull WatchCondition condition,
        @Min(1800) @Max(2100) Integer year,
        @Size(max = 120) String referenceNumber,
        MovementType movement,
        @Size(max = 120) String caseMaterial,
        @Size(max = 120) String strapMaterial,
        @DecimalMin(value = "0.01") BigDecimal diameter,
        @Size(max = 80) String waterResistance,
        @NotNull @Min(0) Integer stock,
        Boolean featured,
        Boolean published,
        @Size(max = 180) String seoTitle,
        @Size(max = 320) String seoDescription,
        List<@Valid WatchImageRequest> images) {
}
