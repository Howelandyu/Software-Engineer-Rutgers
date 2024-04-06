package com.example.parkingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// this class is for deal with data from server
public class DatabaseConnector {
    private List<Appointment> appointments = new ArrayList<>();
    private final ServerRequester server = new ServerRequester();
    private final MainActivity context;
    //DatabaseConnector constructor
    DatabaseConnector(MainActivity context) {
        this.context = context;
        server.start();
    }
    // request
    private String requestServer(String request) {
        server.newRequest(request);
        while(server.isRequestInProgress());
        return server.getResponse();
    }
    //Appoinment class
    @SuppressLint("ViewConstructor")
    public static class Appointment extends AppCompatButton {
        public int startTimePoint;
        public int endTimePoint;
        public String parkingLotName;

        @SuppressLint("SetTextI18n")
        public Appointment(Context context, String parkingLotName, int startTimePoint, int endTimePoint) {
            super(context);
            this.startTimePoint = startTimePoint;
            this.endTimePoint = endTimePoint;
            this.parkingLotName = parkingLotName;
            super.setText(getTime(startTimePoint) + " ~ " + getTime(endTimePoint) + " " + parkingLotName);
            final MainActivity mainStage = (MainActivity) context;
            this.setOnClickListener(new OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    Appointment appointment = (Appointment) v;
                    mainStage.setCurrentAppointment(appointment);
                    appointment.setTextColor(Color.RED);
                }
            });
        }
    }
    //function of getting appointment time
    public static String getTime(int timePoint) {
        int hour = timePoint/2;
        int min = (timePoint - hour * 2)*30;
        if (hour== 24) {
            hour = 23;
            min = 59;
        }
        String start = "";
        if(hour < 10) {
            start = "0";
        }
        String end = "";
        if(min==0) {
            end = "0";
        }
        return start + hour + ":" + min + end;
    }
    //function of add new user when sign up
    public void addNewUser(String fullname, String email,
                           String username,  String password, boolean isFaculty)
            throws Exception
    {
        //now we do not consider special characters, should add the feature later
        if (fullname.equals("")) {
            throw new Exception("Fullname cannot be empty");
        }
        else if (username.equals("")) {
            throw new Exception("Username cannot be empty");
        }
        else if (password.equals("")) {
            throw new Exception("Password cannot be empty");
        }
        else if (email.equals("")) {
            throw new Exception("Email cannot be empty");
        }
        //signup <netid> <password> <Email> <fullname> <IsFactulty 0/1>
        else {
            String isFacultyStr = "0";
            if (isFaculty) {
                isFacultyStr = "1";
            }

            String request = "signup " + username + " " + password + " "
                            + email + " " + fullname + " " + isFacultyStr;
            String response = requestServer(request);
            String[] responseInfo = response.split(";");
            if (responseInfo[0].equals("0")) {
                throw new Exception("Server Error: " + responseInfo[1]);
            }
        }
    }
    // function of login
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void userLogin(String username, String password)
        throws Exception
    {
        String request = "login " + username + " " + password;
        String response = requestServer(request);
        String[] responseInfo = response.split(";");
        if (responseInfo[0].equals("0")) {
            throw new Exception("Server Error: " + responseInfo[1]);
        }
        else {
            context.setIsFaculty(responseInfo[1].equals("1"));
            context.setNetId(username);
        }
    }

    // campus <campusName>
    // request parking lot from server
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<ParkingLot> requestParkingLotsInfo(String campusName) {
        List<ParkingLot> parkingLots = new ArrayList<>();
        String request = "campus " + campusName;
        String response = requestServer(request);
        String[] responseInfo = response.split(";");
        if (BuildConfig.DEBUG && !(responseInfo[0].equals("1"))) {
            throw new AssertionError("Assertion failed");
        }
        for (int i = 1; i < responseInfo.length; ++i) {
            // parkinglot_name, faculty only 0/1, available 0/1
            String[] parkingLotInfo = responseInfo[i].split(" ");
            parkingLots.add(new ParkingLot(context, parkingLotInfo[0], this.context.isFaculty(),
                    parkingLotInfo[1].equals("1"), parkingLotInfo[2].equals("1")));
        }
        return parkingLots;
    }

    //appointment <username> <parkinglot> <start> <end> <add/cancel -1/1>
    //create a new appointment
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void newAppointment(ParkingLot parkingLot)
        throws Exception
    {
        Appointment appointment = parkingLot.getAppointment(this.context);
        if(appointment == null) {
            throw new Exception("Invalid Appointment Selected");
        }
        String request = "appointment " + context.getNetId() + " " + parkingLot.getParkingLotName() +
                " " + appointment.startTimePoint + " " + appointment.endTimePoint + " -1";
        String returnMsg = requestServer(request);
        if(returnMsg.split(";")[0].equals("0")) {
            throw new Exception("Server error: " + returnMsg.split(";")[1]);
        }
        if(this.appointments.size() > 0) {
            this.appointments.add(appointment);
        }

    }

    // appointmentlist <username>
    //request appointment from server
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<Appointment> getAppointments()
    {
        if(this.appointments.size() > 0) {
            return this.appointments;
        }
        String request = "appointmentlist " + context.getNetId();
        String response = requestServer(request);
        String[] responseInfo = response.split(";");
        if (BuildConfig.DEBUG && !(responseInfo[0].equals("1"))) {
            throw new AssertionError("Assertion failed");
        }
        List<Appointment> appointments = new ArrayList<>();
        for (int i = 1; i < responseInfo.length; ++i) {
            // parkinglot_name, faculty only 0/1, available 0/1
            String[] appointmentInfo = responseInfo[i].split(" ");
            appointments.add(new Appointment(context,
                    appointmentInfo[0],
                    Integer.parseInt(appointmentInfo[1]),
                    Integer.parseInt(appointmentInfo[2])));
        }
        this.appointments = appointments;
        return appointments;
    }

    //remove an appointment
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeAppointment(Appointment appointment) {
        String request = "appointment " + context.getNetId() + " " + appointment.parkingLotName +
                " " + appointment.startTimePoint + " " + appointment.endTimePoint + " 1";
        String response =  requestServer(request);
        this.appointments.remove(appointment);
    }
    // update user information
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void userUpdate(String username, String password,String fullname, String email)
            throws Exception
    {
        String request = "update " + username + " " + password + " " + fullname  + " " + email;
        if(password.length() == 0 || fullname.length() == 0 || email.length() == 0)
        {
            throw new Exception("Error: there exists unfilled entry");
        }
        String response = requestServer(request);
        String[] responseInfo = response.split(";");
        if (responseInfo[0].equals("0")) {
            throw new Exception("Server Error: " + responseInfo[1]);
        }
    }
    //get available parking lot
    public List<Integer> getAvailability(String parkingLotName)
    {
        String request = "parkingLot " + parkingLotName;
        String response = requestServer(request);
        String[] responseInfo = response.split(";");
        List<Integer> availability = new ArrayList<>();
        for(String s: responseInfo[1].split(" ")) {
            availability.add(Integer.parseInt(s));
        }
        return availability;
    }

}
