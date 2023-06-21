package com.example.sbb.answer;

import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드로 설정, 로그아웃인 상태인 경우 로그인페이지로 이동 (로그인 판별기능)
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "question_detail";

        }
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);//답변, 답변내용, 답변자
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());//제자리 유지
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal){
        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        answerForm.setContent(answer.getContent());
        return "answer_form";//AnswerForm 객체를 템플릿에 같이 넘겨줌
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드로 설정
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal){
        System.out.println("수정 요청이 전달되었습니다." + id);
        if(bindingResult.hasErrors()){
            return "answer_form";
        }

        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        //답변 삭제 후 해당 답변이 있는 기존 페이지로 복귀 (해당 답변의 질문 id를 이용)
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId(), answer.getId());
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드로 설정
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id){
        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);
        //답변 삭제 후 해당 답변이 있던 기존 페이지로 복귀 (해당 답변의 질문 id를 이용)
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id){
        //답변글의 id를 이용해 답변 객체 찾음
        Answer answer = this.answerService.getAnswer(id);
        //현재 로그인된 사용자의 이름을 이용해 사용자 객체 찾음
        SiteUser siteUser = this.userService.getUser(principal.getName());
        //답변 객체의 추천인 집합에 사용자 객체 추가
        this.answerService.vote(answer, siteUser);
        //답변이 있는 질문글(해당 답변이 있던 위치)로 복귀
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }
}
