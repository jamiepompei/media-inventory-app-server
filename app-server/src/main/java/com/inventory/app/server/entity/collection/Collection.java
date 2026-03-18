package com.inventory.app.server.entity.collection;

import com.inventory.app.server.entity.media.Media;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.inventory.app.server.util.MediaServerUtility.getCurrentUsername;

@Entity
@Table(name = "collection", uniqueConstraints = @UniqueConstraint(columnNames = {"collection_title", "created_by"}))
@Getter
@Setter
public class Collection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collection_title", nullable = false)
    private String collectionTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @ManyToMany
    @JoinTable(
            name = "collection_media",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    @BatchSize(size = 10) // Optimized lazy loading
    private List<Media> media;

    @PreUpdate
    public void onModify() {
        this.modifiedBy = getCurrentUsername();
        this.modifiedOn = LocalDateTime.now();
    }
    @PrePersist
    public void onCreate() {
        this.createdOn = LocalDateTime.now();
        this.createdBy = getCurrentUsername();
        this.modifiedOn = LocalDateTime.now();
        this.modifiedBy = getCurrentUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collection that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCollectionTitle(), that.getCollectionTitle()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getCreatedBy(), that.getCreatedBy()) && Objects.equals(getCreatedOn(), that.getCreatedOn()) && Objects.equals(getModifiedOn(), that.getModifiedOn()) && Objects.equals(getModifiedBy(), that.getModifiedBy()) && Objects.equals(getMedia(), that.getMedia());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCollectionTitle(), getDescription(), getCreatedBy(), getCreatedOn(), getModifiedOn(), getModifiedBy(), getMedia());
    }
}
