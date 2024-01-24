package com.inventory.app.server.entity.media;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class Media implements Serializable {
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
        return "Media{" +
                "id=" + id +
                ", version=" + version +
                ", title='" + title + '\'' +
                ", format='" + format + '\'' +
                ", genre='" + genre + '\'' +
                ", collectionName='" + collectionName + '\'' +
                '}';
    }
}
