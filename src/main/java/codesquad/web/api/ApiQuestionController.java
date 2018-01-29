package codesquad.web.api;

import codesquad.CannotManageException;
import codesquad.domain.User;
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

    @GetMapping("{id}")
    public ResponseEntity<?> getQuestions(@PathVariable Long id) throws CannotManageException {
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

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + question.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto, @LoginUser User loginUser) throws CannotManageException {
        Question question = qnaService.update(loginUser, id, Question.convert(questionDto));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + question.getId()));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
