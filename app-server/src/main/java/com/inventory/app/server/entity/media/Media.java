package com.inventory.app.server.entity.media;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

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
    private String collectionName;

    @Override
    public String toString() {
        return "id=" + id +
                ", version=" + version +
                ", title='" + title + '\'' +
                ", format='" + format + '\'' +
                ", genre='" + genre + '\'' +
                ", collectionName='" + collectionName;
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
                Objects.equals(getCollectionName(), media.getCollectionName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getVersion(),
                getTitle(),
                getFormat(),
                getGenre(),
                getCollectionName());
    }
}
