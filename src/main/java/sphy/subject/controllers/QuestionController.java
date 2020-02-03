package sphy.subject.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sphy.Validator;
import sphy.subject.db.QuestionRepository;
import sphy.subject.db.SubjectRepository;
import sphy.subject.models.*;
import sphy.RestResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QuestionController {
    Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcSubjectRepository")
    private SubjectRepository subjectRepository;

    @Autowired
    @Qualifier("jdbcQuestionRepository")
    private QuestionRepository questionRepository;

    @Autowired
    Validator validator;

    @RequestMapping("/questions")
    public RestResponse getQuestionsOfCategories(@RequestParam(value = "categoryIDs") List<Integer> categoryIDs, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        logger.info("[QuestionController]:[getQuestionsOfCategories]:{IDs: "+categoryIDs+" }");
        if (!validator.simpleValidateToken(token))
            return new RestResponse("error", null, "invalid token");
        List<Question>questions=questionRepository.getQuestionsOfCategories(categoryIDs);
        if(questions==null)
            return new RestResponse("error", null, "questions could not be fetched");
        return new RestResponse("success", questions,null);
    }

    @PostMapping("/questions/{subject}")
    public RestResponse createQuestion(@PathVariable String subject, @CookieValue(value = "jwt", defaultValue = "token") String token, @RequestBody NewQuestion questionToAdd){
        logger.info("[QuestionController]:[createQuestion]:{subject: "+subject+" }");
        if (!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid ADMIN token");
        Integer subjectID = subjectRepository.getSubjectID(subject);
        if(subjectID==-1)
            return new RestResponse("error",null,"subject does not exist");
        Question question=questionToAdd.getNewQuestion();
        if(!validateQuestion(question))
            return new RestResponse("error",null,"question provided is missing attributes");
        Integer result=questionRepository.createQuestion(subjectID,question);
        if(result==-1)
            return new RestResponse("error",null,"question could not be created");
        return new RestResponse("success",null,"question created successfully");
    }

    @DeleteMapping("/questions/{questionID}")
    public RestResponse deleteQuestion(@PathVariable Integer questionID,@CookieValue(value = "jwt", defaultValue = "token") String token){
        logger.info("[QuestionController]:[deleteQuestion]:{questionID: "+questionID+" }");
        if(!questionRepository.checkIfExists(questionID))
            return new RestResponse("error",null,"question does not exist");
        Integer res=questionRepository.deleteQuestion(questionID);
        if(res==-1)
            return new RestResponse("error",null,"could not delete question");
        return new RestResponse("success",null,"question deleted successfully");
    }

    private boolean validateQuestion(Question question){
        return question.getAnswerReference()!=null&&question.getText()!=null;
    }
}
