package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sphy.subject.models.*;
import java.util.List;

@Repository
public class JdbcSubjectRepository implements SubjectRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public Integer createSubject(Subject subject, Integer categoryID) {
        String sql = "INSERT INTO SUBJECT (categoryID, name,text) VALUES (?,?,?)";
        int res=0;
        try {
            res =jdbcTemplate.update(sql, categoryID,subject.getName(),subject.getText());
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Integer deleteSubject(Integer subjectID) {
        String sql = "DELETE FROM SUBJECT WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql,subjectID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Subject> getSubjectsOfCategory(Integer categoryID) {
        String sql = "SELECT * FROM SUBJECT WHERE categoryID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{categoryID},
                    new RowMappers.SubjectRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }



    @Override
    public List<Image> getImagesOfSubject(Integer subjectID){
        String sql = "select URL,label from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID WHERE SUBJECT.ID = ? ";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{subjectID},
                    new RowMappers.ImageRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


}
