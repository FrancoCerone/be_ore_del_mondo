package org.franco.watch.service;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.franco.common.dto.PaginatedResponse;
import org.franco.common.exception.ApiNotFoundException;
import org.franco.common.exception.ConflictException;
import org.franco.watch.dto.WatchImageRequest;
import org.franco.watch.dto.WatchRequest;
import org.franco.watch.dto.WatchResponse;
import org.franco.watch.dto.WatchSearchCriteria;
import org.franco.watch.entity.Watch;
import org.franco.watch.entity.WatchImage;
import org.franco.watch.mapper.WatchMapper;
import org.franco.watch.repository.WatchRepository;

@ApplicationScoped
public class WatchService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "createdAt", "updatedAt", "price", "name", "brand", "model", "year", "stock");

    private final WatchRepository watchRepository;
    private final WatchMapper watchMapper;
    private final SlugService slugService;

    public WatchService(WatchRepository watchRepository, WatchMapper watchMapper, SlugService slugService) {
        this.watchRepository = watchRepository;
        this.watchMapper = watchMapper;
        this.slugService = slugService;
    }

    @Transactional
    public PaginatedResponse<WatchResponse> adminSearch(String search, Integer page, Integer size, String sort) {
        WatchSearchCriteria criteria = new WatchSearchCriteria(null, null, null, null, null, search);
        int currentPage = Math.max(page == null ? 0 : page, 0);
        int pageSize = Math.min(Math.max(size == null ? DEFAULT_PAGE_SIZE : size, 1), MAX_PAGE_SIZE);
        var query = watchRepository.search(criteria, parseSort(sort)).page(Page.of(currentPage, pageSize));
        List<WatchResponse> items = query.list().stream().map(watchMapper::toResponse).toList();
        return PaginatedResponse.of(items, currentPage, pageSize, query.count());
    }

    @Transactional
    public PaginatedResponse<WatchResponse> search(WatchSearchCriteria criteria, Integer page, Integer size, String sort) {
        int currentPage = Math.max(page == null ? 0 : page, 0);
        int pageSize = Math.min(Math.max(size == null ? DEFAULT_PAGE_SIZE : size, 1), MAX_PAGE_SIZE);
        var query = watchRepository.search(defaultPublished(criteria), parseSort(sort)).page(Page.of(currentPage, pageSize));
        List<WatchResponse> items = query.list().stream().map(watchMapper::toResponse).toList();
        return PaginatedResponse.of(items, currentPage, pageSize, query.count());
    }

    @Transactional
    public WatchResponse getBySlug(String slug) {
        return watchMapper.toResponse(watchRepository.findBySlug(slug)
                .filter(watch -> Boolean.TRUE.equals(watch.published))
                .orElseThrow(() -> new ApiNotFoundException("Watch not found")));
    }

    @Transactional
    public WatchResponse getById(Long id) {
        return watchMapper.toResponse(watchRepository.findByIdOptional(id)
                .orElseThrow(() -> new ApiNotFoundException("Watch not found")));
    }

    @Transactional
    public WatchResponse create(WatchRequest request) {
        Watch watch = watchMapper.toEntity(request);
        applyDefaults(watch);
        watch.slug = slugService.uniqueSlug(request.name(), null);
        replaceImages(watch, request.images());
        watchRepository.persist(watch);
        return watchMapper.toResponse(watch);
    }

    @Transactional
    public WatchResponse update(Long id, WatchRequest request) {
        Watch watch = watchRepository.findByIdOptional(id)
                .orElseThrow(() -> new ApiNotFoundException("Watch not found"));
        watchMapper.updateEntity(request, watch);
        applyDefaults(watch);
        watch.slug = slugService.uniqueSlug(request.name(), id);
        replaceImages(watch, request.images());
        return watchMapper.toResponse(watch);
    }

    @Transactional
    public void delete(Long id) {
        boolean deleted = watchRepository.deleteById(id);
        if (!deleted) {
            throw new ApiNotFoundException("Watch not found");
        }
    }

    private WatchSearchCriteria defaultPublished(WatchSearchCriteria criteria) {
        return new WatchSearchCriteria(
                criteria.brand(),
                criteria.minPrice(),
                criteria.maxPrice(),
                criteria.featured(),
                criteria.published() == null ? true : criteria.published(),
                criteria.search());
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("createdAt", Sort.Direction.Descending);
        }
        String field = sort;
        Sort.Direction direction = Sort.Direction.Ascending;
        if (sort.startsWith("-")) {
            field = sort.substring(1);
            direction = Sort.Direction.Descending;
        } else if (sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            field = parts[0];
            direction = "desc".equalsIgnoreCase(parts[1]) ? Sort.Direction.Descending : Sort.Direction.Ascending;
        }
        if (!SORTABLE_FIELDS.contains(field)) {
            field = "createdAt";
            direction = Sort.Direction.Descending;
        }
        return Sort.by(field, direction);
    }

    private void applyDefaults(Watch watch) {
        watch.currency = watch.currency == null || watch.currency.isBlank() ? "EUR" : watch.currency.toUpperCase();
        watch.stock = watch.stock == null ? 0 : watch.stock;
        watch.featured = watch.featured != null && watch.featured;
        watch.published = watch.published != null && watch.published;
    }

    private void replaceImages(Watch watch, List<WatchImageRequest> requests) {
        watch.images.clear();
        if (requests == null || requests.isEmpty()) {
            return;
        }
        long covers = requests.stream().filter(image -> Boolean.TRUE.equals(image.cover())).count();
        if (covers > 1) {
            throw new ConflictException("Only one cover image is allowed");
        }
        for (int index = 0; index < requests.size(); index++) {
            WatchImageRequest request = requests.get(index);
            WatchImage image = watchMapper.toImageEntity(request);
            image.watch = watch;
            image.sortOrder = request.sortOrder() == null ? index : request.sortOrder();
            image.cover = covers == 0 ? index == 0 : Boolean.TRUE.equals(request.cover());
            watch.images.add(image);
        }
    }
}
