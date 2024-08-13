package com.ngocrong.backend.network;


import java.io.*;

public class Message implements AutoCloseable{
    private byte command;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    private DataInputStream dis;

    public Message(int command) {
        this((byte) command);
    }

    public Message(byte command) {
        this.command = command;
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(os);
    }

    public Message(byte command, byte[] data) {
        this.command = command;
        this.is = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(is);
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(int cmd) {
        this.command = (byte) cmd;
    }

    public void setCommand(byte cmd) {
        this.command = cmd;
    }

    public byte[] getData() {
        return os != null ? os.toByteArray() : new byte[0];
    }

    public DataInputStream getReader() {
        return dis;
    }

    public DataOutputStream getWriter() {
        return dos;
    }

    public void cleanup() {
        closeQuietly(dis);
        closeQuietly(dos);
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // log the error if necessary
            }
        }
    }

    @Override
    public void close() throws Exception {
        cleanup();
    }
}
