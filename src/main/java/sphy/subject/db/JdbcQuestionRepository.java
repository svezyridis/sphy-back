package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sphy.subject.models.Option;
import sphy.subject.models.Question;

import java.util.List;
@Repository
public class JdbcQuestionRepository implements QuestionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Question> getQuestionsOfSubject(Integer subjectID) {
        String sql = "select * from QUESTION WHERE subjectID = ? ";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{subjectID},
                    new RowMappers.QuestionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Option> getOptionsOfQuestion(Integer questionID) {
        String sql = "SELECT * FROM CHOICE WHERE questionID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{questionID},
                    new RowMappers.OptionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer createQuestion(Integer subjectID, Question question) {
        String sql = "INSERT INTO QUESTION (text, subjectID, answerReference) VALUES (?,?,?)";
        int res=0;
        try {
            res =jdbcTemplate.update(sql, question.getText(),subjectID,question.getAnswerReference());
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Integer deleteQuestion(Integer questionID) {
        String sql = "DELETE FROM QUESTION WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql,questionID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean checkIfExists(Integer questionID) {
        String sql = "select ID from QUESTION where ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{questionID},
                    (rs, rowNum) ->
                            rs.getInt("ID"))!=null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
