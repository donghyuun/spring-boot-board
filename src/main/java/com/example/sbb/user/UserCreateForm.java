package com.example.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {//값 검증을 위해 객체를 생성함
    @Size(min = 3, max = 25)//문자열 길이 검증
    @NotEmpty(message = "사용자 ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;//비밀번호

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;//비밀번호 확인

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email//해당 속성의 값이 이메일 형식과 일치하는지 검증
    private String email;


}
