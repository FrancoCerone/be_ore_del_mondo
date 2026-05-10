package org.franco.watch.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.franco.common.dto.PaginatedResponse;
import org.franco.watch.dto.WatchRequest;
import org.franco.watch.dto.WatchResponse;
import org.franco.watch.service.WatchService;

@Path("/api/admin/watches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminWatchResource {

    private final WatchService watchService;

    public AdminWatchResource(WatchService watchService) {
        this.watchService = watchService;
    }

    @GET
    public PaginatedResponse<WatchResponse> list(
            @QueryParam("search") String search,
            @QueryParam("page") @Min(0) Integer page,
            @QueryParam("size") @Min(1) @Max(100) Integer size,
            @QueryParam("sort") String sort) {
        return watchService.adminSearch(search, page, size, sort);
    }

    @POST
    public Response create(@Valid WatchRequest request) {
        return Response.status(Response.Status.CREATED).entity(watchService.create(request)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid WatchRequest request) {
        return Response.ok(watchService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        watchService.delete(id);
        return Response.noContent().build();
    }
}
