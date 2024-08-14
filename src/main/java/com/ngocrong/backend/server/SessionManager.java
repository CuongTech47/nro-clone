package com.ngocrong.backend.server;

import com.ngocrong.backend.character.Char;
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
    public static ArrayList<Session> sessions = new ArrayList<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void addSession(Session session) {
        lock.writeLock().lock();
        try {
            sessions.add(session);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void removeSession(Session session) {
        lock.writeLock().lock();
        try {
            sessions.remove(session);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static List<User> findUser(String name) {
        List<User> userList = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss.user != null && ss.user.getUsername().toLowerCase().equals(name.toLowerCase())) {
                    userList.add(ss.user);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return userList;
    }

    public static boolean deviceInvalid(String device) {
        lock.readLock().lock();
        try {
            int num = 0;
            for (Session ss : sessions) {
                if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss.deviceInfo != null && ss.deviceInfo.equals(device)) {
                    num++;
                }
            }
            return num > Server.COUNT_SESSION_ON_IP;
        } finally {
            lock.readLock().unlock();
        }
    }

    // Tìm user theo ID
    public static List<User> findUserById(int id) {
        List<User> userList = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss.user != null && ss.user.getId() == id) {
                    userList.add(ss.user);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return userList;
    }

    public static void sendMessage(Message message) {
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                try {
                    if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null) {
                        ss.sendMessage(message);
                    }
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void chatVip(String text) {
        // Implement chatVip or remove method
        text = "    " + text;
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                try {
                    if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null) {
                        ss._char.service.chatVip(text);
                    }
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void serverMessage(String text) {
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                try {
                    if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null) {
                        ss._char.service.serverMessage(text);
                    }
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void addBigMessage(String text) {
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                try {
                    if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null) {
                        ss._char.service.addBigMessage(ss._char.getPetAvatar(), text, (byte) 0, null, null);
                    }
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }

            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void saveData() {
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                try {
                    if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null) {
                        ss._char.saveData();
                    }
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void close() {
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                try {
                    if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null) {
                        ss.close();
                    }
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }


    public static Char findChar(int playerID) {
        lock.readLock().lock();
        try {
            for (Session ss : sessions) {
                if (ss.isEnter && ss.getSocket() != null && !ss.getSocket().isClosed() && ss._char != null && ss._char.getId() == playerID) {
                    return ss._char;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }
}
