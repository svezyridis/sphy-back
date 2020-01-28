package sphy.evaluation.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sphy.RestResponse;
import sphy.Validator;
import sphy.auth.db.UserRepository;
import sphy.auth.models.User;
import sphy.evaluation.db.ClassRepository;
import sphy.evaluation.models.Classroom;
import sphy.subject.controllers.CategoryController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClassesController {
    Logger logger = LoggerFactory.getLogger(ClassesController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcClassRepository")
    private ClassRepository classRepository;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    @Autowired
    Validator validator;

    @PostMapping(value = "class/{className}")
    public RestResponse createClass(@PathVariable String className, @CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)))
            return new RestResponse("error", null,"invalid token");
        Integer teacherID=validator.getUserID(token);
        if(teacherID==null)
            return new RestResponse("error", null,"teacher id not found in token");
        Integer result=classRepository.createClass(className,teacherID);
        Classroom classroom=new Classroom();
        if(result==-1)
            return new RestResponse("error", null,"class could not be created");
        else {
            classroom.setName(className);
            classroom.setID(result);
            classroom.setStudents(new ArrayList<>());
            classroom.setTests(new ArrayList<>());
        }
        return  new RestResponse("success",classroom,"class successfully created");
    }

    @PostMapping(value = "class/{classID}/user")
    public RestResponse addUserToClass(@PathVariable Integer classID,@RequestBody Integer [] userIDs, @CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)))
            return new RestResponse("error", null,"invalid token");
        Integer userID=validator.getUserID(token);
        if(userID==null)
            return new RestResponse("error", null,"id not found in token");
        Integer result=classRepository.addStudentsToClass(userIDs,classID);
        if(result==-1)
            return new RestResponse("error", null,"users could not be added");
        if(result!=userIDs.length)
            return new RestResponse("error", null,"some users could not be added");
        return new RestResponse("success", null,"users successfully added");
    }

    @GetMapping(value = "class")
    //TODO show different classes for teacher/unit admin/admin
    public RestResponse getAllClassesOfTeacher(@CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateTeacherToken(token)))
            return new RestResponse("error", null,"invalid token");
        Integer teacherID=validator.getUserID(token);
        if(teacherID==null)
            return new RestResponse("error", null,"teacher id not found in token");
        List<Classroom> classrooms = classRepository.getAllClassesOfTeacher(teacherID);
        classrooms.forEach(classroom -> {
            classroom.setStudents(userRepository.getAllUsersOfClass(classroom.getID()));
        });
        return  new RestResponse("success",classrooms,null);
    }


}
