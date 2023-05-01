package com.example.sbb.user;

import lombok.Getter;

//
@Getter//상수 자료형이므로 Getter 만 사용가능
public enum UserRole {//enum : 열거형
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String value){
        this.value = value;
    }

    private String value;
}
