package com.example.sbb.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)//중복 금지
    private String username;

    private String password;//Column 세부 속성 설정 안할 거면 @Column 필요없는 듯

    @Column(unique = true)//중복 금지
    private String email;

}
