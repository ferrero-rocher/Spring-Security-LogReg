package com.example.springloginregistration.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    @Column(length = 60)
    private String password;
    private String role;
    private boolean isenabled = false;
}
