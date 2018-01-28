package codesquad.web;

import codesquad.CannotManageException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static codesquad.utils.HtmlFormDataBuilder.urlEncodedForm;
import static org.junit.Assert.assertEquals;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private Question question;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QnaService qnaService;

    @Before
    public void init() {
        questionRepository.deleteAll();
        makeQuestion("TestTitle", "테스트당");
    }

    private Question makeQuestion(String title, String contents) {
        User javajigi = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        question = new Question(title, contents);
        question.writeBy(javajigi);
        return question;
    }

    private HttpEntity<MultiValueMap<String, Object>> makQnaRequest(String ti, String contents) {
        return urlEncodedForm()
                .addParameter("title", ti)
                .addParameter("contents", contents)
                .build();
    }

    @Test
    public void 질문_저장하기_테스트() {
        Question saveQuestion = questionRepository.save(question);
        assertEquals(question.getTitle(), saveQuestion.getTitle());
        assertEquals(question.getContents(), saveQuestion.getContents());
    }

    @Test
    public void 로그인한_사용자만_질문작성_가능한가() {
        ResponseEntity<String> response = template().postForEntity("/questions", makQnaRequest("제목입니다.", "내용입니다."), String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void 질문_리스트_불러오기_테스트() {
        List<Question> questions = IntStream.range(0, 3)
                .mapToObj(i -> makeQuestion("TestTitle"+i, "테스트당"+i))
                .collect(Collectors.toList());

        questionRepository.save(questions);
        List<Question> savedQuestions = qnaService.findAll(new PageRequest(0, 10));
        assertEquals(3, savedQuestions.size());
    }

    @Test
    public void 질문_수정하기_테스트() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        saveQuestion = saveQuestion.updateTitle("updateTitle").updateContents("updateContents");
        Question updatedQuestion = qnaService.update(findByUserId("javajigi"), saveQuestion.getId(), saveQuestion);

        assertEquals(saveQuestion.getTitle(), updatedQuestion.getTitle());
        assertEquals(saveQuestion.getContents(), updatedQuestion.getContents());
    }

    @Test
    public void 질문_삭제하기_테스트() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        assertEquals(1, questionRepository.findAll().size());
        qnaService.deleteQuestion(findByUserId("javajigi"), saveQuestion.getId());
        assertEquals(0, questionRepository.findAll().size());
    }

    @Test(expected = CannotManageException.class)
    public void 자신의_질문에만_수정이_가능한가() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        saveQuestion.updateContents("수정 내용입니다.");
        qnaService.update(findByUserId("sanjigi"), saveQuestion.getId(), saveQuestion);
    }

    @Test(expected = CannotManageException.class)
    public void 자신의_질문에만_삭제가_가능한가() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        qnaService.deleteQuestion(findByUserId("sanjigi"), saveQuestion.getId());
    }

    @Test
    public void 자신의_질문에_수정가능한가_통합_테스트() {
        Question saveQuestion = questionRepository.save(question);
        ResponseEntity<String> response = template().exchange("/questions/"+saveQuestion.getId(), HttpMethod.PUT, makQnaRequest("수정 제목입니다.", "수정 내용입니다."), String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void 자신의_질문에_삭제가능한가_통합_테스트() {
        Question saveQuestion = questionRepository.save(question);
        ResponseEntity<String> response = template().exchange("/questions/"+saveQuestion.getId(), HttpMethod.DELETE, urlEncodedForm().build(),  String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
