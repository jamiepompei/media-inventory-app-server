package com.inventory.app.server.entity.media;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@MappedSuperclass
@Getter
@Setter
public abstract class Media implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(name = "title")
    private String title;
    @Column(name = "format")
    private String format;
    @Column(name = "genre")
    private String genre;
    @Column(name = "collection_name")
    private String collectionTitle;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_as_of")
    private LocalDateTime createdAsOf;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "modified_as_of")
    private LocalDateTime modifiedAsOf;
    @Column(name = "completed")
    private boolean completed;
    @Column(name = "onLoan")
    private boolean onLoan;
    @Column(name = "tags")
    private Set<String> tags;
    @Column(name = "review_rating")
    private Integer reviewRating;
    @Column(name = "review_description")
    private String reviewDescription;

    @Override
    public String toString() {
        return "id=" + id +
                ", version=" + version +
                ", title='" + title + '\'' +
                ", format=" + format +
                ", genre=" + genre +
                ", collectionName=" + collectionTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media media)) return false;
        return Objects.equals(getId(), media.getId()) &&
                Objects.equals(getVersion(), media.getVersion()) &&
                Objects.equals(getTitle(), media.getTitle()) &&
                Objects.equals(getFormat(), media.getFormat()) &&
                Objects.equals(getGenre(), media.getGenre()) &&
                Objects.equals(getCollectionTitle(), media.getCollectionTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getVersion(),
                getTitle(),
                getFormat(),
                getGenre(),
                getCollectionTitle());
    }
}
