package com.example.sbb.user;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.dao.DataIntegrityViolationException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")//회원가입 템플릿 렌더링
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PostMapping("/signup")//회원가입 진행
    public String Signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult){//회원가입 폼 객체 의존성 주입
        if(bindingResult.hasErrors()){
            return "signup_form";//템플릿 렌더링
        }
        //비밀번호1,2가 동일한지 검증
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            //bindingResult.rejectValue(필드명, 오류코드, 에러메지시) <- 오류 발생시킴
            bindingResult.rejectValue("Password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";//템플릿 렌더링
        }

        try{
            //회원가입 시도
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e){//사용자ID or 이메일 주소가 동일한 경우의 오류
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e){//다른 종류의 오류
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        //회원가입 성공
        return "redirect:/";//홈 화면으로
    }

    @GetMapping("/login")//실제 로그인을 진행하는 @PostMapping 방식의 메서드는 스프링 시큐리티가 대신 처리하므로 직접 구현할 필요 X
    public String login(){
        return "login_form";
    }
}
