package codesquad.web.api;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void question의_get요청이_정상적인가() {

    }

    @Test
    public void question_리스트의_get요청이_정상적인가() {
        ResponseEntity<String> response = template().getForEntity("/api/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        //TODO: 데이터 내용 확인하기(키값으로 확인하면 될듯
    }

    @Test
    public void question_생성을_위한_post요청이_정상적인가() {

    }

    @Test
    public void question_수정을_위한_put요청이_정상적인가() {

    }

    @Test
    public void question_삭제를_위한_delete요청이_정상적인가() {

    }
}
