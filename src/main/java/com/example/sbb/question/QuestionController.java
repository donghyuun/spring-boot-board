package com.example.sbb.question;

import java.util.List;

import com.example.sbb.answer.AnswerForm;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;

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

    @GetMapping("/create")
    public String createQuestion(QuestionForm questionForm){
        return "question_form";
    }

    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "question_form";
        }

        this.questionService.create(questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/list";
    }
}
