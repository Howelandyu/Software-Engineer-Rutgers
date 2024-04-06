package serverMain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//main class
public class ServerMain {
	//main function
	public static void main(String[] args) {
		int port = 6666;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            ServiceProvider storage = new ServiceProvider();
            storage.start();
            
            while (true) {
            	try {
            		Socket socket = serverSocket.accept();
            		storage.registerSocketThread(socket);
            	} catch(IOException ex) {
            		System.out.println(ex.getMessage());
            	}
            }
        }
        catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
