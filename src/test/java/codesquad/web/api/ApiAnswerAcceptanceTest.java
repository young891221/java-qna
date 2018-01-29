package codesquad.web.api;

import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void answer의_get요청이_정상적인가() {
        ResponseEntity<String> response = template().getForEntity("/api/answers/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void answer_리스트의_get요청이_정상적인가() {

    }

    @Test
    public void answer_생성을_위한_post요청이_정상적인가() {
        AnswerDto answerDto = new AnswerDto((long) 1, "contents");
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/api/questions/1/answers", answerDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        AnswerDto dbAnswerDto = template().getForObject(location, AnswerDto.class);
        assertThat(dbAnswerDto, is(answerDto.setQuestionId(dbAnswerDto.getQuestionId())));
    }

    @Test
    public void answer_수정을_위한_put요청이_정상적인가() {

    }

    @Test
    public void answer_삭제를_위한_delete요청이_정상적인가() {

    }
}
