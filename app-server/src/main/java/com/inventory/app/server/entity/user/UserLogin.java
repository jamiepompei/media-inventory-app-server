package com.inventory.app.server.entity.user;

import jakarta.persistence.OneToOne;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity(name = "UserLogin")
@Table(name = "user_login")
public class UserLogin extends BaseUser {
    @NaturalId
    private String username;
    @Column(name = "password")
    private String password;
    @OneToOne
    private User user;
}
