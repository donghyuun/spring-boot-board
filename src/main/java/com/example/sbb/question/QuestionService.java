package com.example.sbb.question;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.example.sbb.DataNotFoundException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> getList(int page){//컨트롤러로부터 조회할 페이지 번호 전달받음
        List<Sort.Order> sorts = new ArrayList<>();//Sort.Order(정렬조건)객체들의 List
        sorts.add(Sort.Order.desc("createDate"));//정렬조건 add
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));//Pageable 객체 생성
        return this.questionRepository.findAll(pageable);// pageable객체를 참고하여 해당하는 question list 찾아서 반환
    }

    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);
        if(question.isPresent()){
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content){
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q);
    }
}
