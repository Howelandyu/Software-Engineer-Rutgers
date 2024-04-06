package dataManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//class for account data
public class AccountManager {
	private Map<String, User> users = new HashMap<String, User>();
	private String persistentFilePath;
	//User class
	private static class User {
        private String password;
        private String email;
        private String fullname;
        int isFaculty;

        public User(String password,
                    String email,
                    String fullname,
                    int isFaculty)
        {
            this.fullname = fullname;
            this.email = email;
            this.password = password;
            this.isFaculty = isFaculty >= 1 ? 1 : 0;
        }
        @Override
        public String toString() {
        	return password + "," + email + "," + fullname + "," + isFaculty + "\n";
        }
        public boolean validPassword(String password) { return this.password.equals(password); }
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
				String[] userInfo = line.split(",", 5);
				this.users.put(userInfo[0], new User(userInfo[1], userInfo[2], userInfo[3], Integer.parseInt(userInfo[4])));
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
	//write file
	public void writeToPersistentFile() {
		try {
			File file = new File(this.persistentFilePath);
			file.createNewFile();
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(this.persistentFilePath));
			String content = "";
			for (String netid: users.keySet()) {
				content += (netid + "," + users.get(netid).toString());
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
	public AccountManager(String persistentFilePath) {
		this.persistentFilePath = persistentFilePath;
		readPersistentFile();
	}

	//boolean has netid or not
	public boolean hasNetId(String netId) {
		for(String netid: users.keySet()) {
			if(netid.equals(netId)) {
				return true;
			}
		}
		return false;
	}
	//add new user
	public void newUser(String netid, String password, String email, String fullname, int isFaculty)
	throws Exception{
		if(hasNetId(netid)) {
			throw(new Exception("Username already exists."));
		}
		users.put(netid, new User(password, email, fullname, isFaculty));
	}
	//judge password valid or not
	public int validPassword(String netid, String password)
	throws Exception
	{
		for(String id: users.keySet()) {
			if(id.equals(netid)) {
				User user = users.get(id);
				if(user.validPassword(password)) {
					return user.isFaculty;
				}
				else {
					throw(new Exception("Password incorrect!"));
				}
			}
		}
		throw(new Exception("Username does not exist."));
	}
	//updtae information
	public void update(String netid, String password, String email, String fullname)
	{
		for(String id: users.keySet()) {
			if(id.equals(netid)) {
				User user = users.get(id);
				user.email=email;
				user.fullname=fullname;
				user.password=password;
				return;
			}
		}
	}
}
