package com.inventory.app.server.entity.collection;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;


@Entity(name= "Tag")
@Table(name = "tag")
@Getter
@Setter
public class Tag {
    @NaturalId
    private String tag;
}
