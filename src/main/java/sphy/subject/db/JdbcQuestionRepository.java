package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sphy.subject.models.Image;
import sphy.subject.models.Option;
import sphy.subject.models.Question;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
@Repository
public class JdbcQuestionRepository implements QuestionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Option> getOptionsOfQuestion(Integer questionID) {
        String sql = "SELECT * FROM OPTIONS WHERE questionID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{questionID},
                    new RowMappers.OptionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Image getImageOfQuestion(Integer imageID) {
        String sql = "select filename,label,IMAGE.ID as ID, SUBJECT.name as subject from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID WHERE IMAGE.ID = ? ";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{imageID},
                    new RowMappers.ImageRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer createQuestions(Integer subjectID, List<Question> questions) {
        String sql = "INSERT INTO QUESTION (text, subjectid, answerreference) VALUES (?,?,ifnull(?,''))";
        int res=0;
        for(Question question:questions){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            try {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1,question.getText());
                    ps.setInt(2,subjectID);
                    ps.setString(3,question.getAnswerReference());
                    return ps;
                }, keyHolder);
                int questionID=keyHolder.getKey().intValue();
                String sql2 = "INSERT INTO OPTIONS (text, correct, questionID) VALUES (?,?,?)";
                for(Option option:question.getOptionList()){
                    try {
                        jdbcTemplate.update(sql2,option.getText(),option.isCorrect(),questionID);
                    } catch (DataAccessException e) {
                        res=-1;
                        e.printStackTrace();
                    }
                }
            }
            catch (DataAccessException e) {
                res = -1;
                e.printStackTrace();
            }
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

    @Override
    public List<Question> getQuestionsOfCategories(List<Integer> categoryIDs) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("IDs", categoryIDs);
        Integer res = -1;
        String sql = "SELECT * FROM COMPLETE_QUESTION WHERE categoryID IN (:IDs) ORDER BY ID";
        try {
            return namedParameterJdbcTemplate.query(sql, parameters,resultSet -> {
                List<Question> result=new ArrayList<>();
                Question currentQuestion =null;
                List<Option> options=new ArrayList<>();
                Image currentImage=null;
                while (resultSet.next()){
                    Integer questionID=resultSet.getInt("ID");
                    System.out.println(questionID);
                    if(currentQuestion==null) {
                        currentQuestion = mapQuestion(resultSet);
                    }else  if(currentQuestion.getID()!=questionID){
                        currentQuestion.setOptionList(options);
                        currentQuestion.setImage(currentImage);
                        result.add(currentQuestion);
                        currentQuestion=mapQuestion(resultSet);
                        options=new ArrayList<>();
                    }
                    options.add(mapOption(resultSet));
                    currentImage=mapImage(resultSet);
                }
                if(currentQuestion!=null){
                    currentQuestion.setOptionList(options);
                    currentQuestion.setImage(currentImage);
                    result.add(currentQuestion);
                }
                return result;
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    private Question mapQuestion(ResultSet resultSet){
        Question question=new Question();
        try{
            question.setID(resultSet.getInt("ID"));
            question.setAnswerReference(resultSet.getString("answerReference"));
            question.setText(resultSet.getString("text"));
            question.setImageID(resultSet.getInt("imageID"));
            question.setSubject(resultSet.getString("subject"));
            question.setBranch(resultSet.getString("branch"));
            question.setCategory(resultSet.getString("category"));
        }
        catch (SQLException e){
            return null;
        }
        return question;
    }
    private Option mapOption(ResultSet resultSet) {
        Option option = new Option();
        try {
            option.setCorrect(resultSet.getBoolean("correct"));
            option.setID(resultSet.getInt("optionID"));
            option.setText(resultSet.getString("optionText"));
            option.setQuestionID(resultSet.getInt("ID"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return option;
    }

    private Image mapImage(ResultSet resultSet){
        Image image = new Image();
        try {
            image.setLabel(resultSet.getString("label"));
            image.setFilename((resultSet.getString("filename")));
            image.setSubject(resultSet.getString("imageSubject"));
            image.setID(resultSet.getInt("imageID"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }
}
