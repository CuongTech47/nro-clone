package com.ngocrong.backend.network;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender implements Runnable{
    private static final Logger logger = Logger.getLogger(Sender.class);
    private final BlockingQueue<Message> sendingMessages;

    private Session session;

    public Sender(Session session) {
       this.sendingMessages = new LinkedBlockingQueue<Message>();
       this.session = session;
    }

    public void addMessage(Message message) {
        sendingMessages.offer(message);
    }

    @Override
    public void run() {
        try{
            while (session.isConnected()) {
                processMessages();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Sender thread interrupted",e);
                }
            }
        }catch (Exception e) {
            logger.error("IOException in Sender thread", e);
        }
    }

    private void processMessages() throws IOException {
        Message message = sendingMessages.poll();
        if (message != null) {
            session.doSendMessage(message);
        }
    }
}
