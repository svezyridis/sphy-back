package sphy.subject.db;

import sphy.subject.models.*;

import java.util.List;

public interface SubjectRepository {
    List<Category> getCategoriesOfWeapon(Integer weaponID);
    Integer getWeaponID(String weapon);
    Image getRandomImageOfCategory(Integer categoryID);
    Integer getCategoryID(String category,Integer weaponID);
    List<Image> getImagesOfSubject(Integer subjectID);
    List<Subject> getSubjectsOfCategory(Integer categoryID);
    List<Question> getQuestionsOfSubject(Integer subjectID);
    Integer getSubjectID(String subject);
    List<Option> getOptionsOfQuestion(Integer questionID);
}
