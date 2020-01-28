package sphy.auth.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import sphy.auth.models.Role;
import sphy.auth.models.Unit;
import sphy.auth.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class JdbcUserRepository implements UserRepository {

    Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);

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
            user.setID(rs.getInt("ID"));
            user.setRank(rs.getString("rank"));
            user.setUnit(rs.getString("unit"));
            user.setRoleID(rs.getInt("roleID"));
            user.setUnitID(rs.getInt("unitID"));
            return user;
        }
    }

    private class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setID(rs.getInt("ID"));
            role.setRole(rs.getString("role"));
            return role;
        }
    }
    private class UnitRowMapper implements RowMapper<Unit> {
        @Override
        public Unit mapRow(ResultSet rs, int rowNum) throws SQLException {
            Unit unit=new Unit();
            unit.setID(rs.getInt("ID"));
            unit.setName(rs.getString("name"));
            return unit;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Integer updateUser(User user) {
        return 0;
    }

    /**
     * @return returns all users
     */
    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select role,firstName,lastName,SN,username,password,USER.ID as ID, rank, UNIT.NAME as unit, unitID, roleID " +
                        "from USER  inner join ROLE  on USER.roleId=ROLE.ID " +
                        "INNER JOIN UNIT on USER.unitID = UNIT.ID",
                new UserRowMapper()
        );
    }

    /**
     * @param username
     * @return user mapped to User object
     */
    @Override
    public User findByUsername(String username) {
        String sql = "select role,firstName,lastName,SN,username,password,USER.ID as ID, rank, UNIT.NAME as unit, unitID, roleID " +
                "from USER  inner join ROLE  on USER.roleId=ROLE.ID " +
                "INNER JOIN UNIT on USER.unitID = UNIT.ID  where username = ?";
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

    @Override
    public Role findRole(Integer roleID) {
        String sql = "SELECT * FROM ROLE WHERE ID=?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{roleID},
                    new RoleRowMapper()
            );
        }
        catch (DataAccessException e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param user user to add to DB
     * @return 1 if user successfully created 0 if user exists -1 for other failures
     */
    @Override
    public Integer createUser(User user) {
        String sql = "INSERT INTO USER (SN,lastName,firstName,username,password,roleID,rank,unitID) VALUES (?,?,?,?,?,?,?,?)";
        int res = 0;
        try {
            res = jdbcTemplate.update(sql, user.getSerialNumber(), user.getLastName(), user.getFirstName(), user.getUsername(), user.getPassword(), user.getRoleID(), user.getRank(),user.getUnitID());
        } catch (DataAccessException e) {
            if (e.getRootCause().getMessage().startsWith("Duplicate entry")) {
                return -1;
            } else return 0;
        }
        logger.info("[JdbcUserRepository]:[createUser]:{res : " + res + " }");
        return res;
    }


    @Override
    public Integer deleteUser(Integer userID) {
        String sql = "DELETE FROM USER WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, userID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        logger.info("[JdbcUserRepository]:[deleteUser]:{res : " + res + " }");
        return res;
    }

    @Override
    public List<Role> getRoles() {
        String sql = "SELECT * FROM ROLE";
        try {
            return jdbcTemplate.query(sql,
                    new RoleRowMapper()
            );
        }
        catch (DataAccessException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Unit> getUnits() {
        String sql = "SELECT * FROM UNIT";
        try {
            return jdbcTemplate.query(sql,
                    new UnitRowMapper()
            );
        }
        catch (DataAccessException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> getAllUsersOfClass(Integer classID) {
        String sql = "select role,firstName,lastName,SN,username,password,USER.ID as ID, rank, UNIT.NAME as unit, unitID, roleID " +
                "from USER  inner join ROLE  on USER.roleId=ROLE.ID " +
                "INNER JOIN UNIT on USER.unitID = UNIT.ID " +
                "INNER JOIN CLASS_STUDENT CS on USER.ID = CS.userID where CS.classID = ?";
        return jdbcTemplate.query(sql,
                new Object[]{classID},
                new UserRowMapper()
        );
    }

    @Override
    public Integer getUnitID(Integer userID) {
        String sql = "SELECT unitID FROM USER WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userID}, (rs, rowNum) ->
                    rs.getInt("unitID")
            );
        }
        catch (DataAccessException e){
            return null;
        }
    }

    @Override
    public List<User> findAllUsersOfUnit(Integer unitID) {
        return jdbcTemplate.query("select role,firstName,lastName,SN,username,password,USER.ID as ID, rank, UNIT.NAME as unit, unitID, roleID " +
                        "from USER  inner join ROLE  on USER.roleId=ROLE.ID " +
                        "INNER JOIN UNIT on USER.unitID = UNIT.ID where unitID=?",
                new Object[]{unitID},
                new UserRowMapper()
        );
    }
}



