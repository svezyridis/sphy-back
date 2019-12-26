package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import sphy.subject.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcSubjectRepository implements SubjectRepository {
    private class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setID(rs.getInt("ID"));
            category.setName(rs.getString("name"));
            category.setWeaponID(rs.getInt("weaponID"));
            return category;
        }
    }

    private class ImageRowMapper implements RowMapper<Image> {
        @Override
        public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
            Image image = new Image();
            image.setLabel(rs.getString("label"));
            image.setURL((rs.getString("URL")));
            return image;
        }
    }

    private class SubjectRowMapper implements RowMapper<Subject> {
        @Override
        public Subject mapRow(ResultSet rs, int rowNum) throws SQLException {
            Subject subject= new Subject();
            subject.setID(rs.getInt("ID"));
            subject.setName(rs.getString("name"));
            subject.setText(rs.getString("text"));
            subject.setCategoryID(rs.getInt("categoryID"));
            return subject;
        }
    }

    private class QuestionRowMapper implements RowMapper<Question>{
        @Override
        public Question mapRow(ResultSet resultSet, int i) throws SQLException {
            Question question=new Question();
            question.setID(resultSet.getInt("ID"));
            question.setAnswerReference(resultSet.getString("answerReference"));
            question.setSubjectID(resultSet.getInt("subjectID"));
            question.setText(resultSet.getString("text"));
            return question;
        }
    }

    private class OptionRowMapper implements RowMapper<Option>{
        @Override
        public Option mapRow(ResultSet resultSet, int i) throws SQLException {
            Option option=new Option();
            option.setCorrect(resultSet.getBoolean("correct"));
            option.setID(resultSet.getInt("ID"));
            option.setLetter(resultSet.getString("letter"));
            option.setText(resultSet.getString("text"));
            option.setQuestionID(resultSet.getInt("questionID"));
            return option;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *
     * @param weaponID
     * @return all categories of the specified weapon
     */
    @Override
    public List<Category> getCategoriesOfWeapon(Integer weaponID) {
        String sql = "SELECT * FROM CATEGORY WHERE weaponID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{weaponID},
                    new CategoryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * @param weapon the role to look for
     * @return the id of the weapon
     */

    @Override
    public Integer getWeaponID(String weapon) {
        String sql = "select ID from WEAPON where NAME = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{weapon},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }
    @Override
    public Image getRandomImageOfCategory(Integer categoryID){
        String sql = "select URL,label from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID " +
                "INNER JOIN CATEGORY ON CATEGORY.ID = SUBJECT.categoryID WHERE CATEGORY.ID = ? " +
                "ORDER BY RAND() LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{categoryID},
                    new ImageRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer getCategoryID(String category, Integer weaponID) {
        String sql = "select ID from CATEGORY where NAME = ? AND weaponID= ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{category,weaponID},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public Integer getSubjectID(String subject) {
        String sql = "select ID from SUBJECT where NAME = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{subject},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public List<Option> getOptionsOfQuestion(Integer questionID) {
        String sql = "SELECT * FROM CHOICE WHERE questionID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{questionID},
                    new OptionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Subject> getSubjectsOfCategory(Integer categoryID) {
        String sql = "SELECT * FROM SUBJECT WHERE categoryID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{categoryID},
                    new SubjectRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Question> getQuestionsOfSubject(Integer subjectID) {
        String sql = "select * from QUESTION WHERE subjectID = ? ";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{subjectID},
                    new QuestionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Image> getImagesOfSubject(Integer subjectID){
        String sql = "select URL,label from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID WHERE SUBJECT.ID = ? ";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{subjectID},
                    new ImageRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


}
