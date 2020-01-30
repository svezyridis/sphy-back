package sphy.evaluation.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.Test;
import sphy.subject.db.RowMappers;
import sphy.subject.models.Question;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcTestRepository implements TestRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static class TestRowMapper implements RowMapper<Test> {
        @Override
        public Test mapRow(ResultSet rs, int rowNum) throws SQLException {
            Test test = new Test();
            test.setID(rs.getInt("ID"));
            test.setClassID(rs.getInt("classID"));
            test.setName(rs.getString("name"));
            test.setDuration(rs.getInt("duration"));
            test.setCreationDate(rs.getDate("creationDate"));
            test.setActivationTime(rs.getTimestamp("activationTime"));
            test.setCompletionTime(rs.getTimestamp("completionTime"));
            return test;
        }
    }

    public static class AnswerRowMapper implements RowMapper<Answer> {
        @Override
        public Answer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Answer answer = new Answer();
            answer.setChoiceID(rs.getInt("choiceID"));
            answer.setQuestionID(rs.getInt("questionID"));
            answer.setUserID(rs.getInt("userID"));
            answer.setID(rs.getInt("ID"));
            return answer;
        }
    }

    @Override
    public Integer createTest(Test test) {
        String sql = "INSERT INTO TEST (classID, name, duration) VALUES (?,?,?)";
        Integer result=-1;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, test.getClassID());
                ps.setString(2,test.getName());
                ps.setInt(3,test.getDuration());
                return ps;
            }, keyHolder);
            return  keyHolder.getKey().intValue();
        }
        catch (DataAccessException e){
            e.printStackTrace();
            return result;
        }
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

    @Override
    public Integer updateTest(Test test) {
        String sql = "UPDATE  TEST SET activationTime=ifnull(?,activationTime),completionTime=ifnull(?,completionTime) WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql,test.getActivationTime(),test.getCompletionTime(),test.getID());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Test getTestByID(Integer testID) {
        String sql = "SELECT * FROM TEST WHERE ID=?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{testID},
                    new TestRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
