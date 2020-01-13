package sphy.subject.db;

import sphy.subject.models.Image;
import sphy.subject.models.Option;
import sphy.subject.models.Question;

import java.util.List;

public interface QuestionRepository {
    List<Question> getQuestionsOfSubject(Integer subjectID);
    List<Option> getOptionsOfQuestion(Integer questionID);
    Image getImageOfQuestion(Integer imageID);
    Integer createQuestion(Integer subjectID,Question question);
    Integer deleteQuestion(Integer questionID);
    boolean checkIfExists(Integer questionID);
}
