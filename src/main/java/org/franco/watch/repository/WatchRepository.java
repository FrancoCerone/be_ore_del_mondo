package org.franco.watch.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Optional;
import org.franco.watch.dto.WatchSearchCriteria;
import org.franco.watch.entity.Watch;

@ApplicationScoped
public class WatchRepository implements PanacheRepositoryBase<Watch, Long> {

    public Optional<Watch> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

    public boolean slugExists(String slug, Long excludedId) {
        if (excludedId == null) {
            return count("slug", slug) > 0;
        }
        return count("slug = ?1 and id <> ?2", slug, excludedId) > 0;
    }

    public PanacheQuery<Watch> search(WatchSearchCriteria criteria, Sort sort) {
        var query = new StringBuilder("1 = 1");
        var params = new HashMap<String, Object>();

        if (criteria.brand() != null && !criteria.brand().isBlank()) {
            query.append(" and lower(brand) = :brand");
            params.put("brand", criteria.brand().toLowerCase());
        }
        if (criteria.minPrice() != null) {
            query.append(" and price >= :minPrice");
            params.put("minPrice", criteria.minPrice());
        }
        if (criteria.maxPrice() != null) {
            query.append(" and price <= :maxPrice");
            params.put("maxPrice", criteria.maxPrice());
        }
        if (criteria.featured() != null) {
            query.append(" and featured = :featured");
            params.put("featured", criteria.featured());
        }
        if (criteria.published() != null) {
            query.append(" and published = :published");
            params.put("published", criteria.published());
        }
        if (criteria.search() != null && !criteria.search().isBlank()) {
            query.append(" and (lower(name) like :search or lower(brand) like :search or lower(model) like :search)");
            params.put("search", "%" + criteria.search().toLowerCase() + "%");
        }

        return find(query.toString(), sort, params);
    }
}
