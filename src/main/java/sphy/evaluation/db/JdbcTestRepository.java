package sphy.evaluation.db;

import org.springframework.stereotype.Repository;
import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Test;

import java.util.List;

@Repository
public class JdbcTestRepository implements TestRepository {
    @Override
    public Integer createTestForClass(Test test, Integer classID) {
        return null;
    }

    @Override
    public Integer initializeTest(Integer testID) {
        return null;
    }

    @Override
    public Integer submitAnswers(Integer testID, Integer studentID, Answer[] answers) {
        return null;
    }

    @Override
    public List<Test> getAllTestsOfClass(Integer classID) {
        return null;
    }
}
