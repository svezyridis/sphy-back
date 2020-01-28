package sphy.evaluation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sphy.Constants;
import sphy.RestResponse;
import sphy.Validator;
import sphy.auth.db.UserRepository;
import sphy.evaluation.db.ClassRepository;
import sphy.evaluation.db.TestRepository;
import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.Test;
import sphy.subject.controllers.CategoryController;
import sphy.subject.db.QuestionRepository;
import sphy.subject.models.Image;
import sphy.subject.models.Question;
import sphy.subject.models.Option;

import java.util.List;

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcClassRepository")
    private ClassRepository classRepository;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("jdbcQuestionRepository")
    private QuestionRepository questionRepository;

    @Autowired
    @Qualifier("jdbcTestRepository")
    private TestRepository testRepository;

    @Autowired
    Validator validator;

    @RequestMapping("test/{classroomID}")
    public RestResponse getAllTestsOfClass(@PathVariable Integer classroomID,@CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)||validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null,"invalid token");
        Integer userID=ClassesController.getUserID(token);
        if(userID==null)
            return new RestResponse("error", null,"teacher id not found in token");
        String role=ClassesController.getUserRole(token);
        Classroom classroom=classRepository.getClassByID(classroomID);
        if(classroom==null)
            return new RestResponse("error", null,"classroom not found");
        if (role== Constants.TEACHER){
            if(classroom.getCreatorID()!=userID)
                return new RestResponse("error", null,"you are not the creator of this class");
        }
        if(role==Constants.UNIT_ADMIN){
            Integer classCreatorUnitID=userRepository.getUnitID(classroom.getCreatorID());
            Integer requesterUnit=userRepository.getUnitID(userID);
            if(classCreatorUnitID!=requesterUnit)
                return new RestResponse("error", null,"this class does not belong to your unit");
        }
        List<Test> tests=testRepository.getAllTestsOfClass(classroomID);
        tests.forEach(test -> {
            List<Question> questions=testRepository.getAllQuestionsOfTest(test.getID());
            for (Question question : questions) {
                List<Option> options = questionRepository.getOptionsOfQuestion(question.getID());
                question.setOptionList(options);
                Image image=questionRepository.getImageOfQuestion((question.getImageID()));
                question.setImage(image);
            }
            test.setQuestions(questions);
            test.setAnswers(testRepository.getAllAnswersOfTest(test.getID()));
        });

        return new RestResponse("success", tests,null);
    }

}
