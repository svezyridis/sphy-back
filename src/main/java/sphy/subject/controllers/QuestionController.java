package sphy.subject.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sphy.Validator;
import sphy.subject.db.SubjectRepository;
import sphy.subject.models.Option;
import sphy.subject.models.Question;
import sphy.subject.models.RestResponse;

import java.util.Collections;
import java.util.List;

@RestController
public class QuestionController {
    Logger logger = LoggerFactory.getLogger(SubjectController.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcSubjectRepository")
    private SubjectRepository subjectRepository;

    @Autowired
    Validator validator;

    @RequestMapping("/questions/{subject}")
    public RestResponse getQuestionsBySubject(@PathVariable String subject, @RequestParam(value = "token") String token) {
        if (!validator.simpleValidateToken(token))
            return new RestResponse("error", null, "invalid token");
        Integer subjectID = subjectRepository.getSubjectID(subject);
        List<Question> questions = subjectRepository.getQuestionsOfSubject(subjectID);
        for (Question question : questions) {
            List<Option> options = subjectRepository.getOptionsOfQuestion(question.getID());
            question.setOptionList(options);
        }
        return new RestResponse("success", questions,"");
    }
}
