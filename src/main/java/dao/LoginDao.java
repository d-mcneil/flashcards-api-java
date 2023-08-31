package dao;

import model.Login;

public interface LoginDao {
    Login getLoginByUserId(int userId);
}
