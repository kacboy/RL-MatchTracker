package com.rlmatchtracker.interface2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class MatchListActivity extends AppCompatActivity {
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private ArrayList<String> mStats;
    MyDatabase db;
    private ImageView myImagePreview, comic;
    TextView credit, noData;

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
        setContentView(R.layout.activity_matchlist);
        setTitle("Matches");

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.bottomNavigationList);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavigationHome:
                        Intent a = new Intent(MatchListActivity.this,MenuActivity.class);
                        startActivity(a);
                        break;
                    case R.id.bottomNavigationList:
                        break;
                    case R.id.bottomNavigationChart:
                        Intent b = new Intent(MatchListActivity.this,StatsActivity.class);
                        startActivity(b);
                        break;
                }
                return false;
            }
        });

        //DATABASE
        db = new MyDatabase(this);

        //CAMERA STUFFS COMPRESSION PLUS DATA STORAGE
        myImagePreview = findViewById(R.id.scoreboardView);
        Intent intent = getIntent();
        String path = intent.getStringExtra("PATH");
        //get the time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy_HH:mm");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = formatter.format(timestamp);
        // give your image file url in mCurrentPhotoPath
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (path != null) {
            compress(path, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            db.insertData(photoString, 0, 3, 0, 0, 0, 0,0, 0, 0, time);

            //delete original photos
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        //RECYCLER VIEW
        mStats = db.getData(0, "");
        Log.d("debugMode", "mStats"+mStats.size());
        if (mStats.size()!=0) {
            comic = findViewById(R.id.comicView2);
            credit = findViewById(R.id.credit);
            noData = findViewById(R.id.emptyText);
            comic.setVisibility(View.GONE);
            credit.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        }
        adapter = new RecyclerViewAdapter(this, mStats);
        adapter.setStats(mStats);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //reverse order - newest on top
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        comic = findViewById(R.id.comicView2);
        //show a random comic
        Random r = new Random();
        int randomInt = r.nextInt(9 - 1) + 1;
        final int resourceId = getResources().getIdentifier("watercolor"+randomInt,"drawable", getPackageName());
        comic.setImageResource(resourceId);
    }

    //top bar filter button
    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.match_filter_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.pointsFilter) {
            //do something here
            categorize(6);
        } else if (id == R.id.goalsFilter) {
            categorize(4);
        } else if (id == R.id.defaultFilter){
            categorize(0);
        } else if (id == R.id.winFilter){
            categorize(2);
        } else if (id == R.id.modeFilter){
            categorize(3);
        }
        return super.onOptionsItemSelected(item);
    }

    public void manualInput(View view) {
        Intent intent= new Intent(this, ManualInputActivity.class);
        startActivity(intent);
    }

    public void categorize(int var){
        //sort by different databse columns
        if (var == 0||var == 4||var==2) {
            mStats = db.getData(var," ASC");
        } else {
            mStats = db.getData(var, " DESC");
        }
        adapter.setStats(mStats);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }

    public Bitmap compress(String path, ByteArrayOutputStream byteArrayOutputStream) {
        //decode the image
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        // In case you want to compress your image, default was 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        return bitmap;
    }
}
