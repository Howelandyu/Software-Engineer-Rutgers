package dataManager;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import java.util.HashMap;

//appointment data manager
public class AppointmentManager {
	private Map<String, List<Appointment>> appointments = new HashMap<String, List<Appointment>>();
	private String persistentFilePath;
	//appointment class
	private static class Appointment {
		public String parkingLot;
     	public int start;
	    public int end;
	    public Appointment(String parkingLot, int start, int end) {
	    	this.parkingLot = parkingLot;
	    	this.start = start;
	    	this.end = end;
	    }
	    @Override
	    public String toString() {
	    	return this.parkingLot + "," + start + "," + end + "\n";
	    }
	    
	    public String info() {
	    	return this.parkingLot + " " + start + " " + end + ";";
	    }
    }
	//boolean has netid or not
	private boolean hasNetid(String netid) {
		for(String id: appointments.keySet()) {
			if(id.equals(netid)) {
				return true;
			}
		}
		return false;
	}
	//read file
	private void readPersistentFile() {
		try {
			File file = new File(this.persistentFilePath);
			if (!file.exists()) {
				return;
			}
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(this.persistentFilePath));
			String line = reader.readLine();
			while(line!=null) {
				String[] info = line.split(",", 4);
				String netid = info[0];
				if(!hasNetid(netid)) {
					this.appointments.put(netid, new ArrayList<Appointment>());
				}
				this.appointments.get(netid).add(new Appointment(info[1], Integer.parseInt(info[2]), Integer.parseInt(info[3])));
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//for path of file
	public void changePersistentFilePath(String persistentFilePath) {
		this.persistentFilePath = persistentFilePath;
	}
	//write to file
	public void writeToPersistentFile() {
		try {
			File file = new File(this.persistentFilePath);
			file.createNewFile();
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(this.persistentFilePath));
			String content = "";
			for (String netid: appointments.keySet()) {
				for(Appointment appointment: appointments.get(netid)) {
					content += (netid + "," + appointment.toString());
				}
				
			}
			if(content.length() > 0) {
				content = content.substring(0, content.length() - 1);
			}
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//constructor 
	public AppointmentManager(String persistentFilePath) {
		this.persistentFilePath = persistentFilePath;
		readPersistentFile();
	}

	//add new appointment
	public boolean newAppointment(String netid, String parkingLotName, int start, int end, int addOrCancel) {
		boolean addNew = addOrCancel < 0;
		if(!hasNetid(netid)) {
			if(addNew) {
				this.appointments.put(netid, new ArrayList<Appointment>());
			}
			else {
				return false;
			}
			
		}
		for(String id: appointments.keySet()) {
			if(id.equals(netid)) {
				if(addNew) {
					appointments.get(id).add(new Appointment(parkingLotName, start, end));
					return true;
				}
				else {
					for(Appointment appointment: appointments.get(id)) {
						if(appointment.parkingLot.equals(parkingLotName) && 
						   appointment.start == start && 
						   appointment.end == end) 
						{
							appointments.get(id).remove(appointment);
							return true;
							
						}
					}
				}
				
			}
		}
		return false;
	}
	
	//show appointment list function
	public String appointmentList(String netid)
	{
		for(String id:appointments.keySet()) {
			if(id.equals(netid)) {
				String res = "";
				for(Appointment appointment: appointments.get(id)) {
					res += appointment.info();
				}
				return res;
			}
		}
		return "";
	}
}
