package com.inventory.app.server.entity.user;


import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity(name = "UserDetails")
@Table(name = "user_details")
public class UserDetails {
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email_address")
    private String emailAddress;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;


}
