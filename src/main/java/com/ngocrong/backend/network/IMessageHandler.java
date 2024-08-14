package com.ngocrong.backend.network;

import com.ngocrong.backend.character.Char;

public interface IMessageHandler {
    void setService(IService service);

    void onMessage(Message message);

    void setChar(Char _char);

    void onConnectionFail();

    void onDisconnected();

    void onConnectOK();

    void close();

}
