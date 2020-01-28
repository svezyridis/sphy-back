package sphy.auth.db;

import sphy.auth.models.Role;
import sphy.auth.models.Unit;
import sphy.auth.models.User;

import java.util.List;

public interface UserRepository {
    Integer updateUser(User user);
    List<User> findAll();
    User findByUsername(String username);
    Role findRole(Integer roleID);
    Integer createUser(User user);
    Integer deleteUser(Integer userID);
    List<Role> getRoles();
    List<Unit> getUnits();
    List<User> getAllUsersOfClass(Integer classID);
    Integer getUnitID(Integer userID);
    List<User> findAllUsersOfUnit(Integer unitID);
}
