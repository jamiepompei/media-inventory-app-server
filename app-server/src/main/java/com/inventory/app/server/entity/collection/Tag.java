package com.inventory.app.server.entity.collection;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;


@Entity(name= "Tag")
@Table(name = "tag")
@Getter
@Setter
public class Tag implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @NaturalId
    private String tag;
}
