package codesquad.service;

import codesquad.CannotManageException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static java.util.Optional.ofNullable;

@Service("qnaService")
@Transactional
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) throws CannotManageException {
        return findOneOrElseThrow(id);
    }

    public Question update(User loginUser, long id, Question updatedQuestion) throws CannotManageException {
        return findOneOrElseThrow(id).update(loginUser, updatedQuestion);
    }

    public void deleteQuestion(User loginUser, long questionId) throws CannotManageException {
        Question question = questionRepository.findOne(questionId);
        question.deleted(loginUser);
    }

    public Iterable<Question> findAll() { return questionRepository.findAll(); }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findByDeleted(false, pageable);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        return null;
    }

    private Question findOneOrElseThrow(long id) throws CannotManageException {
        return ofNullable(questionRepository.findOne(id)).orElseThrow(() -> new CannotManageException("원본 글이 없습니다."));
    }
}
