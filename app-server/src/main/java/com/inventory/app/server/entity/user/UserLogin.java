package com.inventory.app.server.entity.user;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "UserLogin")
@Table(name = "user_login")
public class UserLogin {
    @NaturalId
    private String username;
    @Column(name = "password")
    private String password;
}
