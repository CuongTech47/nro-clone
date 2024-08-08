package com.ngocrong.backend.server;

import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.user.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SessionManager implements ISessionManager{
    private static final Logger logger = Logger.getLogger(SessionManager.class);

    public static ArrayList<Session> sessions = new ArrayList<>();
    public static ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void addSession(Session session) {
        lock.writeLock().lock();
        try {
            sessions.add(session);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeSession(Session session) {
        lock.writeLock().lock();
        try {
            sessions.remove(session);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<User> findUser(String name) {
        List<User> userList = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss.user != null && ss.user.getUsername().toLowerCase().equals(name.toLowerCase())) {
                    userList.add(ss.user);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return userList;
    }

    @Override
    public boolean deviceInvalid(String device) {
        lock.readLock().lock();
        try {
            int num = 0;
            for (Session ss : sessions) {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss.deviceInfo != null && ss.deviceInfo.equals(device)) {
                    num++;
                }
            }
            return num > Server.COUNT_SESSION_ON_IP;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<User> findUserById(int id) {
        List<User> userList = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss.user != null && ss.user.getId() == id) {
                    userList.add(ss.user);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return userList;
    }

//    @Override
//    public Char findChar(int id) {
//        return null;
//    }

    @Override
    public void sendMessage(Message ms) {
//        lock.readLock().lock();
//        try {
//            for (Session ss : sessions) {
//                try {
//                    if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._char != null) {
//                        ss.sendMessage(ms);
//                    }
//                } catch (Exception ex) {
//                    logger.error("failed!", ex);
//                }
//            }
//        } finally {
//            lock.readLock().unlock();
//        }
    }

    @Override
    public void chatVip(String text) {

    }

    @Override
    public void serverMessage(String text) {

    }

    @Override
    public void addBigMessage(String text) {

    }

    @Override
    public void saveData() {

    }

    @Override
    public void close() {

    }
}
