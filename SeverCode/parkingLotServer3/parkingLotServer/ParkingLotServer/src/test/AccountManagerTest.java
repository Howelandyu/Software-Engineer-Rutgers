package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import dataManager.AccountManager;
//import dataManager.AccountManager.User;




class AccountManagerTest {
	private String persistentFilePath="C:\\Users\\yuhao\\Desktop\\parking lot project\\SeverCode\\parkingLotServer3\\parkingLotServer\\ParkingLotServer\\storage_account.txt";
	AccountManager account=new AccountManager(persistentFilePath);
//	private String persistentFilePath="C:\\Users\\yuhao\\Desktop\\parking lot project\\SeverCode\\parkingLotServer2\\parkingLotServer\\ParkingLotServer";
//	@Test
//	public void test01() throws Exception {
//		account.readPersistentFile();
//		account.writeToPersistentFile();
//	}
//	
	@Test
	public void hasNetId() {
		assertTrue(account.hasNetId("1"));
	}
	@Test
	public void hasNetId2() {
		assertFalse(account.hasNetId("xxx"));
	}
	@Test
	public void newUser() throws Exception {
		account.newUser("3", "3", "3", "3", 0);
	}

	@Test
	public void validPassword() throws Exception {
		assertEquals(account.validPassword("A","ABC"),0);
	}
	
	@Test
	public void validPassword2() throws Exception {
		assertEquals(account.validPassword("aaa",""),1);
	}
	
	
 
}