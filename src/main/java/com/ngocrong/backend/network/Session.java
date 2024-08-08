package com.ngocrong.backend.network;

import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.user.User;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Session implements ISession{
    private static final Logger logger = Logger.getLogger(Session.class);
    private static final Lock lock = new ReentrantLock();
    private byte[] key;
    public Socket socket;
    public DataInputStream dis;
    public DataOutputStream dos;
    public int id;
    public IMessageHandler messageHandler;
    @Getter
    private IService service;
    protected boolean isConnected, isLogin;
    private byte curR, curW;
//    private final Sender sender;
    private Thread collectorThread;
    protected Thread sendThread;
    protected String version;
    protected byte zoomLevel;
    protected int width;
    protected int height;
    protected int device; // 0-PC, 1- APK, 2-IOS
    public User user;
//    public Char _char;
    private boolean isSetClientInfo;
    public boolean isEnter = false;
    public String deviceInfo;
    public String ip;

    public Session(Socket socket, String ip, int id) throws IOException {
        this.socket = socket;
        this.ip = ip;
        this.id = id;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        setHandler(new MessageHandler(this));
        messageHandler.onConnectOK();
//        setService(new Service(this));
//        sendThread = new Thread(sender = new Sender());
    }

//    public IMessageHandler messageHandler;
    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {

    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void setService(IService service) {

    }

    @Override
    public void close() {

    }

    @Override
    public void disconnect() {

    }

    protected synchronized void doSendMessage(Message m) throws IOException {
        // Kiểm tra nếu thông điệp null thì thoát khỏi phương thức
        if (m == null) {
            return;
        }

        // Lấy dữ liệu và lệnh từ thông điệp
        byte[] data = m.getData();
        byte command = m.getCommand();

        // Ghi lệnh vào luồng đầu ra
        writeCommand(command);

        // Nếu dữ liệu không null, ghi dữ liệu vào luồng đầu ra
        if (data != null) {
            writeData(data, command);
        }

        // Xả luồng để đảm bảo dữ liệu được gửi đi
        dos.flush();

        // Dọn dẹp thông điệp để giải phóng tài nguyên
        m.cleanup();
    }

    private void writeCommand(byte command) throws IOException {
        // Ghi lệnh vào luồng đầu ra, nếu kết nối thì mã hóa lệnh trước khi ghi
        dos.writeByte(isConnected ? writeKey(command) : command);
    }

    private void writeData(byte[] data, byte command) throws IOException {
        int size = data.length;

        // Ghi kích thước dữ liệu vào luồng đầu ra
        writeDataSize(size, command);

        // Nếu kết nối, mã hóa dữ liệu trước khi ghi
        if (isConnected) {
            for (int i = 0; i < data.length; i++) {
                data[i] = writeKey(data[i]);
            }
        }

        // Ghi dữ liệu vào luồng đầu ra
        dos.write(data);
    }

    private void writeDataSize(int size, byte command) throws IOException {
        // Ghi kích thước dữ liệu vào luồng đầu ra, mã hóa nếu kết nối
        if (isConnected) {
            if (isSpecialMessage(command)) {
                // Nếu là thông điệp đặc biệt, ghi kích thước với offset -128
                dos.writeByte(writeKey((byte) ((size & 255) - 128)));
                dos.writeByte(writeKey((byte) ((size >> 8) - 128)));
                dos.writeByte(writeKey((byte) ((size >> 16) - 128)));
            } else {
                // Ghi kích thước bình thường
                dos.writeByte(writeKey((byte) (size >> 8)));
                dos.writeByte(writeKey((byte) (size & 255)));
            }
        } else {
            // Nếu không kết nối, ghi kích thước mà không mã hóa
            dos.writeByte(size & 256);
            dos.writeByte(size & 255);
        }
    }

    private byte writeKey(byte b) {
        // Mã hóa byte b bằng khóa và con trỏ curW
        byte b2 = curW;
        curW = (byte) (b2 + 1);
        byte result = (byte) ((key[(int) b2] & 255) ^ ((int) b & 255));

        // Nếu con trỏ vượt quá độ dài khóa, đặt lại về đầu khóa
        if (curW >= key.length) {
            curW = (byte) (curW % key.length);
        }
        return result;
    }

    private static boolean isSpecialMessage(int command) {
        // Kiểm tra xem lệnh có phải là lệnh đặc biệt không
        return command == Cmd.BACKGROUND_TEMPLATE || command == Cmd.GET_EFFDATA || command == Cmd.REQUEST_NPCTEMPLATE
                || command == Cmd.REQUEST_ICON || command == Cmd.GET_IMAGE_SOURCE || command == Cmd.UPDATE_DATA
                || command == Cmd.GET_IMG_BY_NAME;
    }









}
