package sphy.auth;

import sphy.auth.models.User;

import java.util.List;

public interface UserRepository {
    int update(User user);
    List<User> findAll();
    User findByUsername(String username);
    Integer findRoleID(String userRole);
    int createUser(User user);
}
