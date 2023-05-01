package com.example.sbb.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

    //로그인 시 db에 사용자 존재하는지 확인하는 기능
    Optional<SiteUser> findByusername(String username);
}
