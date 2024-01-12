package com.inventory.app.server.entity.collection;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name= "Tag")
@Table(name = "tags")
@Getter
@Setter
public class Tag {
    @Column(name = "tag")
    private String tag;
}
