package sphy.evaluation.db;

import sphy.auth.models.User;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.ClassFilterParameters;
import sphy.evaluation.models.Student;

import java.util.List;

public interface ClassRepository {
    Integer createClass(String className,Integer creatorID);
    List<Classroom> getAllClassesFiltered(ClassFilterParameters parameters);
    List<Classroom> getAllClassesOfTeacher(Integer teacherID);
    Classroom getClassByID(Integer classID);
    Integer addStudentsToClass(Integer[] studentIDs,Integer classID);
    Integer removeStudentFromClass(Integer classID,Integer studentID);
    List<Classroom> getAllClassesOfUnit(Integer unitID);
    List<Classroom> getAllCLassesOfStudent(Integer studentID);
    List<Classroom> findAll();
    Integer deleteClass(Integer classID);
    Integer updateClass(Integer classID,String newName);
    boolean isStudent(Integer classID,Integer userID);
    List<Student> getAllStudentsOfClass(Integer classID);
}
