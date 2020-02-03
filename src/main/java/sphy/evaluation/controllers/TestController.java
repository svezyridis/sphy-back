package sphy.evaluation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sphy.Constants;
import sphy.RestResponse;
import sphy.Validator;
import sphy.auth.db.UserRepository;
import sphy.evaluation.db.ClassRepository;
import sphy.evaluation.db.TestRepository;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.NewTest;
import sphy.evaluation.models.Test;
import sphy.subject.db.QuestionRepository;


import java.util.List;

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcClassRepository")
    private ClassRepository classRepository;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("jdbcTestRepository")
    private TestRepository testRepository;

    @Autowired
    Validator validator;

    @PostMapping("tests")
    public RestResponse createTest(@RequestBody NewTest newTest, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "user id not found in token");
        String role = validator.getUserRole(token);
        Test test = newTest.getTest();
        System.out.println(newTest);
        Integer classroomID = test.getClassID();
        Classroom classroom = classRepository.getClassByID(classroomID);
        if (classroom == null)
            return new RestResponse("error", null, "classroom not found");
        if (role.equals(Constants.TEACHER) && (!classroom.getCreatorID().equals(userID))) {
            return new RestResponse("error", null, "you are not the creator of this class");
        }
        if (role.equals(Constants.UNIT_ADMIN)) {
            Integer classCreatorUnitID = userRepository.getUnitID(classroom.getCreatorID());
            Integer requesterUnit = userRepository.getUnitID(userID);
            if (!classCreatorUnitID.equals(requesterUnit))
                return new RestResponse("error", null, "this class does not belong to your unit");
        }
        Integer result = testRepository.createTest(test);
        if (result == -1)
            return new RestResponse("error", null, "test could not be created");
        test.setID(result);
        System.out.println(test.getID());
        result = testRepository.addQuestionsToTest(test.getID(), newTest.getCategoryIDs(), newTest.getNoOfQuestions());
        if (result == -1)
            return new RestResponse("error", null, "questions could not be added");
        return new RestResponse("success", test, "test created successfully");
    }

    @RequestMapping("tests")
    public RestResponse getAllTestsOfClass(@RequestParam(value = "classID") Integer classroomID, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!validator.simpleValidateToken(token))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "user id not found in token");
        String role = validator.getUserRole(token);
        Classroom classroom = classRepository.getClassByID(classroomID);
        if (classroom == null)
            return new RestResponse("error", null, "classroom not found");
        if (role.equals(Constants.TEACHER) && (!classroom.getCreatorID().equals(userID))) {
            return new RestResponse("error", null, "you are not the creator of this class");
        }
        if (role.equals(Constants.UNIT_ADMIN)) {
            Integer classCreatorUnitID = userRepository.getUnitID(classroom.getCreatorID());
            Integer requesterUnit = userRepository.getUnitID(userID);
            if (!classCreatorUnitID.equals(requesterUnit))
                return new RestResponse("error", null, "this class does not belong to your unit");
        }
        if (role.equals(Constants.USER) && !classRepository.isStudent(classroomID, userID)) {
            System.out.println(classroomID+" "+userID);
            return new RestResponse("error", null, "you are not a student of this class");
        }
        long startTIme = System.nanoTime();
        List<Test> tests = testRepository.getAllTestsOfClass(classroomID);
        if (tests == null)
            return new RestResponse("error", null, "tests could not be fetched");
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTIme;
        System.out.println("execution time to get tests in ms" + elapsedTime / 1000000);
        return new RestResponse("success", tests, null);
    }

    @PutMapping("tests/{testID}")
    public RestResponse updateTest(@PathVariable Integer testID, @RequestBody Test test, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "user id not found in token");
        String role = validator.getUserRole(token);
        Test oldTest = testRepository.getTestByID(testID);
        if (oldTest == null)
            return new RestResponse("error", null, "test not found");
        Integer classroomID = oldTest.getClassID();
        Classroom classroom = classRepository.getClassByID(classroomID);
        if (role.equals(Constants.TEACHER) && (!classroom.getCreatorID().equals(userID))) {
            return new RestResponse("error", null, "you can only edit tests of your class");
        }
        if (role.equals(Constants.UNIT_ADMIN)) {
            Integer classCreatorUnitID = userRepository.getUnitID(classroom.getCreatorID());
            Integer userUnit = userRepository.getUnitID(userID);
            if (!classCreatorUnitID.equals(userUnit))
                return new RestResponse("error", null, "this class does not belong to your unit");
        }
        test.setID(testID);
        System.out.println(test);
        Integer result = testRepository.updateTest(test);
        if (result == -1)
            return new RestResponse("error", null, "test could not be updated");
        return new RestResponse("success", null, "test updated successfully");
    }

    @DeleteMapping("tests/{testID}")
    public RestResponse deleteTest(@PathVariable Integer testID, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "user id not found in token");
        String role = validator.getUserRole(token);
        Test oldTest = testRepository.getTestByID(testID);
        if (oldTest == null)
            return new RestResponse("error", null, "test not found");
        Integer classroomID = oldTest.getClassID();
        Classroom classroom = classRepository.getClassByID(classroomID);
        if (role.equals(Constants.TEACHER) && (!classroom.getCreatorID().equals(userID))) {
            return new RestResponse("error", null, "you can only delete tests of your class");
        }
        if (role.equals(Constants.UNIT_ADMIN)) {
            Integer classCreatorUnitID = userRepository.getUnitID(classroom.getCreatorID());
            Integer userUnit = userRepository.getUnitID(userID);
            if (!classCreatorUnitID.equals(userUnit))
                return new RestResponse("error", null, "this class does not belong to your unit");
        }
        Integer result = testRepository.deleteTest(testID);
        if (result == -1)
            return new RestResponse("error", null, "test could not be deleted");
        return new RestResponse("success", null, "test deleted successfully");
    }

}
