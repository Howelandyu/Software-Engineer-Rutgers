package com.example.parkingapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
// parking lot class
public class ParkingLot extends AppCompatButton{
    private String name;
    private boolean available;
    private boolean facultyOnly;
    private MainActivity mainApp;
    private int startTimePoint = -1;
    private int endTimePoint = -1;
    //a time class
    private class TimeSlot extends AppCompatButton {
        private ParkingLot parkingLot;
        public int startTime;
        @RequiresApi(api = Build.VERSION_CODES.O)
        TimeSlot(Context context,
                 int startTime,
                 int availableSpace,
                 ParkingLot parkingLot) {
            super(context);
            LocalTime now = LocalTime.now();
            int hour = startTime/2;
            int minute = 30*(startTime % 2);
            int currentHour = now.getHour();
            int currentMinute = now.getMinute();
            String placeholder = "";
            if(availableSpace < 10) {
                placeholder = "0";
            }
            super.setText(DatabaseConnector.getTime(startTime)+
                        "~ (" + placeholder + availableSpace + ")");

            if(availableSpace == 0 || currentHour > hour ||
                    (currentHour == hour && currentMinute > minute + 15)) {
                super.setEnabled(false);
            }
            this.startTime = startTime;
            this.parkingLot = parkingLot;
            this.setOnClickListener(new OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    TimeSlot timeSlot = (TimeSlot) v;
                    timeSlot.setTime(timeSlot.startTime);
                }
            });
        }
        public void setTime(int startTime) {
            this.parkingLot.setTime(startTime);
        }
    }
    //parking lot constructor
    public ParkingLot(Context context,
                      String parkingLotName,
                      boolean isFaculty,
                      boolean facultyOnly,
                      boolean available) {
        super(context);
        super.setText(parkingLotName);
        this.mainApp=(MainActivity)context;
        this.name = parkingLotName;
        this.facultyOnly = facultyOnly;
        this.available = available;
        if(!available || (!isFaculty && facultyOnly)) {
            super.setEnabled(false);
        }
        this.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                ParkingLot parkingLot = (ParkingLot) v;
                parkingLot.setContentView(R.layout.activity_appointment);
            }
        });
    }

    //make appointment page
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setContentView(int layoutResID) {
        this.mainApp.setContentView(layoutResID);
        List<Integer> availabilities = this.mainApp.parkingLotInfo(this.name);
        TableLayout tableView = this.mainApp.findViewById(R.id.availableSlot);
        tableView.removeAllViews();
        int h = tableView.getLayoutParams().height;
        int w = tableView.getLayoutParams().width;
        for(int r = 0; r < 12; ++r) {
            TableRow boardRow = new TableRow(this.mainApp);
            LinearLayout.LayoutParams layoutParams = new TableRow.LayoutParams(w/4, h/12);
            layoutParams.weight = 1;
            layoutParams.gravity= Gravity.CENTER;
            for(int c=0; c < 4; ++c) {
                TimeSlot square = new TimeSlot(this.mainApp, r*4+c, availabilities.get(r*4+c), this);
                square.setLayoutParams(layoutParams);
                boardRow.addView(square);
            }
            tableView.addView(boardRow);
        }
        this.mainApp.setCurrentParkingLot(this);
    }

    //function of time when choossing it
    public void setTime(int startTime) {
        ((TextView)this.mainApp.findViewById(R.id.timeSlotError)).setText("");
        if(startTimePoint == -1 && endTimePoint == -1) {
            startTimePoint = startTime;
        }
        else if(startTimePoint == -1 && endTimePoint > 0) {
            if(startTime < endTimePoint) {
                startTimePoint = startTime;
            }
            else{
                startTimePoint = endTimePoint;
                endTimePoint = startTime;
            }
        }
        else if(startTimePoint > 0 && endTimePoint == -1) {
            if(startTime > startTimePoint) {
                endTimePoint = startTime;
            }
            else{
                endTimePoint = startTimePoint;
                startTimePoint = startTime;
            }
        }
        else{
            ((TextView)this.mainApp.findViewById(R.id.timeSlotError)).setText(
                    "Already Set StartTime and endTime, need to clear to reset them if you want to change");
        }
        if(startTimePoint > 0) {
            ((TextView)this.mainApp.findViewById(R.id.startTime)).setText(DatabaseConnector.getTime(startTimePoint));
        }
        if(endTimePoint > 0) {
            ((TextView)this.mainApp.findViewById(R.id.endTime)).setText(DatabaseConnector.getTime(endTimePoint+1));
        }
    }

    //clear time when click clear
    public void clearSelection() {
        ((TextView)this.mainApp.findViewById(R.id.timeSlotError)).setText("");
        this.startTimePoint = -1;
        this.endTimePoint = -1;
        ((TextView)this.mainApp.findViewById(R.id.startTime)).setText("");
        ((TextView)this.mainApp.findViewById(R.id.endTime)).setText("");

    }

    //get appointment
    public DatabaseConnector.Appointment getAppointment(Context context) {
        if(this.startTimePoint < 0 || this.endTimePoint < 0) {
            ((TextView)findViewById(R.id.timeSlotError)).setText("Please select start/end time slot");
            return null;
        }
        return new DatabaseConnector.Appointment(context, this.name,
                this.startTimePoint, this.endTimePoint + 1);
    }

    //get parkinglot name
    public String getParkingLotName() {
        return name;
    }
}
