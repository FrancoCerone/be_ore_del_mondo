package org.franco.watch.mapper;

import org.franco.watch.dto.WatchImageRequest;
import org.franco.watch.dto.WatchImageResponse;
import org.franco.watch.dto.WatchRequest;
import org.franco.watch.dto.WatchResponse;
import org.franco.watch.entity.Watch;
import org.franco.watch.entity.WatchImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface WatchMapper {

    WatchResponse toResponse(Watch watch);

    WatchImageResponse toImageResponse(WatchImage image);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    Watch toEntity(WatchRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateEntity(WatchRequest request, @MappingTarget Watch watch);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "watch", ignore = true)
    WatchImage toImageEntity(WatchImageRequest request);
}
