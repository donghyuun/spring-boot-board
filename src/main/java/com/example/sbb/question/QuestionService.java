package com.example.sbb.question;

import com.example.sbb.user.SiteUser;
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

    public void create(String subject, String content, SiteUser user){
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }

    //질문 수정
    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);//id 가 같으므로 새로 등록되는게 아니라 "수정"됨
    }

    //질문 삭제, question 객체를 입력받아 해당 질문 데이터를 삭제
    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    //질문 추천
    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);//질문의 Voter 속성(추천인 집합)에 사용자 객체를 추가
        this.questionRepository.save(question);
    }
}
