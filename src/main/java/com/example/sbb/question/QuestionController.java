package com.example.sbb.question;

import java.security.Principal;
import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import com.example.sbb.answer.AnswerForm;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.prepost.PreAuthorize;

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

    @PreAuthorize("isAuthenticated()")
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
}
