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
    public void create(Question question, String content, SiteUser author){//answer 객체를 생성하여 저장
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
    }

    //답변 수정
    public void modify(Answer answer, String content){
        answer.setContent(content);
        answer.setModifyData(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    //답변 삭제
    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }

}
