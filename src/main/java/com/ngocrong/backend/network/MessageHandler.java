package com.ngocrong.backend.network;

import org.apache.log4j.Logger;

public class MessageHandler implements IMessageHandler{
    private static final Logger logger = Logger.getLogger(MessageHandler.class);
    private Session session;
    private Service service;
//    private Char _char;


    public MessageHandler(Session client) {
        this.session = client;
    }

    @Override
    public void setService(IService service) {
        this.service = (Service) service;
    }

    @Override
    public void onMessage(Message message) {
        if (message != null) {

        }
    }

    @Override
    public void onConnectionFail() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectOK() {

    }

    @Override
    public void close() {
        session = null;
        service = null;

    }




}
