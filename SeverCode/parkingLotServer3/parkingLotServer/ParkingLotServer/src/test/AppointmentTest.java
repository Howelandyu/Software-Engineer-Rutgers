package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import dataManager.AppointmentManager;

class AppointmentTest {
	private String persistentFilePath="C:\\Users\\yuhao\\Desktop\\parking lot project\\SeverCode\\parkingLotServer3\\parkingLotServer\\ParkingLotServer\\storage_appointment.txt";
	AppointmentManager appoinTest=new AppointmentManager(persistentFilePath);
//	@Test
//	public void test01() throws Exception {
//		appoinTest.readPersistentFile();
//		appoinTest.writeToPersistentFile();
//	}
	@Test
	public void changePersistentFilePath() throws Exception {
		appoinTest.changePersistentFilePath(persistentFilePath);
	}
	@Test
	public void newAppointment() throws Exception {
		appoinTest.newAppointment("1", "P1", 30, 40, -1);
		assertEquals(appoinTest.newAppointment("1", "P1", 30, 40, -1),true);
	}
	@Test
	public void newAppointment2() throws Exception {
		appoinTest.newAppointment("1", "P1", 30, 40, -1);
		assertEquals(appoinTest.newAppointment("1", "P1", 30, 40, -1),false);
	}
	@Test
	public void appointmentList() throws Exception {
		assertEquals(appoinTest.appointmentList("6"),"");
	}
	@Test
	public void appointmentList2() throws Exception {
		assertEquals(appoinTest.appointmentList("1"),"P3 P1\r\n"
				+ "P4 P1\r\n"
				+ "P5 P1\r\n"
				+ "P1 P1");
	}
	

}
