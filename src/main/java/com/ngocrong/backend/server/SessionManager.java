package com.ngocrong.backend.server;

import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.user.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SessionManager {
    private static final Logger logger = Logger.getLogger(SessionManager.class);
    private static final Map<Integer, Session> sessions = new ConcurrentHashMap<>(); // Dùng ConcurrentHashMap để thay thế ArrayList
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void addSession(Session session) {
        sessions.put(session.id ,session);
    }

    public static void removeSession(Session session) {
        sessions.remove(session.id);
    }

    public static List<User> findUser(String name) {
        List<User> userList = new ArrayList<>();
        sessions.values().forEach(session -> {
            if (isValidSession(session) && session.user.getUsername().equalsIgnoreCase(name)) {
                userList.add(session.user);
            }
        });
        return userList;
    }

    public static boolean deviceInvalid(String device) {
        long count = sessions.values().stream()
                .filter(session -> isValidSession(session) && device.equals(session.deviceInfo))
                .count();
        return count > Server.COUNT_SESSION_ON_IP;
    }

    // Tìm user theo ID
    public static List<User> findUserById(int id) {
        List<User> userList = new ArrayList<>();
        sessions.values().forEach(session -> {
            if (isValidSession(session) && session.user.getId() == id) {
                userList.add(session.user);
            }
        });
        return userList;
    }

//    public static void sendMessage(Message message) {
//        lock.readLock().lock();
//        try {
//            for (Session session : sessions) {
//                if (isValidSession(session) && session.getCharacter() != null) {
//                    session.sendMessage(message);
//                }
//            }
//        } catch (Exception ex) {
//            logger.error("Failed to send message", ex);
//        } finally {
//            lock.readLock().unlock();
//        }
//    }

    public static void chatVip(String text) {
        // Implement chatVip or remove method
    }

    public static void serverMessage(String text) {
        // Implement serverMessage or remove method
    }

    public static void addBigMessage(String text) {
        // Implement addBigMessage or remove method
    }

    public static void saveData() {
        // Implement saveData or remove method
    }

    public static void close() {
        // Implement close or remove method
    }

    private static boolean isValidSession(Session session) {
        return session.isEnter && session.isConnected();
    }
}
