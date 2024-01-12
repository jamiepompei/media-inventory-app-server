package com.inventory.app.server.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "UserLogin")
@Table(name = "user_login")
public class UserLogin {
    @Column(name = "user_name")
    private String username;
    @Column(name = "password")
    private String password;
}
