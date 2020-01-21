package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sphy.subject.models.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcSubjectRepository implements SubjectRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer getSubjectID(String subject) {
        String sql = "select ID from SUBJECT where URI = ?";
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
        String sql = "INSERT INTO SUBJECT (categoryID, name,general, units,URI) VALUES (?,?,?,?,?)";
        int res=-1;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,categoryID);
                ps.setString(2,subject.getName());
                ps.setString(3,subject.getGeneral());
                ps.setString(4,subject.getUnits());
                ps.setString(5,subject.getURI());
                return ps;
            }, keyHolder);
            System.out.println(keyHolder.getKey().intValue());
            return  keyHolder.getKey().intValue();
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
            res = jdbcTemplate.update(sql, subjectID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Subject getSubjectByURI(String URI) {
        String sql = "SELECT SUBJECT.ID as ID, general, units, categoryID, name, URI, defaultImageID, IMAGE.ID as imageID, filename, subjectID, label FROM SUBJECT LEFT  JOIN SPHY.IMAGE ON IMAGE.ID = SUBJECT.defaultImageID WHERE URI=?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{URI},
                    new RowMappers.SubjectRowMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer updateSubject(Subject subject) {
        String sql = "UPDATE  SUBJECT SET defaultImageID=IFNULL(?,defaultImageID), name=ifnull(?,name)," +
                "units=ifnull(?,units),general=ifnull(?,general),URI=ifnull(?,URI)  WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, subject.getDefaultImageID(), subject.getName(), subject.getUnits(), subject.getGeneral(), subject.getURI(), subject.getID());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Subject> getSubjectsOfCategory(Integer categoryID) {
        String sql = "SELECT SUBJECT.ID as ID, general, units, categoryID, name, URI, defaultImageID, IMAGE.ID as imageID, filename, subjectID, label FROM SUBJECT LEFT  JOIN SPHY.IMAGE ON IMAGE.ID = SUBJECT.defaultImageID WHERE categoryID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{categoryID},
                    new RowMappers.SubjectRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public List<Image> getImagesOfSubject(Integer subjectID) {
        String sql = "select filename,label,IMAGE.ID as ID, SUBJECT.URI as subject from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID WHERE SUBJECT.ID = ? ";
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
