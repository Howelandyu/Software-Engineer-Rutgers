package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import dataManager.ParkingLotManager;

class ParkingLotTest {
	private String persistentFilePath="C:\\Users\\yuhao\\Desktop\\parking lot project\\SeverCode\\parkingLotServer3\\parkingLotServer\\ParkingLotServer\\storage_parking.txt";
	ParkingLotManager parkTest=new ParkingLotManager(persistentFilePath);
	
	@Test
	public void test01() throws Exception {
		parkTest.hasCampus("BUSCH");
	}
	@Test
	public void test02() throws Exception {
		assertEquals(parkTest.hasCampus(" "),false);
//		parkTest.hasCampus(" ");
	}
	@Test
	public void campParkinglotsInfo() throws Exception {
		assertEquals(parkTest.campParkinglotsInfo("BUSCH"),"P1 0 1;");
	}
	@Test
	public void campParkinglotsInfo2() throws Exception {
		assertEquals(parkTest.campParkinglotsInfo("BUSCH"),"Lot58B 0 1;Lot58A 0 1;Lot58 0 1;Lot606 1 1;Lot602 1 1;Lot55 1 1;Lot56 1 1;");
	}
	@Test
	public void parkingLotInfo() throws Exception {
		assertEquals(parkTest.parkingLotInfo("Lot22"),"12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12,12 12 12 12 12 12 12 12 11 11 11 11 11 11 11 11 12 12 12 12 12 12 12 12;");		
	}
	@Test
	public void parkingLotInfo2() throws Exception {
		assertEquals(parkTest.parkingLotInfo("Lot22"),"12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12 12;");		
	}
//	@Test
//	public void newAppointmentTest() throws Exception {
//		assertEquals(parkTest.newAppointment("Lot58",30,40,-1),;		
//	}
}
