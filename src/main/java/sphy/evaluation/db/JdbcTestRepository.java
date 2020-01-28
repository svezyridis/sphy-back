package sphy.evaluation.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.Test;
import sphy.subject.db.RowMappers;
import sphy.subject.models.Question;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcTestRepository implements TestRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static class TestRowMapper implements RowMapper<Test> {
        @Override
        public Test mapRow(ResultSet rs, int rowNum) throws SQLException {
            Test test=new Test();

            return test;
        }
    }

    public static class AnswerRowMapper implements RowMapper<Answer> {
        @Override
        public Answer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Answer answer=new Answer();
            answer.setChoiceID(rs.getInt("choiceID"));
            answer.setQuestionID(rs.getInt("questionID"));
            answer.setUserID(rs.getInt("userID"));
            answer.setID(rs.getInt("ID"));
            return answer;
        }
    }

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
        String sql = "SELECT * FROM TEST WHERE classID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{classID},
                    new TestRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<Question> getAllQuestionsOfTest(Integer testID) {
        String sql = "SELECT * FROM TEST_QUESTION inner join SPHY.QUESTION ON TEST_QUESTION.questionID = QUESTION.ID WHERE TEST_QUESTION.testID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{testID},
                    new RowMappers.QuestionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Answer> getAllAnswersOfTest(Integer testID) {
        String sql = "SELECT * FROM TEST_ANSWER INNER JOIN TEST_QUESTION TQ on TEST_ANSWER.questionID = TQ.ID WHERE testID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{testID},
                    new AnswerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
