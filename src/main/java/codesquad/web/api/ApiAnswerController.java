package codesquad.web.api;

import codesquad.CannotManageException;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnswer(@PathVariable Long id) throws CannotManageException {
        return ResponseEntity.ok(qnaService.findOneAnswer(id));
    }

}
