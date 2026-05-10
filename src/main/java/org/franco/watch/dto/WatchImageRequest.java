package org.franco.watch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WatchImageRequest(
        @NotBlank @Size(max = 1000) String imageUrl,
        @Size(max = 255) String altText,
        @Min(0) Integer sortOrder,
        Boolean cover) {
}
