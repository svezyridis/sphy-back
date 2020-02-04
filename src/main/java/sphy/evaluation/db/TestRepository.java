package sphy.evaluation.db;

import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Test;
import sphy.subject.models.Question;

import java.util.List;

public interface TestRepository {
    Integer createTest(Test test);
    Integer submitAnswers(Integer studentID, List<Answer> answers);
    List<Test> getAllTestsOfClass(Integer classID);
    Integer updateTest(Test test);
    Test getTestByID(Integer testID);
    Integer addQuestionsToTest(Integer testID,List<Integer> categoryIDs,Integer noOfQuestions);
    Integer deleteTest(Integer testID);
    boolean hasSubmitted(Integer userID,Integer testID);
}
