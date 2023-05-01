package com.example.sbb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;//의존성 객체 주입
    private final PasswordEncoder passwordEncoder;//의존성 객체 주입

    //UserRepository 를 사용하여 User 데이터를 생성하는 create 메서드
    //비밀번호는 보안을 위해 반드시 암호화해야 한다.(시큐리티의 BCryptPasswordEncoder 클래스를 이용)
    public SiteUser create(String username, String email, String password){
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }
}
