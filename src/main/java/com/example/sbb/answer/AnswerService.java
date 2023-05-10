package com.example.sbb.answer;

import com.example.sbb.DataNotFoundException;
import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;//답변 시 작성자를 저장할 수 있도록
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer getAnswer(Integer id){
        Optional<Answer> answer = this.answerRepository.findById(id);
        if(answer.isPresent()){
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    //답변 등록
    public Answer create(Question question, String content, SiteUser author){//answer 객체를 생성하여 저장
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;//컨트롤러에서 답변이 등록된 위치로 이동하기 위해 해당 답변 객체를 넘겨줌
    }

    //답변 수정
    public void modify(Answer answer, String content){
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    //답변 삭제
    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }

    //답변 추천
    public void vote(Answer answer, SiteUser siteUser){
        //해당 답변 객체의 Voter(추천인 set)에 사용자 객체 추가
        answer.getVoter().add(siteUser);
        //변경 사항 저장
        this.answerRepository.save(answer);
    }
}
