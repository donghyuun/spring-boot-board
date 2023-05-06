package com.example.sbb.question;

import java.awt.print.Printable;
import java.security.Principal;
import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import com.example.sbb.answer.AnswerForm;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page){
        Page<Question> paging = this.questionService.getList(page);//원하는 페이지 전달
        model.addAttribute("paging", paging);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm){
       Question question = this.questionService.getQuestion(id);
       model.addAttribute("question", question);
       return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드로 설정, 로그아웃인 상태인 경우 로그인페이지로 이동 (로그인 판별기능)
    @GetMapping("/create")
    public String createQuestion(QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            return "question_form";
        }

        //principal는 스프링 시큐리티가 제공하는 객체, getName()을 통해 로그인한 사용자의 사용자명을 알 수 있음
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal){
        //해당 id로 질문객체 검색
        Question question = this.questionService.getQuestion(id);
        //질문의 작성자와 현재 로그인한 사용자가 동일하지 않는 경우, 에러발생시킴
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        //동일한 경우, 질문 객체 수정, 수정할 질문의 제목과 내용을 화면에 보여주기 위해 questionForm 객체에 기존의 값을 담아서 템플릿으로 전달한다.
        //이 과정이 없으면 수정할 제목과 내용이 비워져보임
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";//질문 등록 템플릿을 수정시에도 그대로 사용(다만 해당 템플릿에서 설정 필요, 설정안하면 그냥 새 질문으로 등록됨)
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
        //입력 내용이 모두 채워지지 않은 경우, 기존 화면으로 복귀
        if(bindingResult.hasErrors()){
            return "question_form";
        }
        //DB에서 해당 id를 가진 질문 객체를 찾음
        Question question = this.questionService.getQuestion(id);
        //로그인한 사용자와 질문글의 작성자가 다른 경우
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        //같은 경우, 수정함
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        //수정 이후 해당 질문글 상세 페이지로 이동
        return String.format("redirect:/question/detail/%s", id);
    }


    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id){
        //DB에서 해당 id를 가진 질문 객체를 찾음
        Question question = this.questionService.getQuestion(id);
        //로그인한 사용자와 질문글의 작성자가 다른 경우
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        //같은 경우, 수정함
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")//로그인이 필요한 메서드
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id){
        //질문글의 id를 이용해 질문 객체 찾음
        Question question = this.questionService.getQuestion(id);
        //현재 로그인된 사용자의 이름을 이용해 사용자 객체 찾음
        SiteUser siteUser = this.userService.getUser(principal.getName());
        //질문 객체의 추천인 집합에 사용자 객체 추가
        this.questionService.vote(question, siteUser);
        //해당 질문글로 다시 복귀
        return String.format("redirect:/question/detail/%s", id);
    }
}
