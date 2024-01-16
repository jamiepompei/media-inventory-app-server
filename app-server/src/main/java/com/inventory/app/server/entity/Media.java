package com.inventory.app.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Integer version;
    @Column(name = "title")
    private String title;
    @Column(name = "format")
    private String format;
    @Column
    private String genre;
    @Column(name = "collection_name")
    private String collectionName;
}
