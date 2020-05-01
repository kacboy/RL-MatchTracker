package com.rlmatchtracker.interface2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    TextView statWins, statPoints, statGoals, statAssists, statShots, statSaves;
    private ArrayList<String> mStats;
    MyDatabase db;
    Double txtWins, txtPoints, txtGoals, txtAssist, txtSaves, txtShots;
    Button webView;

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
        setContentView(R.layout.activity_stats);
        setTitle("Stats");

        //https://medium.com/@suragch/how-to-add-a-bottom-navigation-bar-in-android-958ed728ef6c
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        //https://stackoverflow.com/questions/40202294/set-selected-item-in-android-bottomnavigationview
        navigation.setSelectedItemId(R.id.bottomNavigationChart);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavigationHome:
                        Intent a = new Intent(StatsActivity.this,MenuActivity.class);
                        startActivity(a);
                        break;
                    case R.id.bottomNavigationList:
                        Intent b = new Intent(StatsActivity.this,MatchListActivity.class);
                        startActivity(b);
                        break;
                    case R.id.bottomNavigationChart:
                        break;
                }
                return false;
            }
        });

        //hide tracker network if switch player
        Integer platform = sharedPrefs.getInt("platform", 0);
        webView = findViewById(R.id.webButton);
        if (platform == 3) {
            webView.setVisibility(View.GONE);//makes it disappear
        }

        //get stats
        statWins = findViewById(R.id.statWins);
        statPoints = findViewById(R.id.statPoints);
        statGoals = findViewById(R.id.statGoals);
        statAssists = findViewById(R.id.statAssists);
        statSaves = findViewById(R.id.statSaves);
        statShots = findViewById(R.id.statShots);
        db = new MyDatabase(this);
        valueA();
        //set stats
        if (Double.isNaN(txtWins)) { //check if stats is empty
            statWins.setText("None");
            statPoints.setText("None");
            statGoals.setText("None");
            statAssists.setText("None");
            statSaves.setText("None");
            statShots.setText("None");
        } else { //if stats are not empty
            statWins.setText(String.format("%.2f", txtWins));
            statPoints.setText(String.format("%.2f", txtPoints));
            statGoals.setText(String.format("%.2f", txtGoals));
            statAssists.setText(String.format("%.2f", txtAssist));
            statSaves.setText(String.format("%.2f", txtSaves));
            statShots.setText(String.format("%.2f", txtShots));
        }

        //chart
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Avg Goals", txtGoals));
        data.add(new ValueDataEntry("Avg Assists", txtAssist));
        data.add(new ValueDataEntry("Avg Saves", txtSaves));
        data.add(new ValueDataEntry("Avg Shots", txtShots));
        pie.setData(data);
        AnyChartView anyChartView = findViewById(R.id.circle_chart_view);
        pie.getLegend().setItemsLayout("vertical");
        pie.getLegend().setPosition("right");
        pie.getLegend().setAlign("center");
        pie.setPalette(new String[]{ "#0C88FC", "#0A6ECC", "#FC7C0C", "#CC640A"});
        anyChartView.setChart(pie);
    }

    public void valueA(){

        mStats = db.getData(2,"");
        Cursor cursor = db.getCursor();
        int indexP = cursor.getColumnIndex(Constants.POINTS);
        int indexG = cursor.getColumnIndex(Constants.GOALS);
        int indexA = cursor.getColumnIndex(Constants.ASSISTS);
        int indexS = cursor.getColumnIndex(Constants.SAVES);
        int indexH = cursor.getColumnIndex(Constants.SHOTS);
        int indexR = cursor.getColumnIndex(Constants.WL);

        float tPoints = 0, tGoals = 0, tSaves = 0, tAssist = 0, tShots = 0;
        float aWinRatio = 0, aPoints = 0, aGoals = 0, aSaves = 0, aAssist = 0, aShots = 0;
        float tGames=0;


        for (int i = 0; i < mStats.size(); i++){
            tGames = tGames +1; //how many games there are in the database
        }

        //Points
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {//selects every point value in the database
            String sPoint = cursor.getString(indexP);
            int iPoints = Integer.parseInt(sPoint);
            tPoints += iPoints; //adds to the total
            cursor.moveToNext();//goes to the next row in the table
        }
        aPoints = tPoints / tGames;
        txtPoints = Double.valueOf(aPoints);

        //Goals
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String sGoals = cursor.getString(indexG);
            int iGoals = Integer.parseInt(sGoals);
            tGoals += iGoals;
            cursor.moveToNext();
        }
        aGoals = tGoals / tGames;
        txtGoals = Double.valueOf(aGoals);

        //Assists
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String sAssist = cursor.getString(indexA);
            int iAssist = Integer.parseInt(sAssist);
            tAssist += iAssist;
            cursor.moveToNext();
        }
        aAssist = tAssist / tGames;
        txtAssist = Double.valueOf(aAssist);

        //Saves
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String sSaves = cursor.getString(indexS);
            int iSaves = Integer.parseInt(sSaves);
            tSaves += iSaves;
            cursor.moveToNext();
        }
        aSaves = tSaves / tGames;
        txtSaves = Double.valueOf(aSaves);

        //Shots
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String sSaves = cursor.getString(indexH);
            int iShots = Integer.parseInt(sSaves);
            tShots += iShots;
            cursor.moveToNext();
        }
        aShots = tShots / tGames;
        txtShots = Double.valueOf(aShots);

        //WinLoss
        cursor.moveToFirst();
        int iWins=0, iLoss=0;
        while (!cursor.isAfterLast()) {
            String sWL = cursor.getString(indexR);
            if (sWL.equals("W")) {
                iWins += 1;
            } else if (sWL.equals("L")) {
                iLoss += 1;
            }
            cursor.moveToNext();
        }
        aWinRatio = iWins / tGames;
        txtWins = Double.valueOf(aWinRatio);
    }

    public void openWebView(View view){
        Intent intent= new Intent(this, WebActivity.class);
        startActivity(intent);
    }
}
