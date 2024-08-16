package com.ngocrong.backend.top;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.network.Message;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Getter
public abstract class Top {
    public static final byte TYPE_NONE = 1;
    public static final byte TYPE_THACH_DAU = 0;
    public static final int TOP_POWER = 0;

    private static Logger logger = Logger.getLogger(Top.class);
    private static ArrayList<Top> tops = new ArrayList<>();

    public static void initialize() {
        addTop(new TopPower(TOP_POWER, TYPE_NONE, "Sức mạnh", (byte) 100));
    }

    public static void addTop(Top top) {
        synchronized (tops) {
            tops.add(top);
        }
    }

    public static Top getTop(int id) {
        synchronized (tops) {
            for (Top top : tops) {
                if (top.id == id) {
                    return top;
                }
            }
        }
        return null;
    }

    private int id;
    private String name;
    private byte type;
    protected byte limit;
    protected ArrayList<TopInfo> elements;
    protected long lowestScore = -1;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public Top(int id, byte type, String name, byte limit) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.limit = limit;
        elements = new ArrayList<>();
        //load();
    }

    public TopInfo getTopInfo(int playerID) {
        lock.readLock().lock();
        try {
            for (TopInfo top : elements) {
                if (top.playerID == playerID) {
                    return top;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public void update() {
        lock.readLock().lock();
        try {
            elements.sort(new Comparator<TopInfo>() {
                @Override
                public int compare(TopInfo o1, TopInfo o2) {
                    return ((Long) o2.score).compareTo(((Long) o1.score));
                }
            });
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateLowestScore() {
        if (elements.size() < limit) {
            lowestScore = 0;
        } else {
            lock.readLock().lock();
            try {
                long lowest = -1;
                for (TopInfo top : elements) {
                    if (lowest == -1) {
                        lowest = top.score;
                    } else {
                        if (top.score < lowest) {
                            lowest = top.score;
                        }
                    }
                }
                lowestScore = lowest;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public void addTopInfo(TopInfo top) {
        lock.writeLock().lock();
        try {
            if (elements.size() >= limit) {
                elements.set(limit - 1, top);
            } else {
                elements.add(top);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public abstract void load();

    public void show(Char _char) {
        try {
            Message ms = new Message(Cmd.TOP);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(type);
            ds.writeUTF(name);
            ds.writeByte(elements.size());
            int i = 1;
            for (TopInfo top : elements) {
                ds.writeInt(i++);
                ds.writeInt(top.playerID);
                ds.writeShort(top.head);
                ds.writeShort(top.body);
                ds.writeShort(top.leg);
                ds.writeUTF(top.name);
                ds.writeUTF(top.info);
                ds.writeUTF(top.info2);
            }
            ds.flush();
            _char.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }

    }


}
