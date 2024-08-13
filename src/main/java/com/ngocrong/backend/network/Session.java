package com.ngocrong.backend.network;

import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.user.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Session implements ISession {
    private static final Logger logger = Logger.getLogger(Session.class);
    private static final Lock lock = new ReentrantLock();
    private byte[] key;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    public int id;
    private IMessageHandler messageHandler;
    @Getter
    private IService service;
    private boolean isConnected;
    private byte curR, curW;
    private Thread collectorThread;
    private Thread sendThread;
    private String version;

    @Getter
    private byte zoomLevel;
    private int width;
    private int height;
    private int device; // 0-PC, 1- APK, 2-IOS
    public User user;
    private boolean isSetClientInfo;
    public boolean isEnter = false;
    public String deviceInfo;
    private String ip;

    public Session(Socket socket, String ip, int id) throws IOException {
        this.socket = socket;
        this.ip = ip;
        this.id = id;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        setHandler(new MessageHandler(this));
        this.messageHandler.onConnectOK();
    }






    @Override
    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            doSendMessage(message);
        } catch (IOException e) {
            logger.error("Failed to send message", e);
        }
    }

    @Override
    public void setService(IService service) {
        this.service = service;
    }

    @Override
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Failed to close socket", e);
        }
    }

    @Override
    public void disconnect() {
        close();
        isConnected = false;
    }

    private synchronized void doSendMessage(Message m) throws IOException {
        if (m == null) {
            return;
        }
        byte[] data = m.getData();
        byte command = m.getCommand();
        writeCommand(command);
        if (data != null) {
            writeData(data, command);
        }
        dos.flush();
        m.cleanup();
    }

    private void writeCommand(byte command) throws IOException {
        dos.writeByte(isConnected ? writeKey(command) : command);
    }

    private void writeData(byte[] data, byte command) throws IOException {
        int size = data.length;
        writeDataSize(size, command);
        if (isConnected) {
            for (int i = 0; i < data.length; i++) {
                data[i] = writeKey(data[i]);
            }
        }
        dos.write(data);
    }

    private void writeDataSize(int size, byte command) throws IOException {
        if (isConnected) {
            if (isSpecialMessage(command)) {
                dos.writeByte(writeKey((byte) ((size & 255) - 128)));
                dos.writeByte(writeKey((byte) ((size >> 8) - 128)));
                dos.writeByte(writeKey((byte) ((size >> 16) - 128)));
            } else {
                dos.writeByte(writeKey((byte) (size >> 8)));
                dos.writeByte(writeKey((byte) (size & 255)));
            }
        } else {
            dos.writeByte(size & 256);
            dos.writeByte(size & 255);
        }
    }

    private byte writeKey(byte b) {
        byte b2 = curW;
        curW = (byte) (b2 + 1);
        byte result = (byte) ((key[b2] & 255) ^ (b & 255));
        if (curW >= key.length) {
            curW %= key.length;
        }
        return result;
    }

    private static boolean isSpecialMessage(int command) {
        return command == Cmd.BACKGROUND_TEMPLATE || command == Cmd.GET_EFFDATA || command == Cmd.REQUEST_NPCTEMPLATE
                || command == Cmd.REQUEST_ICON || command == Cmd.GET_IMAGE_SOURCE || command == Cmd.UPDATE_DATA
                || command == Cmd.GET_IMG_BY_NAME;
    }


}
