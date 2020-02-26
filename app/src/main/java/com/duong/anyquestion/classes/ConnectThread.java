package com.duong.anyquestion.classes;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
public class ConnectThread extends Thread {

    private static ConnectThread instance;
    // implementation part
    private Socket mSocket;
    //private String URL = "https://server-anyquestion-3.herokuapp.com";///

    private String URL = "http://172.20.10.11:3000";///


    public static ConnectThread getInstance(){
        return (instance == null) ? instance = new ConnectThread() : instance;
    }

    private ConnectThread(){
        try {
            mSocket = IO.socket(URL);
            mSocket.connect();
        } catch (URISyntaxException e) { }
    }

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void run() {
        try {
            mSocket = IO.socket(URL);
            mSocket.connect();
        } catch (URISyntaxException e) { }
    }
}
