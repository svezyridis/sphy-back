package sphy.subject.db;

import sphy.subject.models.Image;
import sphy.subject.models.Option;
import sphy.subject.models.Question;

import java.util.List;

public interface QuestionRepository {
    List<Option> getOptionsOfQuestion(Integer questionID);
    Image getImageOfQuestion(Integer imageID);
    Integer createQuestions(Integer subjectID,List<Question> questions);
    Integer deleteQuestion(Integer questionID);
    boolean checkIfExists(Integer questionID);
    List<Question> getQuestionsOfCategories(List<Integer> categoryIDs);
    List<Question> getQuestionsOfSubject(Integer subjectID);
}
