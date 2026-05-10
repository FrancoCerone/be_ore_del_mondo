package org.franco.watch.resource;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import org.franco.common.dto.PaginatedResponse;
import org.franco.watch.dto.WatchResponse;
import org.franco.watch.dto.WatchSearchCriteria;
import org.franco.watch.service.WatchService;

@Path("/api/watches")
@Produces(MediaType.APPLICATION_JSON)
public class WatchResource {

    private final WatchService watchService;

    public WatchResource(WatchService watchService) {
        this.watchService = watchService;
    }

    @GET
    public PaginatedResponse<WatchResponse> list(
            @QueryParam("brand") String brand,
            @QueryParam("minPrice") BigDecimal minPrice,
            @QueryParam("maxPrice") BigDecimal maxPrice,
            @QueryParam("featured") Boolean featured,
            @QueryParam("published") Boolean published,
            @QueryParam("search") String search,
            @QueryParam("page") @Min(0) Integer page,
            @QueryParam("size") @Min(1) @Max(100) Integer size,
            @QueryParam("sort") String sort) {
        return watchService.search(new WatchSearchCriteria(brand, minPrice, maxPrice, featured, published, search), page, size, sort);
    }

    @GET
    @Path("/featured")
    public PaginatedResponse<WatchResponse> featured(
            @QueryParam("page") @Min(0) Integer page,
            @QueryParam("size") @Min(1) @Max(100) Integer size,
            @QueryParam("sort") String sort) {
        return watchService.search(new WatchSearchCriteria(null, null, null, true, true, null), page, size, sort);
    }

    @GET
    @Path("/brand/{brand}")
    public PaginatedResponse<WatchResponse> byBrand(
            @PathParam("brand") String brand,
            @QueryParam("page") @Min(0) Integer page,
            @QueryParam("size") @Min(1) @Max(100) Integer size,
            @QueryParam("sort") String sort) {
        return watchService.search(new WatchSearchCriteria(brand, null, null, null, true, null), page, size, sort);
    }

    @GET
    @Path("/{slug}")
    public WatchResponse getBySlug(@PathParam("slug") String slug) {
        return watchService.getBySlug(slug);
    }

    @GET
    @Path("/id/{id}")
    public WatchResponse getById(@PathParam("id") Long id) {
        return watchService.getById(id);
    }
}
