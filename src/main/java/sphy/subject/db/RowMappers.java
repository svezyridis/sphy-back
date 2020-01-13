package sphy.subject.db;

import org.springframework.jdbc.core.RowMapper;
import sphy.subject.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMappers {
    public static class ImageRowMapper implements RowMapper<Image> {
        @Override
        public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
            Image image = new Image();
            image.setLabel(rs.getString("label"));
            image.setFilename((rs.getString("filename")));
            image.setSubject(rs.getString("subject"));
            image.setID(rs.getInt("ID"));
            return image;
        }
    }

    public static class SubjectRowMapper implements RowMapper<Subject> {
        @Override
        public Subject mapRow(ResultSet rs, int rowNum) throws SQLException {
            Subject subject= new Subject();
            subject.setID(rs.getInt("ID"));
            subject.setName(rs.getString("name"));
            subject.setText(rs.getString("text"));
            subject.setCategoryID(rs.getInt("categoryID"));
            subject.setURI(rs.getString("URI"));
            return subject;
        }
    }

    public static class QuestionRowMapper implements RowMapper<Question>{
        @Override
        public Question mapRow(ResultSet resultSet, int i) throws SQLException {
            Question question=new Question();
            question.setID(resultSet.getInt("ID"));
            question.setAnswerReference(resultSet.getString("answerReference"));
            question.setText(resultSet.getString("text"));
            question.setImageID(resultSet.getInt("imageID"));
            return question;
        }
    }

    public static class OptionRowMapper implements RowMapper<Option>{
        @Override
        public Option mapRow(ResultSet resultSet, int i) throws SQLException {
            Option option=new Option();
            option.setCorrect(resultSet.getBoolean("correct"));
            option.setID(resultSet.getInt("ID"));
            option.setText(resultSet.getString("text"));
            option.setQuestionID(resultSet.getInt("questionID"));
            return option;
        }
    }

    public static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setID(rs.getInt("ID"));
            category.setName(rs.getString("name"));
            category.setWeaponID(rs.getInt("weaponID"));
            category.setURI(rs.getString("URI"));
            return category;
        }
    }
}
