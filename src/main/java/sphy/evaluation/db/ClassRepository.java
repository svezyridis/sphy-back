package sphy.evaluation.db;

import sphy.auth.models.User;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.ClassFilterParameters;

import java.util.List;

public interface ClassRepository {
    Integer createClass(String className,Integer creatorID);
    List<Classroom> getAllClassesFiltered(ClassFilterParameters parameters);
    List<Classroom> getAllClassesOfTeacher(Integer teacherID);
    Classroom getClassByID(Integer classID);
    Integer addStudentsToClass(Integer[] studentIDs);
}
