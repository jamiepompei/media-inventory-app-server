package com.inventory.app.server.entity.collection;

import com.inventory.app.server.entity.media.Media;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Entity(name= "Tag")
@Table(name = "tags")
@Getter
@Setter
public class Tag implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    private String tag;
    @ManyToMany
    @JoinTable(
            name = "tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    @BatchSize(size = 10) // Optimized lazy loading
    private List<Media> media;

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", media=" + media +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag1)) return false;
        return Objects.equals(getId(), tag1.getId()) && Objects.equals(getTag(), tag1.getTag()) && Objects.equals(getMedia(), tag1.getMedia());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTag(), getMedia());
    }
}
