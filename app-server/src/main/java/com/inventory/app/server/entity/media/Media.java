package com.inventory.app.server.entity.media;

import com.inventory.app.server.entity.collection.Collection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Integer version;
    @Column(name = "title")
    private String title;
    @Column(name = "format")
    private String format;
    @ManyToOne(fetch = FetchType.LAZY)
    private Collection collection;

}
