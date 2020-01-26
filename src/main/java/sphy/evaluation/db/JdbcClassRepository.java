package sphy.evaluation.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
@Repository
public class JdbcClassRepository implements ClassRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer createClass(String className, Integer creatorID) {
        String sql = "INSERT INTO CLASS (NAME, CREATORID) VALUES (?,?)";
        int res=0;
        try {
            res =jdbcTemplate.update(sql, className,creatorID);
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }
        return res;
    }
}
