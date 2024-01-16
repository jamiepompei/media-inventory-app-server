package com.inventory.app.server.entity;


import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Integer version;
}
