package sphy.auth.db;

import sphy.auth.models.User;

import java.util.List;

public interface UserRepository {
    Integer updateUser(User user);
    List<User> findAll();
    User findByUsername(String username);
    Integer findRoleID(String userRole);
    Integer createUser(User user);
    Integer deleteUser(Integer userID);
}
