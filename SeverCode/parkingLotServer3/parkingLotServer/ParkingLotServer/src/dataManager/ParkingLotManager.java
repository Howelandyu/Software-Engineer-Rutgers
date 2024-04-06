package dataManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//parking lot class
public class ParkingLotManager {
	private static final Map<String, List<ParkingLot>> PARKINGLOTS; 
	//all of parking lot information
	static {
		Map<String, List<ParkingLot>> mymap = new HashMap<String, List<ParkingLot>>();
		String[] campusNames = {"BUSCH", "COLLEGE AVENUE", "COOK", "LIVINGSTON"};
		for (String camp: campusNames) {
			mymap.put(camp, new ArrayList<ParkingLot>());
		}
		
		int[] availability1 = new int[48];
		for(int i=0;i<48;++i) {
			availability1[i] = 12;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot58B", 0, availability1));
		
		int[] availability2 = new int[48];
		for(int i=0;i<48;++i) {
			availability2[i] = 10;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot58A", 0, availability2));
		
		
		int[] availability3 = new int[48];
		for(int i=0;i<48;++i) {
			availability3[i] = 10;
		}
		mymap.get(campusNames[1]).add(new ParkingLot("Lot24", 0, availability3));
		
		int[] availability4 = new int[48];
		for(int i=0;i<48;++i) {
			availability4[i] = 12;
		}
		mymap.get(campusNames[1]).add(new ParkingLot("Lot20", 0, availability4));
		int[] availability5 = new int[48];
		for(int i=0;i<48;++i) {
			availability5[i] = 15;
		}
		mymap.get(campusNames[2]).add(new ParkingLot("Lot99B", 0, availability5));
		int[] availability6 = new int[48];
		for(int i=0;i<48;++i) {
			availability6[i] = 6;
		}
		mymap.get(campusNames[3]).add(new ParkingLot("Lot105", 0, availability6));
		
		int[] availability7 = new int[48];
		for(int i=0;i<48;++i) {
			availability7[i] = 10;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot58", 0, availability7));
		
		int[] availability8 = new int[48];
		for(int i=0;i<48;++i) {
			availability8[i] = 10;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot606", 1, availability8));
		
		int[] availability9 = new int[48];
		for(int i=0;i<48;++i) {
			availability9[i] = 10;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot602", 1, availability9));
		
		int[] availability10 = new int[48];
		for(int i=0;i<48;++i) {
			availability10[i] = 10;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot55", 1, availability10));
		
		int[] availability11 = new int[48];
		for(int i=0;i<48;++i) {
			availability11[i] = 10;
		}
		mymap.get(campusNames[0]).add(new ParkingLot("Lot56", 1, availability11));
		
		int[] availability12 = new int[48];
		for(int i=0;i<48;++i) {
			availability12[i] = 12;
		}
		mymap.get(campusNames[1]).add(new ParkingLot("Lot22", 1, availability12));
		
		int[] availability13 = new int[48];
		for(int i=0;i<48;++i) {
			availability13[i] = 12;
		}
		mymap.get(campusNames[1]).add(new ParkingLot("Lot35", 1, availability13));
		
		int[] availability14 = new int[48];
		for(int i=0;i<48;++i) {
			availability14[i] = 12;
		}
		mymap.get(campusNames[1]).add(new ParkingLot("Lot26", 0, availability14));
		
		int[] availability15 = new int[48];
		for(int i=0;i<48;++i) {
			availability15[i] = 12;
		}
		mymap.get(campusNames[1]).add(new ParkingLot("Lot25A", 1, availability15));
		
		int[] availability16 = new int[48];
		for(int i=0;i<48;++i) {
			availability16[i] = 6;
		}
		mymap.get(campusNames[2]).add(new ParkingLot("Lot99A", 0, availability16));
		
		int[] availability17 = new int[48];
		for(int i=0;i<48;++i) {
			availability17[i] = 6;
		}
		mymap.get(campusNames[2]).add(new ParkingLot("Lot88", 1, availability17));
		
		int[] availability18 = new int[48];
		for(int i=0;i<48;++i) {
			availability18[i] = 6;
		}
		mymap.get(campusNames[3]).add(new ParkingLot("Lot112", 1, availability18));
		
		int[] availability19 = new int[48];
		for(int i=0;i<48;++i) {
			availability19[i] = 6;
		}
		mymap.get(campusNames[3]).add(new ParkingLot("Lot102", 1, availability19));
		
		int[] availability20 = new int[48];
		for(int i=0;i<48;++i) {
			availability20[i] = 6;
		}
		mymap.get(campusNames[3]).add(new ParkingLot("Lot912", 1, availability20));
		
		PARKINGLOTS = Collections.unmodifiableMap(mymap);
	}
	
	private Map<String, List<ParkingLot>> parkingLots = new HashMap<String, List<ParkingLot>>();
	private String persistentFilePath;
	//parking lot class
	private static class ParkingLot {
		public String parkingLotName;
		private int facultyOnly;
        private int[] availability = new int[48];
        //constructor
        public ParkingLot(String parkingLotName,
                    int facultyOnly,
                    int[] availability)
        {
            this.parkingLotName = parkingLotName;
            this.facultyOnly = facultyOnly>= 1 ? 1 : 0;
            this.availability = availability;
        }
        //
        @Override
        public String toString() {
        	String res = parkingLotName + "," + facultyOnly;
        	for(int i=0; i<this.availability.length; ++i) {
        		res += ("," + availability[i]);
        	}
        	return  res + "\n";
        }
        //get parking lot information an status
        public String getParkingLotInfo() {
        	LocalTime time = LocalTime.now();
        	int slot = 2*(time.getHour()) + (time.getMinute()/30);
        	int available = 0;
        	for(int i=slot; i<this.availability.length; ++i) {
        		if(this.availability[i] != 0) {
        			available = 1;
        			break;
        		}
        	}
        	return parkingLotName + " " + facultyOnly + " " + available +";";
        }
        //get available parking lot
        public String getParkingLotAvailability() {
        	String res = "";
        	for(int i=0; i<this.availability.length; ++i) {
        		res += (availability[i] + " ");
        	}
        	return  res.substring(0, res.length()-1) + ";";
        }
        //update about availability of parking lot
        public void updateParkingLotAvailability(int start, int end, int addOrCancel) 
        throws Exception{
        	assert(addOrCancel == 1 || addOrCancel == -1);
        	for(int i=start; i<end; ++i) {
        		if(this.availability[i] == 0 && addOrCancel < 0) {
        			throw(new Exception("Parkinglot: " + parkingLotName + ", is not avaialble"));
        		}
        	}
        	for(int i=start; i<end; ++i) {
        		this.availability[i] += addOrCancel;
        	}
        }
    }
	//check campus name
	public boolean hasCampus(String camp) {
		for(String campus: parkingLots.keySet()) {
			if(campus.equals(camp)) {
				return true;
			}
		}
		return false;
	}
	//write to file
	private void readPersistentFile() {
		try {
			File file = new File(this.persistentFilePath);
			if (!file.exists()) {
				this.parkingLots = PARKINGLOTS;
				return;
			}
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(this.persistentFilePath));
			String line = reader.readLine();
			while(line!=null) {
				String[] parkingLotInfo = line.split(",", 51);
				int[] availability = new int[48];
				for(int i=0; i<48; i++) {
					availability[i] = Integer.parseInt(parkingLotInfo[i+3]);
				}
				if(!hasCampus(parkingLotInfo[0])) {
					this.parkingLots.put(parkingLotInfo[0], new ArrayList<ParkingLot>());
				}
				this.parkingLots.get(parkingLotInfo[0]).add(new ParkingLot(parkingLotInfo[1], Integer.parseInt(parkingLotInfo[2]), availability));
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//path of file
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
			for (String campus: parkingLots.keySet()) {
				for(ParkingLot parkinglot: parkingLots.get(campus)) {
					content += (campus + "," + parkinglot.toString());
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
	public ParkingLotManager(String persistentFilePath) {
		this.persistentFilePath = persistentFilePath;
		readPersistentFile();
	}

	//get campus information
	public String campParkinglotsInfo(String campus)
	throws Exception
	{
		for(String camp: parkingLots.keySet()){
			if(camp.equals(campus)) {
				String res = "";
				for(ParkingLot parkinglot: parkingLots.get(camp)) {
					res += parkinglot.getParkingLotInfo();
				}
				return res;
			}
		}
		throw(new Exception("Campus name: " + campus + " does not exists."));
	}
	//get parking lot information
	public String parkingLotInfo(String parkingLotName)
	throws Exception
	{
		for(String camp: parkingLots.keySet()){
			for(ParkingLot parkinglot: parkingLots.get(camp)) {
				if(parkinglot.parkingLotName.equals(parkingLotName)) {
					System.out.println(parkinglot.getParkingLotAvailability());
					return parkinglot.getParkingLotAvailability();
				}
			}
		}
		throw(new Exception("ParkingLot name: " + parkingLotName + " does not exists."));
	}
	//new appointment for availability of parking lot
	public void newAppointment(String parkingLotName, int start, int end, int addOrCancel) 
	throws Exception
	{
		for(String camp: parkingLots.keySet()){
			for(ParkingLot parkinglot: parkingLots.get(camp)) {
				if(parkinglot.parkingLotName.equals(parkingLotName)) {
					parkinglot.updateParkingLotAvailability(start, end, addOrCancel);
					return;
				}
			}
		}
		throw(new Exception("ParkingLot name: " + parkingLotName + " does not exists."));
	}
}
