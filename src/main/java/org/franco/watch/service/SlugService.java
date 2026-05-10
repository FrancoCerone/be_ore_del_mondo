package org.franco.watch.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.Locale;
import org.franco.watch.repository.WatchRepository;

@ApplicationScoped
public class SlugService {

    private final WatchRepository watchRepository;

    public SlugService(WatchRepository watchRepository) {
        this.watchRepository = watchRepository;
    }

    public String uniqueSlug(String source, Long excludedId) {
        String base = slugify(source);
        String candidate = base;
        int suffix = 2;
        while (watchRepository.slugExists(candidate, excludedId)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private String slugify(String source) {
        String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "")
                .replaceAll("-{2,}", "-");
        return normalized.isBlank() ? "watch" : normalized;
    }
}
