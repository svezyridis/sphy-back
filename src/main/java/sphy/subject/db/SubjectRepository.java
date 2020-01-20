package sphy.subject.db;

import sphy.subject.models.*;

import java.util.List;

public interface SubjectRepository {
    List<Image> getImagesOfSubject(Integer subjectID);
    List<Subject> getSubjectsOfCategory(Integer categoryID);
    Integer getSubjectID(String subject);
    Integer createSubject(Subject subject, Integer categoryID);
    Integer deleteSubject(Integer subjectID);
    Subject getSubjectByURI(String URI);
    Integer updateSubject(Subject subject);
}
