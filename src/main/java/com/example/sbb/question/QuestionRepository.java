package com.example.sbb.question;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer>{
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String Subject);

    // Pageable 객체를 입력받아서 db에서 해당하는 Question 객체들의 집합을
    // Page<Question>객체로 반환
    Page<Question> findAll(Pageable pageable);//단순 페이지 이동

    //Specification 과 Pageable 객체를 입력으로 "Question 엔티티"를 조회하는 findAll 메서드를 선언
    //spec은 쿼리의 조인문과 where문을 생성한 Specification 객체를 인자로 받음
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);//검색 결과로 인한 페이지 이동

    //Specification 대신에 직접 쿼리를 작성하여 수행하는 방법, 복잡한 쿼리는 자바코드보다 직접 쿼리 작성하는게 훨씬 편함
    //Query 작성 시, 반드시 테이블기준이 아니라 엔티티 기준으로 작성해야 한다.
    @Query("select "
            + "distinct q "
            + "from Question q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + " q.subject like %:kw% "
            + " or q.content like %:kw% "
            + " or a.content like %:kw% "
            + " or u2.username like %:kw% ")
    //@Query 에너테이션이 적용된 findAllByKeyword 메서드,
    //쿼리에 파라미터로 전달할 kw 문자열은 메서드의 매개변수에 @Param 에너테이션을 사용해야 한다. kw문자열은 @Query 안에서 :kw로 참조됨
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}