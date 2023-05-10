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

import com.example.sbb.answer.Answer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/*
select //distinct 뒤에 중복을 제거할 속성들을 적어준다. 검색 결과에서 아래 속성들이 중복되는 경우, 중복된 결과들을 제거한다.
    distinct q.id,
    q.author_id,
    q.content,
    q.create_date,
    q.modify_date,
    q.subject
from question q
left outer join site_user u1 on q.author_id=u1.id
left outer join answer a on q.id=a.question_id
left outer join site_user u2 on a.author_id=u2.id
where //검색
    q.subject like '%스프링%' //q.subject가 "스프링"을 포함하는지 검색
    or q.content like '%스프링%'
    or u1.username like '%스프링%'
    or a.content like '%스프링%'
    or u2.username like '%스프링%'
* */
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    //Specification은 정교한 쿼리 작성을 도와주는 JPA의 도구이다.
    private Specification<Question> search(String kw){//검색어(kw)를 입력받아 쿼리의 조인문과 where문을 생성하여 리턴하는 메서드
        return new Specification<>(){
            private static final long serialVersionUID = 1L;
            @Override                    //q <- 기준을 의미하는 Question 엔티티의 객체(질문 제목과 내용을 검색하기 위해 필요)
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb){
                query.distinct(true);//중복을 제거
                //Question 엔티티와 SiteUser 엔티티를 아우터 조인(JoinType.LEFT)하여 만든 SiteUser 엔티티의 객체
                //두 엔티티가 author 속성으로 연결되어 있기 때문에 q.join("author")와 같이 조인해야 한다.(질문 작성자를 검색하기 위해 필요)
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                //Question 엔티티와 Answer 엔티티를 아우터 조인하여 만든 Answer 엔티티의 객체
                //두 엔티티가 answerList 속성으로 연결되어 있어 q.join("answerList")와 같이 조인해야 한다.(답변 내용을 검색하기 위해 필요)
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);//(답변 작성자를 검색하기 위해 필요)
                return cb.or(
                        //객체.get(객체의 속성명)으로 해당 속성의 값을 가져와서
                        //like로 검색어가 포함되어 있는지를 검색하고, 각각의 결과에 대해 최종적으로 OR(합집합!) 검색되게 함
                        cb.like(q.get("subject"), "%" + kw + "%"),//제목
                        cb.like(q.get("content"), "%" + kw + "%"),//내용
                        cb.like(u1.get("username"), "%" + kw + "%"),//질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),//답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%")//답변 작성자
                );
            }
        };
    }

    public Page<Question> getList(int page, String kw){//컨트롤러로부터 조회할 페이지 번호와 검색 입력값 전달받음
        List<Sort.Order> sorts = new ArrayList<>();//Sort.Order(정렬조건)객체들의 List
        sorts.add(Sort.Order.desc("createDate"));//정렬조건 add
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));//Pageable 객체 생성
        /* Specification 사용하는 경우
        Specification<Question> spec = search(kw);//search 함수 <= 쿼리의 조인문과 where문을 생성하여 Specification<Question> 객체를 생성해 여기에 담아서 반환함
        return this.questionRepository.findAll(spec, pageable);// spec객체와 pageable객체를 참고하여 해당하는 question list 찾아서 반환
        */
        //쿼리 직접 작성한 경우
        return this.questionRepository.findAllByKeyword(kw, pageable);
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
