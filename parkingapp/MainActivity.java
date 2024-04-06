package com.example.parkingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ZoomControls;

import java.util.List;
// main function here
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    ZoomControls zoomControls;
    private DatabaseConnector dbConnector = new DatabaseConnector(this);
    private String netId;
    private boolean isFaculty = false;
    private ParkingLot parkingLot;
    private String currentCampus;
    private DatabaseConnector.Appointment currentSelectedAppointment;
    //initial function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    //encapsulation
    public void setIsFaculty(boolean isFaculty) {
        this.isFaculty = isFaculty;
    }
    public boolean isFaculty() { return this.isFaculty; }
    public void setNetId(String netId) { this.netId = netId; }
    public String getNetId() { return this.netId; }
    // get available parking lot
    public List<Integer> parkingLotInfo(String parkingLot) {
        return this.dbConnector.getAvailability(parkingLot);
    }

    /*
     * activity signup buttons onClicked start
     */
    // student buttom on signup
    public void studentRaidoButtonOnClicked(View v) {
        RadioButton faculty = (RadioButton) findViewById(R.id.faculty);
        if (faculty.isChecked()) {
            faculty.toggle();
        }
    }
    // faculty buttom on signup
    public void facultyRadioButtonOnClicked(View v) {
        RadioButton student = (RadioButton) findViewById(R.id.student);
        if (student.isChecked()) {
            student.toggle();
        }
    }
    // click signup buttom
    public void signupButtonClicked(View v) {
        TextView fullNameView = (TextView) findViewById(R.id.fullname);
        TextView emailView = (TextView) findViewById(R.id.email);
        TextView netidView = (TextView) findViewById(R.id.username);
        TextView passwordView = (TextView) findViewById(R.id.password);
        TextView errorView = (TextView) findViewById(R.id.signUpErrorMsg);
        RadioButton faculty = (RadioButton) findViewById(R.id.faculty);
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        String fullName = fullNameView.getText().toString();
        String email = emailView.getText().toString();
        String netid = netidView.getText().toString();
        String password = passwordView.getText().toString();

        try {
            dbConnector.addNewUser(fullName, email, netid, password, faculty.isChecked());
            errorView.setVisibility(View.INVISIBLE);
            setContentView(R.layout.activity_login);
        } catch(Exception e) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(e.getMessage());
        }
        progressBar.setVisibility(View.GONE);
    }
    /*
     * activity signup buttons onClicked finish.
     */

    /*
     * activity login buttons onClicked start
     */
    // click login buttom
    public void loginOnClicked(View v) {
        TextView useridView=(TextView)findViewById(R.id.netidEd);
        TextView passwordView=(TextView)findViewById(R.id.passEd);
        TextView errorView = (TextView) findViewById(R.id.loginError);

        String username = useridView.getText().toString();
        String password = passwordView.getText().toString();
        try {
            dbConnector.userLogin(username, password);
            errorView.setVisibility(View.INVISIBLE);
            setContentView(R.layout.activity_choosecamp);
        } catch(Exception e) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(e.getMessage());
        }
    }
    // click signup txt
    public void signUpClicked(View v) {
        setContentView(R.layout.activity_signup);
        RadioButton student = (RadioButton) findViewById(R.id.student);
        if (!student.isChecked()) {
            student.toggle();
        }
    }
    // go back login page
    public void backToLogin(View v){
        setContentView(R.layout.activity_login);
    }
    /*
     * activity choosecamp buttons onClicked from 132 to 141
     */

    public void selectBusch(View v) {
        moveToCampusView(new Campus("BUSCH", dbConnector));
    }
    public void selectCac(View v) {
        moveToCampusView(new Campus("COLLEGE AVENUE", dbConnector));
    }
    public void selectCook(View v) {
        moveToCampusView(new Campus("COOK", dbConnector));
    }
    public void selectLivingston(View v) {
        moveToCampusView(new Campus("LIVINGSTON", dbConnector));
    }
    // when move to activity.campus.xml
    private void moveToCampusView(Campus campus) {
        this.currentCampus = campus.getCampusName();
        setContentView(R.layout.activity_campus);
        LinearLayout allParkingLots = (LinearLayout) findViewById(R.id.ParkingLotsList);
        for(ParkingLot parkingLot: campus.getAllParkingLots()) {
            LinearLayout parent = (LinearLayout) parkingLot.getParent();
            if(parent != null) {
                parent.removeView(parkingLot);
            }
            allParkingLots.addView(parkingLot);
        }
    }


    //back to activity_choosecamp.xml
    public void backToCampusSelection(View v) {
        setContentView(R.layout.activity_choosecamp);
    }
    //back to login page
    public void logOut(View v) {
        setContentView(R.layout.activity_login);
    }

    /*
     * Appointment related
     */
    public void confirmTime(View v) {
        try{
            dbConnector.newAppointment(this.parkingLot);
            moveToAppointmentListView(v);
        } catch(Exception e) {
            ((TextView)findViewById(R.id.timeSlotError)).setText(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public void backToCampusView(View v) {
        moveToCampusView(new Campus(this.currentCampus, dbConnector));
    }

    public void setCurrentParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    /*
     * Appointment List View
     */
    //when click comfirm time
    public void moveToAppointmentListView(View v) {
        setContentView(R.layout.activity_appointmentlist);
        LinearLayout appointment_list = findViewById(R.id.appointment_list);
        for(DatabaseConnector.Appointment appointment: dbConnector.getAppointments()) {
            appointment.setTextColor(Color.BLACK);
            this.currentSelectedAppointment = null;
            LinearLayout parent = (LinearLayout) appointment.getParent();
            if(parent != null) {
                parent.removeView(appointment);
            }
            appointment_list.addView(appointment);
        }
    }
    //get current appointment
    public void setCurrentAppointment(DatabaseConnector.Appointment appointment) {
        for(DatabaseConnector.Appointment app: dbConnector.getAppointments()) {
            app.setTextColor(Color.BLACK);
        }
        this.currentSelectedAppointment = appointment;
    }
    // remove appointment
    public void removeAppointment(View v) {
        if(this.currentSelectedAppointment!=null) {
            this.dbConnector.removeAppointment(this.currentSelectedAppointment);
            LinearLayout appointment_list = findViewById(R.id.appointment_list);
            appointment_list.removeView(this.currentSelectedAppointment);
        }

    }
    //go to activity_information.xml
    public void movetoinfo(View v) {
        setContentView(R.layout.activity_information);
        ((TextView)findViewById(R.id.Netid)).setText(this.netId);
    }
    //when click update at inofrmation page
    public void clickUpdate(View v){
        TextView fullNameView = (TextView) findViewById(R.id.fullnameEd);
        TextView emailView = (TextView) findViewById(R.id.emailEd);
        TextView passwordView = (TextView) findViewById(R.id.passwordEd);

        String fullName = fullNameView.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        try {
            ((TextView)findViewById(R.id.errMsg)).setText("");
            dbConnector.userUpdate(this.netId,password,fullName,email);
            setContentView(R.layout.activity_choosecamp);
        } catch(Exception e) {
            ((TextView)findViewById(R.id.errMsg)).setText(e.getMessage());
        }
    }
    //cancel choosen time
    public void clearTimeSlotSelection(View v) {
        this.parkingLot.clearSelection();
    }
    //go back to activity_campus.xml
    public void backtolotselection(View v){moveToCampusView(new Campus(this.currentCampus, dbConnector));}
    // layout function
    public void viewlayout(View v){
        String campname = this.currentCampus;
        if (campname.equals("BUSCH")){
            setContentView(R.layout.activity_buschlayout);
        }else if (campname.equals("COLLEGE AVENUE")){
            setContentView(R.layout.caclayout);
        }else if (campname.equals("COOK")){
            setContentView(R.layout.cooklayout);
        }else{
            setContentView(R.layout.livilayout);
        }

        imageView = findViewById(R.id.image_view);
        zoomControls  =findViewById(R.id.zoom_control);
        //zoomControls.hide();

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomControls.show();
                return false;
            }
        });

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = imageView.getScaleX();
                float y = imageView.getScaleY();
                imageView.setScaleX((float) (x+1));
                imageView.setScaleY((float) (y+1));
                //zoomControls.hide();
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = imageView.getScaleX();
                float y = imageView.getScaleY();
                if (x==1&&y==1){
                    imageView.setScaleX(x);
                    imageView.setScaleY(y);
                }else{
                    imageView.setScaleX((float) (x-1));
                    imageView.setScaleY((float) (y-1));
                    //zoomControls.hide();
                }
            }
        });}
}
