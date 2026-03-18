package com.inventory.app.server.entity.media;

import com.inventory.app.server.entity.collection.Collection;
import com.inventory.app.server.entity.collection.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.inventory.app.server.util.MediaServerUtility.getCurrentUsername;

@Entity
@Table(name = "media", uniqueConstraints = @UniqueConstraint(columnNames = {"title", "created_by"}))
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED) // Allows different types of media
public abstract class Media implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(name = "format")
    private String format;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "onLoan")
    private Boolean onLoan;

    @Column(name = "reviewRating")
    private Integer reviewRating;

    @Column(name = "reviewDescription")
    private String reviewDescription;

    @ManyToMany(mappedBy = "media", cascade = CascadeType.ALL)
    private List<Collection> collections;

    @ManyToMany(mappedBy = "media", cascade = CascadeType.ALL)
    private Set<Tag> tags;

    @PrePersist
    public void onCreate(){
        this.createdBy = getCurrentUsername();
        this.createdOn = LocalDateTime.now();
        this.modifiedBy = getCurrentUsername();
        this.modifiedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void onModify(){
        this.modifiedBy = getCurrentUsername();
        this.modifiedOn = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", format='" + format + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modifiedOn=" + modifiedOn +
                ", completed=" + completed +
                ", onLoan=" + onLoan +
                ", reviewRating=" + reviewRating +
                ", reviewDescription='" + reviewDescription + '\'' +
                ", collections=" + collections +
                ", tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media media)) return false;
        return Objects.equals(getId(), media.getId()) && Objects.equals(getTitle(), media.getTitle()) && Objects.equals(getGenre(), media.getGenre()) && Objects.equals(getFormat(), media.getFormat()) && Objects.equals(getCreatedBy(), media.getCreatedBy()) && Objects.equals(getCreatedOn(), media.getCreatedOn()) && Objects.equals(getModifiedBy(), media.getModifiedBy()) && Objects.equals(getModifiedOn(), media.getModifiedOn()) && Objects.equals(getCompleted(), media.getCompleted()) && Objects.equals(getOnLoan(), media.getOnLoan()) && Objects.equals(getReviewRating(), media.getReviewRating()) && Objects.equals(getReviewDescription(), media.getReviewDescription()) && Objects.equals(getCollections(), media.getCollections()) && Objects.equals(getTags(), media.getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getGenre(), getFormat(), getCreatedBy(), getCreatedOn(), getModifiedBy(), getModifiedOn(), getCompleted(), getOnLoan(), getReviewRating(), getReviewDescription(), getCollections(), getTags());
    }
}
