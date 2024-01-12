package com.inventory.app.server.entity.user;

import javax.persistence.*;

@MappedSuperclass
public class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Integer version;

}
