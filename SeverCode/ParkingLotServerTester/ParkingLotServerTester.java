import java.net.*;
import java.io.*;
 
//tester of server, and administration controller
public class ParkingLotServerTester {
 
    public static void main(String[] args) {
    	
        String hostname = "localhost";
        
        int port = 6666;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        else if (args.length >= 2) {
            hostname = args[0];
            port = Integer.parseInt(args[1]);
        }
 
        try (Socket socket = new Socket(hostname, port)) {
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            Console console = System.console();
            String text;
 
 			int count = 0;
            do {
                text = console.readLine("Enter request: ");
 
                writer.println(text);
 
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
                String response = reader.readLine();
 
                System.out.println("Server response: " + response);
                System.out.println("----------------------------------------------");
 
 
            } while (count++ < 10);
 
		    writer.println("quit");
            socket.close();
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}