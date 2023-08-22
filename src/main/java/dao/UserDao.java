package dao;

import model.User;

public interface UserDao {
    public int getUserIdByUsername(String username);

    public User getUserByUserId(int userId);

    public User updateUser(User user);

    public int deleteUserById(int userId);
}
