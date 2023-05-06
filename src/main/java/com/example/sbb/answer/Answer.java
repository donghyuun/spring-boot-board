package com.example.sbb.answer;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    //ManyToMany 관계로 속성을 생성하면 새로운 테이블을 생성하여 데이터를 관리함
    @ManyToMany//추천한 사람(SiteUser 객체), 하나의 답변에 여러사람이 추천할 수 있고, 한사람이 여러개의 답변에 추천할 수 있으므로 ManyToMany(대등한 관게)이다.
    Set<SiteUser> voter;//추천인은 중복되면 안되므로 List가 아닌 Set(중복 허용 X)
}