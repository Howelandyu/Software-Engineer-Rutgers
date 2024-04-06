package serverMain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
//thread class
public class SocketThread extends Thread {
	public final int ID;
    protected Socket socket;
    private ServiceProvider storage;
    private static int count = 1;
    //constructor
    public SocketThread(Socket clientSocket, ServiceProvider storage) {
        this.socket = clientSocket;
        this.storage = storage;
        this.ID = count;
        count++;
    }
    //thread run method
    public void run() {
    	BufferedReader reader;
    	PrintWriter writer;
        try {
        	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            return;
        }
        String msg;
        
        while (true) {
        	try {
        	    msg = reader.readLine();
        	    
            	if (msg == null || msg.equals("null") || msg.equalsIgnoreCase("QUIT")) {
            		System.out.println("Client #" + this.ID +" disconnected!");
                    socket.close();
                    storage.removeThread(this);
                    return;
                }
            	else {
            		this.storage.newRequest(this.ID, msg);
            		while(this.storage.getResponse(this.ID).type!=ServiceProvider.Message.MessageType.RESPONSE);
            		String outputMsg = this.storage.getResponse(this.ID).msg;
            		System.out.println("Client #" + ID + " get response: " + outputMsg);
            		writer.println(outputMsg);
            	}
                
            } 
        	catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Client #" + this.ID +" disconnected!");
                storage.removeThread(this);
                return;
            }
        }
    }
}
