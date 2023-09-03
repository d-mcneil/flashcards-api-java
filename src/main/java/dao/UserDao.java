package dao;

import model.User;

public interface UserDao {
    int getUserIdByUsername(String username);

    User getUserByUserId(int userId);

    User updateUser(User user);

    int deleteUserById(int userId);

    User createUser(User user, String hashedPassword);

    String getHashedPasswordByUsername(String username);
}
