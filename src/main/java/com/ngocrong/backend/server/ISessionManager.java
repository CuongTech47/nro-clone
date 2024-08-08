package com.ngocrong.backend.server;

import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.user.User;

import java.util.List;

public interface ISessionManager {
    void addSession(Session session);
    void removeSession(Session session);
    List<User> findUser(String name);
    boolean deviceInvalid(String device);
    List<User> findUserById(int id);
//    Char findChar(int id);
    void sendMessage(Message ms);
    void chatVip(String text);
    void serverMessage(String text);
    void addBigMessage(String text);
    void saveData();
    void close();
}
