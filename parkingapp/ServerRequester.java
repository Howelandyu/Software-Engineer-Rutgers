package com.example.parkingapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
//face to request to server
public class ServerRequester extends Thread{
    private String requestMsg;
    private String returnMsg;
    private boolean requestInProgress;
    //connect to server
    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void run() {
        try (Socket socket = new Socket("10.0.2.2", 6666)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                try {
                    this.sleep(1000000000);
                }
                catch(InterruptedException e) {
                    writer.println(requestMsg);
                    returnMsg =  reader.readLine();
                    requestInProgress = false;
                }
            }
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
    //new request
    public void newRequest(String request) {
        requestMsg = request;
        requestInProgress = true;
        this.interrupt();
        return;
    }
    //get respond from server
    public String getResponse() {
        return returnMsg;
    }
    //boolean request in progress or not
    public boolean isRequestInProgress() {
        return requestInProgress;
    }
}
