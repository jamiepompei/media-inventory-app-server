package com.inventory.app.server.entity;


import jakarta.persistence.*;

import java.io.Serializable;

@MappedSuperclass
public abstract class BaseCollection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
}
