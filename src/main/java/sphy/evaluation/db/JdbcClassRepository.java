package sphy.evaluation.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sphy.auth.db.JdbcUserRepository;
import sphy.auth.models.User;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.ClassFilterParameters;
import sphy.evaluation.models.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcClassRepository implements ClassRepository {

    public static class ClassRowMapper implements RowMapper<Classroom> {
        @Override
        public Classroom mapRow(ResultSet rs, int rowNum) throws SQLException {
            Classroom classRoom = new Classroom();
            classRoom.setID(rs.getInt("ID"));
            classRoom.setName(rs.getString("name"));
            classRoom.setCreationDate(rs.getDate("creationDate"));
            classRoom.setCreatorID(rs.getInt("creatorID"));
            classRoom.setNoOfTests(rs.getInt("noOfTests"));
            classRoom.setUnit(rs.getString("unit"));
            return classRoom;
        }
    }
    public static class PureClassRowMapper implements RowMapper<Classroom> {
        @Override
        public Classroom mapRow(ResultSet rs, int rowNum) throws SQLException {
            Classroom classRoom = new Classroom();
            classRoom.setID(rs.getInt("ID"));
            classRoom.setName(rs.getString("name"));
            classRoom.setCreationDate(rs.getDate("creationDate"));
            classRoom.setCreatorID(rs.getInt("creatorID"));
            return classRoom;
        }
    }

    private class StudentRowMapper implements RowMapper<Student> {
        @Override
        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
            Student student=new Student();
            student.setFirstName(rs.getString("firstName"));
            student.setLastName(rs.getString("lastName"));
            student.setRole(rs.getString("role"));
            student.setSerialNumber(rs.getInt("SN"));
            student.setUsername(rs.getString("username"));
            student.setID(rs.getInt("ID"));
            student.setRank(rs.getString("rank"));
            student.setUnit(rs.getString("unit"));
            student.setTimeAdded(rs.getTimestamp("addedOn"));
            return student;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer createClass(String className, Integer creatorID) {
        String sql = "INSERT INTO CLASS (NAME, CREATORID) VALUES (?,?)";
        int res = -1;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, className);
                ps.setInt(2, creatorID);
                return ps;
            }, keyHolder);
            System.out.println(keyHolder.getKey().intValue());
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Classroom> getAllClassesFiltered(ClassFilterParameters parameters) {
        return null;
    }

    @Override
    public List<Classroom> getAllClassesOfTeacher(Integer teacherID) {
        String sql = "SELECT CLASS.ID as ID,CLASS.name as name, CLASS.creationDate as creationDate, CLASS.creatorID as creatorID,U2.name as unit, COUNT(TEST.ID) as noOfTests " +
                "FROM CLASS INNER JOIN USER U on CLASS.creatorID = U.ID INNER  JOIN UNIT U2 on U.unitID = U2.ID LEFT JOIN SPHY.TEST ON CLASS.ID = TEST.classID  WHERE creatorID=? GROUP BY CLASS.ID";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{teacherID},
                    new ClassRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Classroom getClassByID(Integer classID) {
        String sql = "SELECT * FROM CLASS WHERE ID=?";
        try {
            return (Classroom) jdbcTemplate.queryForObject(sql,
                    new Object[]{classID},
                    new BeanPropertyRowMapper(Classroom.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer addStudentsToClass(Integer[] studentIDs, Integer classID) {
        int res = -1;
        String sql = "INSERT INTO CLASS_STUDENT (CLASSID, USERID) VALUES (?,?)";
        try {
            int[] rows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setInt(1, classID);
                    preparedStatement.setInt(2, studentIDs[i]);
                }

                @Override
                public int getBatchSize() {
                    return studentIDs.length;
                }
            });
            res = 0;
            for (int row : rows)
                res += row;
        } catch (DataAccessException e) {
            return res;
        }
        return res;
    }

    @Override
    public Integer removeStudentFromClass(Integer classID, Integer studentID) {
        String sql = "DELETE FROM CLASS_STUDENT WHERE classID=? AND userID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, classID, studentID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Classroom> getAllClassesOfUnit(Integer unitID) {
        String sql = "SELECT CLASS.ID as ID,CLASS.name as name, CLASS.creationDate as creationDate, CLASS.creatorID as creatorID, unitID,unitID,U2.name as unit, COUNT(TEST.ID) as noOfTests " +
                "FROM CLASS INNER JOIN USER U on CLASS.creatorID = U.ID INNER  JOIN UNIT U2 on U.unitID = U2.ID LEFT JOIN SPHY.TEST ON CLASS.ID = TEST.classID WHERE unitID=? GROUP BY CLASS.ID";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{unitID},
                    new ClassRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Classroom> getAllCLassesOfStudent(Integer studentID) {
        String sql = "SELECT * FROM  CLASS INNER JOIN CLASS_STUDENT CS on CLASS.ID = CS.classID WHERE userID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{studentID},
                    new PureClassRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Classroom> findAll() {
        String sql = "SELECT CLASS.ID as ID,CLASS.name as name, CLASS.creationDate as creationDate, CLASS.creatorID as creatorID, unitID,U2.name as unit, COUNT(TEST.ID) as noOfTests " +
                "FROM CLASS INNER JOIN USER U on CLASS.creatorID = U.ID INNER  JOIN UNIT U2 on U.unitID = U2.ID LEFT JOIN SPHY.TEST ON CLASS.ID = TEST.classID GROUP BY CLASS.ID";
        try {
            return jdbcTemplate.query(sql,
                    new ClassRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer deleteClass(Integer classID) {
        String sql = "DELETE FROM CLASS WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, classID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Integer updateClass(Integer classID,String newName) {
        String sql = "UPDATE CLASS SET name= IFNULL(?,name) WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql,newName,classID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean isStudent(Integer classID, Integer userID) {
        String sql = "SELECT * FROM CLASS_STUDENT WHERE classID=? AND  userID=?";
        try {
            jdbcTemplate.queryForObject(sql,
                    new Object[]{classID,userID},(resultSet, i) -> {return 0;});
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
    @Override
    public List<Student> getAllStudentsOfClass(Integer classID) {
        String sql = "select role,firstName,lastName,SN,username,USER.ID as ID, rank, UNIT.NAME as unit, CS.addedOn as addedOn " +
                "from USER  inner join ROLE  on USER.roleId=ROLE.ID " +
                "INNER JOIN UNIT on USER.unitID = UNIT.ID " +
                "INNER JOIN CLASS_STUDENT CS on USER.ID = CS.userID where CS.classID = ?";
        return jdbcTemplate.query(sql,
                new Object[]{classID},
                new StudentRowMapper()
        );
    }
}
