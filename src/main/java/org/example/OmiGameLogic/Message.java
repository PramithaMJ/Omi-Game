package org.example.OmiGameLogic;

public class Message {
    private static Message instance;
    private String msg;

    private Message() {
        System.out.println("Creating Message instance");
    }

    public static Message getInstance() {
        if (instance == null) {
            instance = new Message();
        }
        return instance;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}