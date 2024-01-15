package com.inventory.app.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;


@Entity(name= "Tag")
@Table(name = "tag")
@Getter
@Setter
public class Tag {
    @Id
    private Long id;
    @Version
    private Integer version;
    @NaturalId
    private String tag;
}
