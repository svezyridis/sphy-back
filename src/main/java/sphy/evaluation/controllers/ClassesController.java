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
import sphy.evaluation.models.Classroom;

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


    @PostMapping(value = "classes/{className}")
    public RestResponse createClass(@PathVariable String className, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer teacherID = validator.getUserID(token);
        if (teacherID == null)
            return new RestResponse("error", null, "teacher id not found in token");
        Integer result = classRepository.createClass(className, teacherID);
        Classroom classroom = new Classroom();
        if (result == -1)
            return new RestResponse("error", null, "class could not be created");
        else {
            classroom.setName(className);
            classroom.setID(result);
            classroom.setStudents(new ArrayList<>());
            classroom.setTests(new ArrayList<>());
        }
        return new RestResponse("success", classroom, "class successfully created");
    }

    @PostMapping(value = "classes/{classID}/students")
    public RestResponse addUserToClass(@PathVariable Integer classID, @RequestBody Integer[] userIDs, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "id not found in token");
        Integer result = classRepository.addStudentsToClass(userIDs, classID);
        if (result == -1)
            return new RestResponse("error", null, "users could not be added");
        if (result != userIDs.length)
            return new RestResponse("error", null, "some users could not be added");
        return new RestResponse("success", null, "users successfully added");
    }

    @DeleteMapping(value = "classes/{classID}/students/{studentID}")
    public RestResponse removeUserFromClass(@PathVariable Integer classID, @CookieValue(value = "jwt", defaultValue = "token") String token,@PathVariable Integer studentID) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "id not found in token");
        String userRole = validator.getUserRole(token);
        Classroom classroom = classRepository.getClassByID(classID);
        if (classroom == null)
            return new RestResponse("error", null, "class not found");
        switch (userRole) {
            case Constants.TEACHER:
                if (classroom.getCreatorID() != userID)
                    return new RestResponse("error", null, "you can only edit your classes");
            case Constants.UNIT_ADMIN:
                if (userRepository.getUnitID(classroom.getCreatorID()) != userRepository.getUnitID(userID))
                    return new RestResponse("error", null, "you can only edit classes of your unit");
        }
        Integer result = classRepository.removeStudentFromClass(classID, studentID);
        if (result == -1)
            return new RestResponse("error", null, "student could not be deleted");
        return new RestResponse("success", null, "student deleted successfully");
    }

    @DeleteMapping(value = "classes/{classID}")
    public RestResponse deleteClass(@PathVariable Integer classID, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "id not found in token");
        String userRole = validator.getUserRole(token);
        Classroom classroom = classRepository.getClassByID(classID);
        if (classroom == null)
            return new RestResponse("error", null, "class not found");
        switch (userRole) {
            case Constants.TEACHER:
                if (classroom.getCreatorID() != userID)
                    return new RestResponse("error", null, "you can only delete your classes");
            case Constants.UNIT_ADMIN:
                if (userRepository.getUnitID(classroom.getCreatorID()) != userRepository.getUnitID(userID))
                    return new RestResponse("error", null, "you can only delete classes of your unit");
        }
        Integer result = classRepository.deleteClass(classID);
        if (result == -1)
            return new RestResponse("error", null, "class could not be deleted");
        return new RestResponse("success", null, "class deleted successfully");
    }


    @GetMapping(value = "classes")
    public RestResponse getAllClassesOf(@CookieValue(value = "jwt", defaultValue = "token") String token) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "teacher id not found in token");
        String userRole = validator.getUserRole(token);
        List<Classroom> classrooms = null;
        switch (userRole) {
            case Constants.TEACHER:
                classrooms = classRepository.getAllClassesOfTeacher(userID);
                if (classrooms == null)
                    return new RestResponse("error", null, "classrooms could not be fetched");
                classrooms.forEach(classroom -> {
                    classroom.setStudents(userRepository.getAllUsersOfClass(classroom.getID()));
                });
                return new RestResponse("success", classrooms, null);
            case Constants.UNIT_ADMIN:
                classrooms = classRepository.getAllClassesOfUnit(userRepository.getUnitID(userID));
                if (classrooms == null)
                    return new RestResponse("error", null, "classrooms could not be fetched");
                classrooms.forEach(classroom -> {
                    classroom.setStudents(userRepository.getAllUsersOfClass(classroom.getID()));
                });
                return new RestResponse("success", classrooms, null);
            case Constants.ADMIN:
                classrooms = classRepository.findAll();
                if (classrooms == null)
                    return new RestResponse("error", null, "classrooms could not be fetched");
                classrooms.forEach(classroom -> {
                    classroom.setStudents(userRepository.getAllUsersOfClass(classroom.getID()));
                });
                return new RestResponse("success", classrooms, null);
        }
        return new RestResponse("error", null, "you don't have the rights to see this info");
    }

    @PutMapping(value = "classes/{classID}")
    public RestResponse deleteClass(@PathVariable Integer classID, @CookieValue(value = "jwt", defaultValue = "token") String token,@RequestBody String newName) {
        if (!(validator.validateAdminToken(token) || validator.validateTeacherToken(token) || validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        Integer userID = validator.getUserID(token);
        if (userID == null)
            return new RestResponse("error", null, "id not found in token");
        String userRole = validator.getUserRole(token);
        Classroom classroom = classRepository.getClassByID(classID);
        if (classroom == null)
            return new RestResponse("error", null, "class not found");
        switch (userRole) {
            case Constants.TEACHER:
                if (classroom.getCreatorID() != userID)
                    return new RestResponse("error", null, "you can only edit your classes");
            case Constants.UNIT_ADMIN:
                if (userRepository.getUnitID(classroom.getCreatorID()) != userRepository.getUnitID(userID))
                    return new RestResponse("error", null, "you can only edit classes of your unit");
        }
        Integer result = classRepository.updateClass(classID,newName);
        if (result == -1)
            return new RestResponse("error", null, "class could not be updated");
        return new RestResponse("success", null, "class updated successfully");
    }

}
