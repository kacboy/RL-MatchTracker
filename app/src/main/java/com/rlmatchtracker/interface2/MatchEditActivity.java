package com.rlmatchtracker.interface2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MatchEditActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_MESSAGE = "toRecView";
    private String DEBUG_TAG = "MainActivity";
    float inpMode,inpTeam,inpOpp,inpPoints,inpGoals,inpAssists,inpSaves,inpShot;
    String sWL, sTeam, sOpp, sPoints, sGoals, sAssists, sShots, sSaves;
    Integer WL;
    Spinner spinnerMode;
    EditText editTextTeam, editTextOpp, editTextPoints, editTextGoals, editTextAssists, editTextShots, editTextSaves;
    //Arraylist Stuff
    MyDatabase db;
    int index;
    private ArrayList<String> mStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //https://www.javarticles.com/2015/04/android-set-theme-dynamically.html
        SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        Integer theme = sharedPrefs.getInt("theme", 0);
        if (theme.equals(1)) {
            setTheme(R.style.AltTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_match_edit);
        setTitle("Edit Match");

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

        //get info for current match
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("PATH",0);
        db = new MyDatabase(this);
        mStats = db.getData(0, "");
        String[] results = mStats.get(index).split(" ");;

        //set text
        if (results[Constants.ID_MODE].equals("0")) {
            spinnerMode.setSelection(0);
        } else if (results[Constants.ID_MODE].equals("1")){
            spinnerMode.setSelection(1);
        } else {
            spinnerMode.setSelection(2);
        }
        editTextTeam.setText(results[Constants.ID_TEAM]);
        editTextOpp.setText(results[Constants.ID_OPP]);
        editTextPoints.setText(results[Constants.ID_POINTS]);
        editTextGoals.setText(results[Constants.ID_GOALS]);
        editTextAssists.setText(results[Constants.ID_ASSISTS]);
        editTextSaves.setText(results[Constants.ID_SAVES]);
        editTextShots.setText(results[Constants.ID_SHOTS]);
    }

    @Override
    public void onClick(View v) {
        Log.d(DEBUG_TAG, "onClick: called");
        //get info for current match
        db = new MyDatabase(this);
        mStats = db.getData(0, "");
        String[] results = mStats.get(index).split(" ");

        //TIME
        String time = results[Constants.ID_TIME];
        sTeam = time;//String Val
        //MODE
        Integer mode = spinnerMode.getSelectedItemPosition();
        sTeam = Integer.toString(mode);//String Val
        //TEAM SCORE
        sTeam = editTextTeam.getText().toString();
        Integer team = Integer.parseInt(sTeam);
        //OPP SCORE
        sOpp = editTextOpp.getText().toString();
        Integer opp = Integer.parseInt(sOpp);
        //Points
        sPoints = editTextPoints.getText().toString();
        Integer points = Integer.parseInt(sPoints);
        //Goals
        sGoals = editTextGoals.getText().toString();
        Integer goals = Integer.parseInt(sGoals);
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
        if (inpTeam > inpOpp) {
            WL = 1;
            sWL = Integer.toString(WL);
        } else if (inpTeam < inpOpp) {
            WL = 2;
            sWL = Integer.toString(WL);
        }

        //...
        if (sTeam.length()==0||sOpp.length()==0||sPoints.length()==0||sGoals.length()==0||sAssists.length()==0||sSaves.length()==0||sShots.length()==0){
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
        } else if (inpTeam == inpOpp) {
            Toast.makeText(this, "Invalid Score", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success - "+index, Toast.LENGTH_SHORT).show();
            Log.d("DEBUG_TAG","MatchEditindex="+index+", db="+db);
            db.updateData(results[Constants.ID_INDEX],null, WL, mode, team, opp, points, goals, assists, shots, saves, time);
            Intent i = new Intent(this, MatchListActivity.class);
            startActivity(i);
        }
    }
}
