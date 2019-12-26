package sphy.auth.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import sphy.auth.models.User;

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
            user.setUsername(rs.getString("username"));
            return user;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public int update(User user) {
        return 0;
    }

    /**
     *
     * @return returns all users
     */
    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select *from USER u inner join ROLE r on u.roleId=r.ID",
                new UserRowMapper()
        );
    }

    /**
     *
     * @param username
     * @return user mapped to User object
     */
    @Override
    public User findByUsername(String username) {
        String sql = "select * from USER u inner join ROLE r on u.roleId=r.ID where username = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{username},
                    new UserRowMapper()
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param userRole the role to look for
     * @return the id of the role
     */

    @Override
    public Integer findRoleID(String userRole) {
        String sql = "select ID from ROLE where role = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{userRole},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *
     * @param user user to add to DB
     * @return 1 if user successfully created 0 if user exists -1 for other failures
     */
    @Override
    public int createUser(User user) {
        String sql = "INSERT INTO USER (SN,lastName,firstName,username,password,roleID,rank) VALUES (?,?,?,?,?,?,?)";
        int res=0;
        try {
            res =jdbcTemplate.update(sql, user.getSerialNumber(), user.getLastName(), user.getFirstName(), user.getUsername(), user.getPassword(), user.getRoleID(), user.getRank());
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
