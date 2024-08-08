package com.ngocrong.backend.user;

import java.sql.SQLException;

public interface IUser {
    int login() throws SQLException;
    boolean isUserAlreadyLoggedIn(String username);
}
