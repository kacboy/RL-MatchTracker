package com.rlmatchtracker.interface2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ManualInputActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_MESSAGE = "toRecView";
    private String DEBUG_TAG = "MainActivity";
    String sWL, sTeam, sOpp, sPoints, sGoals, sAssists, sShots, sSaves;
    Integer WL, team, opp, goals;
    Spinner spinnerMode;
    EditText editTextTeam, editTextOpp, editTextPoints, editTextGoals, editTextAssists, editTextShots, editTextSaves;
    //Arraylist Stuff
    MyDatabase db;
    private ArrayList<String> stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme
        //https://www.javarticles.com/2015/04/android-set-theme-dynamically.html
        SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        Integer theme = sharedPrefs.getInt("theme", 0);
        if (theme.equals(1)) {
            setTheme(R.style.AltTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_manual_input);
        setTitle("Input New Match");

        //close icon instead of back
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_cross);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerMode = findViewById(R.id.spinnerMode);
        editTextTeam = findViewById(R.id.textViewTeam);
        editTextOpp = findViewById(R.id.textViewOpp);
        editTextPoints = findViewById(R.id.editTextPoints);
        editTextGoals = findViewById(R.id.editTextGoals);
        editTextAssists = findViewById(R.id.editTextAssists);
        editTextSaves = findViewById(R.id.editTextSaves);
        editTextShots = findViewById(R.id.editTextShots);

        db = new MyDatabase(this);
        stats = new ArrayList<String>();
        stats = db.getData(0,"");
    }

    @Override
    public void onClick(View v) {
        Log.d(DEBUG_TAG, "onClick: called");

        //TIME
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy_HH:mm");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = formatter.format(timestamp);
        sTeam = time;//String Val
        //MODE
        Integer mode = spinnerMode.getSelectedItemPosition();
        sTeam = Integer.toString(mode);//String Val
        //TEAM SCORE
        sTeam = editTextTeam.getText().toString();
        team = Integer.parseInt(sTeam);
        //OPP SCORE
        sOpp = editTextOpp.getText().toString();
        opp = Integer.parseInt(sOpp);
        //Points
        sPoints = editTextPoints.getText().toString();
        Integer points = Integer.parseInt(sPoints);
        //Goals
        sGoals = editTextGoals.getText().toString();
        goals = Integer.parseInt(sGoals);
        //Assists
        sAssists = editTextAssists.getText().toString();
        Integer assists = Integer.parseInt(sAssists);
        //Saves
        sSaves = editTextSaves.getText().toString();
        Integer saves = Integer.parseInt(sSaves);
        //Shots
        sShots = editTextShots.getText().toString();
        Integer shots = Integer.parseInt(sShots);
        //WinLoss
//        String WL = editTextWL.getText().toString();
        if (team > opp) {
            WL = 1;
        } else if (team < opp) {
            WL = 2;
        }

        //check if data is valid before applying changes
        if (sTeam.length()==0||sOpp.length()==0||sPoints.length()==0||sGoals.length()==0||sAssists.length()==0||sSaves.length()==0||sShots.length()==0||goals>team){
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
        } else if (team==opp) {
            Toast.makeText(this, "Invalid Score", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success"+time+timestamp, Toast.LENGTH_SHORT).show();
            db.insertData(null, WL, mode, team, opp, points, goals, assists, shots, saves, time);
            Intent i = new Intent (this, MatchListActivity.class);
            i.putExtra(EXTRA_MESSAGE, stats);
            startActivity(i);
        }
    }
}
