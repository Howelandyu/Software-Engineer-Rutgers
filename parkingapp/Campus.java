package com.example.parkingapp;

import java.util.List;
//campus class
public class Campus {
    private String campusName;
    private List<ParkingLot> parkingLots;
    //campus constructor
    public Campus(String campusName, DatabaseConnector dbConnector) {
        this.campusName = campusName;
        this.parkingLots = dbConnector.requestParkingLotsInfo(this.campusName);
    }
    //get parking lot
    public List<ParkingLot> getAllParkingLots() {
        return parkingLots;
    }
    //get campus name
    public String getCampusName() {
        return campusName;
    }

}
