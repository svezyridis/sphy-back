package sphy.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class JdbcUserRepository implements UserRepository {

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setRole(rs.getString("role"));
            user.setPassword(rs.getString("password"));
            user.setSerialNumber(rs.getInt("SN"));
            return user;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public int update(User user) {
        return 0;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select *from USERS u inner join ROLES r on u.roleId=r.ID",
                new UserRowMapper()
        );
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select *from USERS u inner join ROLES r on u.roleId=r.ID where username = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{username},
                    new UserRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public Integer findRoleID(String userRole) {
        String sql = "select * from ROLES where role = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{userRole},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int createUser(User user) {
        String sql = "INSERT INTO USERS (SN,lastName,firstName,username,password,roleID) VALUES (?,?,?,?,?,?)";
        int res=0;
        try {
            res =jdbcTemplate.update(sql, user.getSerialNumber(), user.getLastName(), user.getFirstName(), user.getUsername(), user.getPassword(), user.getRoleID());
        }
        catch (DataAccessException e){
            if(e.getRootCause().getMessage().startsWith("Duplicate entry")){
                return -1;
            }
            else return 0;
        }
        return res;
    }



}
