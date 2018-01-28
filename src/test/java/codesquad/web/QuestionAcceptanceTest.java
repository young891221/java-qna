package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.test.AcceptanceTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private Question question;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    public void 질문_저장하기_테스트() {
        Question saveQuestion = questionRepository.save(question);
        assertEquals(question.getTitle(), saveQuestion.getTitle());
        assertEquals(question.getContents(), saveQuestion.getContents());
    }

    @Test
    public void 로그인한_사용자만_질문작성_가능한가() {

    }

    @Test
    public void 질문_리스트_불러오기_테스트() {
        List<Question> questions = IntStream.range(0, 3)
                .mapToObj(i -> makeQuestion("TestTitle"+i, "테스트당"+i))
                .collect(Collectors.toList());

        questionRepository.save(questions);
        List<Question> savedQuestions = questionRepository.findAll();
        assertEquals(3, savedQuestions.size());
    }

    @Test
    public void 질문_수정하기_테스트() {
        Question saveQuestion = questionRepository.save(question);
        saveQuestion = saveQuestion.updateTitle("updateTitle").updateContents("updateContents");
        questionRepository.save(saveQuestion);

        Question updatedQuestion = questionRepository.findOne(saveQuestion.getId());
        assertEquals(saveQuestion.getTitle(), updatedQuestion.getTitle());
        assertEquals(saveQuestion.getContents(), updatedQuestion.getContents());
    }

    @Test
    public void 질문_삭제하기_테스트() {
        Question saveQuestion = questionRepository.save(question);
        assertEquals(1, questionRepository.findAll().size());
        questionRepository.delete(saveQuestion.getId());
        assertEquals(0, questionRepository.findAll().size());
    }

    @Test
    public void 자기질문만_수정_삭제가능한가() {
        User havi = new User(2, "havi", "test", "하비", "havi@gmail.com");
    }
}
