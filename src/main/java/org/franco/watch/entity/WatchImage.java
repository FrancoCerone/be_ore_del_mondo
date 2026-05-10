package org.franco.watch.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "watch_images")
public class WatchImage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watch_id", nullable = false)
    public Watch watch;

    @Column(name = "image_url", nullable = false, length = 1000)
    public String imageUrl;

    @Column(name = "alt_text", length = 255)
    public String altText;

    @Column(name = "sort_order", nullable = false)
    public Integer sortOrder;

    @Column(name = "is_cover", nullable = false)
    public Boolean cover;
}
