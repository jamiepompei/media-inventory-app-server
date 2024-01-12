package com.inventory.app.server.entity.collection;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name= "Tag")
@Table(name = "tag")
@Getter
@Setter
public class Tag {
    @NaturalId
    private String tag;
}
