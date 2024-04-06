package serverMain;

import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

import dataManager.AccountManager;
import dataManager.AppointmentManager;
import dataManager.ParkingLotManager;

import java.util.HashMap;

//some command, file at server class
public class ServiceProvider extends Thread {
	private Map<Integer, Message> threadPool = new HashMap<Integer, Message>();
	private int pendingRequests;
	private final static String PersistentFileNames = "storage";
	private static AccountManager AccountManager = new AccountManager(PersistentFileNames+"_account.txt");
	private static ParkingLotManager ParkingLotManager = new ParkingLotManager(PersistentFileNames+"_parking.txt");
	private static AppointmentManager AppointmentManager = new AppointmentManager(PersistentFileNames+"_appointment.txt");
	private final static String AdminPassword = "AdminPassword";
	private LocalTime time;
	volatile boolean hasRequests = false;
	
	public ServiceProvider (){}
	//meassage class
	public static class Message {
		public enum MessageType {
			EMPTY,
			REQUEST,
			RESPONSE
		}
		public MessageType type;
		public String msg;
		public Message(MessageType type, String msg) {
			this.type=type;
			this.msg = msg;
		}
		public void changeStatus(MessageType type, String msg) {
			this.msg = msg;
			this.type=type;
		}
	}
	
	//running server function
	public void run() {
		while(true) {
			while(!hasRequests) {
				try {
					super.sleep(10000);
				}
				catch(InterruptedException e) {
					continue;
				}
				
			}
			for(int ID: threadPool.keySet()) {
				if(threadPool.get(ID).type == Message.MessageType.REQUEST) {
					String inputMsg  = threadPool.get(ID).msg;
					threadPool.get(ID).changeStatus(Message.MessageType.RESPONSE, processMsg(inputMsg));
					pendingRequests--;
				}
			}
			LocalTime now = LocalTime.now();
			if(time!=null && Duration.between(time, now).toMinutes() > 2) {
				dumpToFiles();
			}
			time = now;
			
			if(pendingRequests == 0) {
				hasRequests = false;
			}
		}
	}
	//read file
	public void registerSocketThread(Socket clientSocket) {
		SocketThread socketThread = new SocketThread(clientSocket, this);
		socketThread.start();
		threadPool.put(socketThread.ID, new Message(Message.MessageType.EMPTY, ""));

		System.out.println("Server: Client #" + socketThread.ID + " connnected! Current number of clients: " + threadPool.size());
	}
	//new request from app
	public void newRequest(int ID, String msg) {
		System.out.println("Client #" + ID + " new request: " + msg);
		threadPool.get(ID).changeStatus(Message.MessageType.REQUEST, msg);
		pendingRequests++;
		if(!hasRequests) {
			hasRequests = true;
			super.interrupt();
		}
	}
	//get respond
	public Message getResponse(int ID) {
		return threadPool.get(ID);
	}
	//remove thread
	public void removeThread(SocketThread socketThread) {
		if(threadPool.get(socketThread.ID).type==Message.MessageType.REQUEST) {
			pendingRequests--;
		}
		this.threadPool.remove(socketThread.ID);
	}
	//renew file
	private void dumpToFiles() {
		AccountManager.writeToPersistentFile();
		ParkingLotManager.writeToPersistentFile();
		AppointmentManager.writeToPersistentFile();
	}
	
//	signup netid password Email fullname IsFactulty //0 or 1
//	----------------------------------------------------------------------------
//	1 // success
//	0;username already exist // fail
//
//	login User_name Password
//	----------------------------------------------------------------------------
//	1;isFaculty // 0 or 1 succcess
//	0;username does not exist, password wrong.. // fail
//
//	campus campusName
//	----------------------------------------------------------------------------
//	1;parkinglot1 0 1;parkinglot2 1 1 //success (parkinglot name, faculty only (0 or 1), available (0 or 1))
//	0 //fail
//
//	parkingLot parkingLot
//	----------------------------------------------------------------------------
//	1;0 0 0 0 0 0 0 10 10 10 10 ... // success availability in each time period
//	0 //fail
//
//	appointment username parkinglot s e -1 // s is an integer which means sth half hour in a day(0<= s < 48, s < e) addorcancel(add -1, cancel 1)
//	----------------------------------------------------------------------------
//	1// success availability in each time period
//	0;failing reason//fail
//  
// appointmentlist username
// -------------------------------------------------------------------------------
//  1;parkinglot start end;parkinglot start end;
//
// dumptofile <password>
// -------------------------------------------------------------------------------
//  1;parkinglot start end;parkinglot start end;
	//command from input, and respond
	private String processMsg(String inputMsg) {
		String outputMsg = "nan";
		if(inputMsg.startsWith("signup")) {
	        String[] signupMsg = inputMsg.split(" ", 6);
	        try {
	        	signUp(signupMsg[1], signupMsg[2], signupMsg[3], signupMsg[4], Integer.parseInt(signupMsg[5]));
	        	outputMsg = "1"; 
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }
	    }
	    else if(inputMsg.startsWith("login")) {
	    	String[] loginMsg = inputMsg.split(" ", 3);
	        try {
	        	int isFaculty = login(loginMsg[1], loginMsg[2]);
	        	outputMsg = "1;" + isFaculty; 
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }
	    } 
	    else if(inputMsg.startsWith("campus")) {
	    	String[] campusMsg = inputMsg.split(" ", 2);
	        try {
	        	String parkingLotsInfo = selectCampus(campusMsg[1]);
	        	outputMsg = "1;" + parkingLotsInfo; 
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }	 
	    }
	    else if(inputMsg.startsWith("parkingLot")) {
	    	String[] parkingLotMsg = inputMsg.split(" ", 2);
	        try {
	        	String parkingLotInfo = selectParkingLot(parkingLotMsg[1]);
	        	outputMsg = "1;" + parkingLotInfo; 
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }	 
	    }
	    else if(inputMsg.startsWith("appointmentlist")) {
	    	String[] appointmentListMsg = inputMsg.split(" ", 2);
	        try {
	        	outputMsg = "1;"+appointmentList(appointmentListMsg[1]);
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }
	    }
	    else if(inputMsg.startsWith("appointment")) {
	    	String[] appointmentMsg = inputMsg.split(" ", 6);
	        try {
	        	makeAppointment(appointmentMsg[1], 
	        					appointmentMsg[2], 
	        					Integer.parseInt(appointmentMsg[3]), 
	        					Integer.parseInt(appointmentMsg[4]), 
	        					Integer.parseInt(appointmentMsg[5])
	        				   );
	        	outputMsg = "1;"; 
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }
	    }
	    else if(inputMsg.startsWith("dumptofile")) {
	    	String[] passWordMsg = inputMsg.split(" ", 2);
	    	try {
	    		if(passWordMsg[1].equals(AdminPassword)) {
	    			dumpToFiles();
	    			outputMsg = "1;";
	    		}
	    	}
	    	catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }
	    }
	    else if(inputMsg.startsWith("update")) {
	    	String[] updateUser = inputMsg.split(" ", 5);
	        try {
	        	userUpdate(updateUser[1], 
	        			   updateUser[2], 
	        			   updateUser[3], 
	        			   updateUser[4]
	        				   );
	        	outputMsg = "1;"; 
	        }
	        catch(Exception e) {
	        	outputMsg = "0;" + e.getMessage();
	        }
	    }
	    else {
	    	outputMsg="0;Unknow request" + inputMsg;
	    }
		
		return outputMsg;
	}
	
	
	//sign up method
	private void signUp(String netid, String password, String email, String fullname, int isFaculty)
	throws Exception
	{
		AccountManager.newUser(netid, password, email, fullname, isFaculty);
	}
	//login method
	private int login(String netid, String password)
	throws Exception
	{
		return AccountManager.validPassword(netid, password);
	}
	//campus method
	private String selectCampus(String campus)
	throws Exception
	{
		return ParkingLotManager.campParkinglotsInfo(campus);
	}
	//parking lot method
	private String selectParkingLot(String parkingLot)
	throws Exception
	{
		return ParkingLotManager.parkingLotInfo(parkingLot);
	}
	//appointment method
	private void makeAppointment(String netid, String parkinglot, int start, int end, int addOrCancel)
	throws Exception
	{
		if(!AccountManager.hasNetId(netid)) {
			throw(new Exception("Unknown User: " + netid));
		}
		ParkingLotManager.newAppointment(parkinglot, start, end, addOrCancel);
		if(!AppointmentManager.newAppointment(netid, parkinglot, start, end, addOrCancel)) {
			ParkingLotManager.newAppointment(parkinglot, start, end, -addOrCancel);
			throw(new Exception("Cannot cancel appointment, no such appointment exists."));
		}
	}
	//appointmentlist method
	private String appointmentList(String netid)
	throws Exception
	{
		if(!AccountManager.hasNetId(netid)) {
			throw(new Exception("Unknown User: " + netid));
		}
		return AppointmentManager.appointmentList(netid);
	}
	//update method
	private void userUpdate(String netid, String password, String fullname, String email)
	throws Exception
	{
		if(!AccountManager.hasNetId(netid)) {
			throw(new Exception("Unknown User: " + netid));
		}
		AccountManager.update(netid,password,fullname,email);
	}
	
}
