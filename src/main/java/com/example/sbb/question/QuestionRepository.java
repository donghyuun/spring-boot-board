package com.example.sbb.question;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer>{
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String Subject);

    // Pageable 객체를 입력받아서 db에서 해당하는 Question 객체들의 집합을
    // Page<Question>객체로 반환
    Page<Question> findAll(Pageable pageable);
 }
