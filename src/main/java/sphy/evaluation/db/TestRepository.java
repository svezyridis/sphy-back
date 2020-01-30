package sphy.evaluation.db;

import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Test;
import sphy.subject.models.Question;

import java.util.List;

public interface TestRepository {
    Integer createTest(Test test);
    Integer submitAnswers(Integer testID, Integer studentID, Answer[] answers);
    List<Test> getAllTestsOfClass(Integer classID);
    List<Question> getAllQuestionsOfTest(Integer testID);
    List<Answer> getAllAnswersOfTest(Integer testID);
    Integer updateTest(Test test);
    Test getTestByID(Integer testID);
}
