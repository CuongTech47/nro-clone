package com.ngocrong.backend.user;


import com.google.common.base.CharMatcher;
import com.ngocrong.backend.entity.UserEntity;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.repository.GameRepo;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.server.SessionManager;
import lombok.Data;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Data
public class User implements IUser{
    private static Logger logger = Logger.getLogger(User.class);
    private static final int[][] HAIR_ID = {{64, 30, 31}, {9, 29, 32}, {6, 27, 28}};
    private static final int[][] LOCATION = {{39, 100, 384}, {40, 100, 384}, {41, 100, 384}};

    private int id;
    private String username;
    private String password;
    private int status;
    private int role;
    private Session session;
    private Timestamp lockTime;
    public static boolean isAdmin;


    public User(String username, String password, Session session) {
        this.username = username.toLowerCase();
        this.password = password;
        this.session = session;
    }


    @Override
    public int login() throws SQLException {
        Server server = DragonBall.getInstance().getServer();
        if (server.isMaintained) {
            return 5;
        }
        if (!CharMatcher.javaLetterOrDigit().matchesAllOf(username)) {
            return 6;
        }
        List<UserEntity> userDataList = GameRepo.getInstance().user.findByUsernameAndPassword(username,password);
        if (userDataList.isEmpty()) {
            return 0;
        }
        UserEntity userEntity = userDataList.get(0);
        populateUserData(userEntity);
        if (isUserAlreadyLoggedIn(username)) {
            return 3;
        }
        if (status == 1) {
            return 2;
        }
        if (lockTime != null) {
            return 4;
        }
        return 1;
    }

    private void populateUserData(UserEntity userEntity) {
        setId(userEntity.getId());
        setStatus(userEntity.getStatus());
        setUsername(userEntity.getUsername());
        setRole(userEntity.getRole());
        setLockTime(userEntity.getLockTime());
        isAdmin = userEntity.getRole() == 2 ? true : false;
    }

    @Override
    public boolean isUserAlreadyLoggedIn(String username) {
        List<User> userList = SessionManager.
    }
}
