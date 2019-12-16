package sphy.auth;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    int update(User user);
    List<User> findAll();
    User findByUsername(String username);
    Integer findRoleID(String userRole);
    int createUser(User user);
}
