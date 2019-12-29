package sphy.image.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcImageRepository implements ImageRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer addImage(String filename, Integer subjectID, String label) {
        String sql = "INSERT INTO IMAGE (filename,subjectID,label) VALUES (?,?,?)";
        Integer res = 0;
        try {
            res = jdbcTemplate.update(sql, filename, subjectID, label);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
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
