package codesquad.web.api;

import codesquad.CannotManageException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import codesquad.domain.Question;
import codesquad.service.QnaService;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestion(@PathVariable Long id) throws CannotManageException {
        return ResponseEntity.ok(qnaService.findById(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQuestions(@PageableDefault Pageable pageable) {
        Page<Question> boards = qnaService.findAll(pageable);
        PageMetadata pageMetadata = new PageMetadata(pageable.getPageSize(), boards.getNumber(), boards.getTotalElements());
        PagedResources<Question> resources = new PagedResources<>(boards.getContent(), pageMetadata);
        resources.add(linkTo(methodOn(ApiQuestionController.class).getQuestions(pageable)).withSelfRel());

        return ResponseEntity.ok(resources);
    }

    @PostMapping
    public ResponseEntity<?> postQuestion(@Valid @RequestBody QuestionDto questionDto, @LoginUser User loginUser) {
        Question question = qnaService.create(loginUser, Question.convert(questionDto));

        return new ResponseEntity<>(httpHeaders(question.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto, @LoginUser User loginUser) throws CannotManageException {
        Question question = qnaService.update(loginUser, id, Question.convert(questionDto));

        return new ResponseEntity<>(httpHeaders(question.getId()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id, @LoginUser User loginUser) throws CannotManageException {
        qnaService.deleteQuestion(loginUser, id);

        return new ResponseEntity<>(httpHeaders(id), HttpStatus.OK);
    }

    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> getAnswer(@PathVariable Long questionId, @PathVariable Long answerId) throws CannotManageException {
        return ResponseEntity.ok(qnaService.findOneAnswer(answerId));
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> postAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerDto answerDto, @LoginUser User loginUser) throws CannotManageException {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto.getContents());

        return new ResponseEntity<>(answerHttpHeaders(answer.getQuestion().getId(), answer.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> putAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @RequestBody AnswerDto answerDto, @LoginUser User loginUser) throws CannotManageException {
        Answer answer = qnaService.updateAnswer(loginUser, answerId, Answer.convert(loginUser, answerDto.getContents()));

        return new ResponseEntity<>(answerHttpHeaders(questionId, answer.getId()), HttpStatus.OK);
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @LoginUser User loginUser) throws CannotManageException {
        qnaService.deleteAnswer(loginUser, answerId);

        return new ResponseEntity<>(answerHttpHeaders(questionId, answerId), HttpStatus.OK);
    }

    private HttpHeaders httpHeaders(long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id));
        return headers;
    }

    private HttpHeaders answerHttpHeaders(long questionId, long answerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + answerId));
        return headers;
    }
}
