package com.rlmatchtracker.interface2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import utils.CameraUtils;

public class ImageInputActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "toRecView";
    private String DEBUG_TAG = "MainActivity";
    String sWL, sTeam, sOpp, sPoints, sGoals, sAssists, sShots, sSaves;
    Integer WL, team, opp, goals;
    Spinner spinnerMode;
    EditText editTextTeam, editTextOpp, editTextPoints, editTextGoals, editTextAssists, editTextShots, editTextSaves;
    //Arraylist Stuff
    int index;
    MyDatabase db;
    private ArrayList<String> mStats;
    String[] results;

    //image preview
    private ImageView myImagePreview;

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
        setContentView(R.layout.activity_image_input);
        setTitle("Input New Match");

        //show close icon instead of back
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
        int index = extras.getInt("currentMatchIndex",0);
        db = new MyDatabase(this);
        mStats = db.getData(0,"");
        results = mStats.get(index).split(" ");;

        //CAMERA STUFFS
        myImagePreview = findViewById(R.id.scoreboardView);
        decodeBase64AndSetImage(results[Constants.ID_IMAGE], myImagePreview);
        Intent intent = getIntent();
        String path = intent.getStringExtra("PATH");
        Log.d(DEBUG_TAG, "PATH"+path);
        try {
            myImagePreview.setVisibility(View.VISIBLE);
            Log.d("new", path);
            final Bitmap bitmap = CameraUtils.scaleDownAndRotatePic(path);
            myImagePreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void dataInput(View view) {
        Log.d(DEBUG_TAG, "onClick: called");
        //get info for current match
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("currentMatchIndex", 0);
        db = new MyDatabase(this);
        mStats = db.getData(0,"");
        String[] results = mStats.get(index).split(" ");

        //TIME
        String time = results[Constants.ID_TIME];
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
        if (team > opp) {
            WL = 1;
//            sWL = Integer.toString(WL);
        } else if (team < opp) {
            WL = 2;
//            sWL = Integer.toString(WL);
        }

        //check if data is valid before applying changes
        if (sTeam.length()==0||sOpp.length()==0||sPoints.length()==0||sGoals.length()==0||sAssists.length()==0||sSaves.length()==0||sShots.length()==0||goals>team){
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
        } else if (team == opp) {
            Toast.makeText(this, "Invalid Score", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Log.d("DEBUG_TAG","ImageInputindex="+index);
            db.updateData(results[Constants.ID_INDEX], null, WL, mode, team, opp, points, goals, assists, shots, saves, time);
            Intent i = new Intent(this, MatchListActivity.class);
            startActivity(i);
        }
    }

    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {
        // Incase you're storing into aws or other places where we have extension stored in the starting.
        //https://stackoverflow.com/questions/41396194/how-to-convert-image-to-string-in-android
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }

    //top bar settings button
    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.delete_match_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.topNavigationTrash) {
            //do something here
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
                .setMessage("Are you sure you want to delete this match image?")
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
//        String[] results = mStats.get(index).split(" ");
        Toast.makeText(this, "Match deleted", Toast.LENGTH_LONG).show();
        Log.d("DEBUG_TAG","resultsID="+results[Constants.ID_INDEX]+" index="+index);
        db.deleteRow(results[Constants.ID_INDEX]);
        Intent intent= new Intent(this, MatchListActivity.class);
        startActivity(intent);
    }
}
