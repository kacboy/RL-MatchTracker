package com.rlmatchtracker.interface2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MatchViewActivity extends AppCompatActivity {
    private ArrayList<String> mStats;
    MyDatabase db;
    int index;
    TextView pointsView, goalsView, assistsView, scoreView, resultView, modeView, shotsView, savesView, timeView;

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
        setContentView(R.layout.activity_match_view);

        //get info for current match
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("currentMatchIndex",0);
        db = new MyDatabase(this);
        mStats = db.getData(0, "");
        String[] results = mStats.get(index).split(" ");;


        //find related layout items
        modeView = findViewById(R.id.modeView);
        resultView = findViewById(R.id.resultView);
        scoreView = findViewById(R.id.scoreView);
        pointsView = findViewById(R.id.pointsView);
        goalsView = findViewById(R.id.goalsView);
        assistsView = findViewById(R.id.assistsView);
        savesView = findViewById(R.id.savesView);
        shotsView = findViewById(R.id.shotsView);
        timeView = findViewById(R.id.timeView);

        //set text
        String winLossTest = "1";
        if (results[1].toLowerCase().contains(winLossTest.toLowerCase())) {
            resultView.setText("Win");
            resultView.setTextColor(Color.parseColor("#42B200"));
        } else {
            resultView.setText("Loss");
            resultView.setTextColor(Color.parseColor("#FF3232"));
        }
        if (results[Constants.ID_MODE].equals("0")) {
            modeView.setText("3v3 Standard");
            setTitle(results[Constants.ID_TIME].replace("_"," ")+" Standard");
        } else if (results[Constants.ID_MODE].equals("1")){
            modeView.setText("2v2 Doubles");
            setTitle(results[Constants.ID_TIME].replace("_"," ")+" Doubles");
        } else if (results[Constants.ID_MODE].equals("2")){
            modeView.setText("1v1 Duel");
            setTitle(results[Constants.ID_TIME].replace("_"," ")+" Duel");
        } 
        scoreView.setText(results[Constants.ID_TEAM]+"-"+results[Constants.ID_OPP]);
        pointsView.setText(results[Constants.ID_POINTS]);
        goalsView.setText(results[Constants.ID_GOALS]);
        assistsView.setText(results[Constants.ID_ASSISTS]);
        savesView.setText(results[Constants.ID_SAVES]);
        shotsView.setText(results[Constants.ID_SHOTS]);
        timeView.setText(results[Constants.ID_TIME].replace("_"," "));
    }

    //top bar delete button
    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.match_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editMatch) {
            //do something here
            Bundle extras = getIntent().getExtras();
            int index = extras.getInt("currentMatchIndex",0);
            Intent i = new Intent(this, MatchEditActivity.class);
            i.putExtra("PATH", index);
            startActivity(i);
        } else if (id == R.id.deleteMatch) {
            AlertDialog diaBox = AskOption();
            diaBox.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete Match")
                .setMessage("Are you sure you want to delete this match data?")
//                .setIcon(R.drawable.icon_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteMatch();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return myQuittingDialogBox;
    }
    public void deleteMatch() {
        String[] results = mStats.get(index).split(" ");
        Toast.makeText(this, "Match deleted", Toast.LENGTH_LONG).show();
        Log.d("DEBUG_TAG","MatchViewindex="+index);
        db.deleteRow(results[Constants.ID_INDEX]);
        Intent intent= new Intent(this, MatchListActivity.class);
        startActivity(intent);
    }
}
