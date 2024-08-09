package com.uk.uk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
@Entity
@Getter
@Setter
public class AuthEntity {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue
    private long id;
    private String username;
    private String userEmail;
    private String password;
    private String tempOtp;
}

