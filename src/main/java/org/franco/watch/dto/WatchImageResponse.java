package org.franco.watch.dto;

public record WatchImageResponse(
        Long id,
        String imageUrl,
        String altText,
        Integer sortOrder,
        Boolean cover) {
}
