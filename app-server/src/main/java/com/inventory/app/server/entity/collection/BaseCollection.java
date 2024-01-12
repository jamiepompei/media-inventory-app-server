package com.inventory.app.server.entity.collection;

import javax.persistence.*;

@MappedSuperclass
public class BaseCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Integer version;
}
