package com.example.sbb.user;


import groovy.cli.internal.OptionAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
//스프링 시큐리티에 등록할 UserSecurityService는 스프링 시큐리티가 제공하는 UserDetailsService 인터페이스를
// 구현(implements)해야 한다. 해당 인터페이스는 loadUserByUsername 메서드를 구현하도록 강제하는 인터페이스이다.
public class UserSecurityService implements UserDetailsService {//UserSecurityService는 스프링 시큐리티 로그인 처리의 핵심부분
    private final UserRepository userRepository;
    
    @Override//사용자명으로 비밀번호를 조회하여 리턴하는 메서드, 조회했을때 데이터 없으면 아래 에러 발생 시킴(일치 판별하는게 아니고 그냥 비밀번호가 존재하는지 확인)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
        //해당 id의 사용자 존재 X
        if(_siteUser.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        //존재 O
        SiteUser siteUser = _siteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if("admin".equals(username)){//사용자의 역할(관리자 or 일반 유저)에 따라 권한 부여
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        //사용자명, 비밀번호, 권한을 입력으로 한 스프링 시큐리티의 User 객체를 생성하여 리턴
        //스프링 시큐리티는 리턴하는 객체의 비밀번호가 입력받은 비밀번호와 일치하는지 확인하는 로직을 내부적으로 가짐!!!
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }

}
