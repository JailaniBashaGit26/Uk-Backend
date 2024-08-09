package com.uk.uk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "TempUser")
@Entity
@Getter
@Setter
public class TempUser {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String username;
    private String otp;
    private String userEmail;
    private String password;
}

