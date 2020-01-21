package sphy.image.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class JdbcImageRepository implements ImageRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(JdbcImageRepository.class);

    @Override
    public Integer addImage(String filename, Integer subjectID, String label) {
        String sql = "INSERT INTO IMAGE (filename,subjectID,label) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, filename);
                ps.setInt(2,subjectID);
                ps.setString(3,label);
                return ps;
            }, keyHolder);
            return  keyHolder.getKey().intValue();
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer deleteImage(Integer imageID) {
        String sql = "DELETE FROM IMAGE WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, imageID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        logger.info("[JdbcImageRepository]:[deleteImage]:{res: +"+res+" }");
        return res;
    }

    @Override
    public Integer getImageByFileName(String filename, Integer subjectID) {
        String sql = "select ID from IMAGE where filename = ? AND subjectID= ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{filename,subjectID},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }
}
