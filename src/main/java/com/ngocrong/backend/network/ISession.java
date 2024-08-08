package com.ngocrong.backend.network;

public interface ISession {
    boolean isConnected();

    void setHandler(IMessageHandler messageHandler);

    void sendMessage(Message message);

    void setService(IService service);

    void close();

    void disconnect();
}
