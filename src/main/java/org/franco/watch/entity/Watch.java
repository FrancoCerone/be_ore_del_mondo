package org.franco.watch.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "watches")
public class Watch extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(nullable = false, length = 180)
    public String name;

    @Column(nullable = false, unique = true, length = 220)
    public String slug;

    @Column(columnDefinition = "text")
    public String description;

    @Column(name = "short_description", length = 500)
    public String shortDescription;

    @Column(nullable = false, length = 120)
    public String brand;

    @Column(nullable = false, length = 120)
    public String model;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal price;

    @Column(nullable = false, length = 3)
    public String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public WatchCondition condition;

    @Column(name = "production_year")
    public Integer year;

    @Column(name = "reference_number", length = 120)
    public String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    public MovementType movement;

    @Column(name = "case_material", length = 120)
    public String caseMaterial;

    @Column(name = "strap_material", length = 120)
    public String strapMaterial;

    @Column(precision = 5, scale = 2)
    public BigDecimal diameter;

    @Column(name = "water_resistance", length = 80)
    public String waterResistance;

    @Column(nullable = false)
    public Integer stock;

    @Column(nullable = false)
    public Boolean featured;

    @Column(nullable = false)
    public Boolean published;

    @Column(name = "seo_title", length = 180)
    public String seoTitle;

    @Column(name = "seo_description", length = 320)
    public String seoDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    public OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "watch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, id ASC")
    public List<WatchImage> images = new ArrayList<>();

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now();
        uuid = uuid == null ? UUID.randomUUID() : uuid;
        createdAt = now;
        updatedAt = now;
        featured = featured != null && featured;
        published = published != null && published;
        stock = stock == null ? 0 : stock;
        currency = currency == null ? "EUR" : currency.toUpperCase();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
        currency = currency == null ? "EUR" : currency.toUpperCase();
    }
}
