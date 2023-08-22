package dao;

import model.Login;

public interface LoginDao {
    public Login getLoginByUserId(int userId);
}
