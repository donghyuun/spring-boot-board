package com.example.sbb.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.sbb.answer.Answer;
import com.example.sbb.user.SiteUser;
import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    //ManyToMany 관계로 속성을 생성하면 새로운 테이블을 생성하여 데이터를 관리함
    @ManyToMany//추천한 사람(SiteUser 객체), 하나의 질문에 여러사람이 추천할 수 있고, 한사람이 여러개의 질문에 추천할 수 있으므로 ManyToMany(대등한 관게)이다.
    Set<SiteUser> voter;//추천인은 중복되면 안되므로 List가 아닌 Set(중복 허용 X)
}