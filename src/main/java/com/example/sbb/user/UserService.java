package com.example.sbb.user;

import com.example.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.Option;
import java.util.Optional;

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

    //해당 username(사용자명)을 가진 SiteUser 객체를 조회한다.
    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        }else{
            throw new DataNotFoundException("siteuser not found");
        }
    }
}
