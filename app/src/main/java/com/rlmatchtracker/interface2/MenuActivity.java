package com.rlmatchtracker.interface2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.Cartesian;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.ValueDataEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_THUMB = 1;
    private static final int CAMERA_CAPTURE_IMAGE_NEW = 2;
    private static final int CAMERA_CAPTURE_IMAGE_PREVIEW = 3;
    private String mCurrentPhotoPath;
    private ArrayList<String> mStats;
    TextView usernameWelcomeText;

    //charts
    String txtStandard, txtDoubles, txtDuel, txtNA;

    //db Stuff
    MyDatabase db;
    private ArrayList<String> stats;

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
        setContentView(R.layout.activity_menu);
//        setTitle("Home");

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.bottomNavigationHome);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavigationHome:
                        break;
                    case R.id.bottomNavigationList:
                        Intent a = new Intent(MenuActivity.this,MatchListActivity.class);
                        startActivity(a);
                        break;
                    case R.id.bottomNavigationChart:
                        Intent b = new Intent(MenuActivity.this,StatsActivity.class);
                        startActivity(b);
                        break;
                }
                return false;
            }
        });

        //welcome text
        usernameWelcomeText = findViewById(R.id.usernameWelcomeText);
        if (sharedPrefs.getInt("platform", 0)==0 && sharedPrefs.getString("username", "not available").length()>20) {
            usernameWelcomeText.setText("Welcome to RL Match Tracker!");
        } else {
            usernameWelcomeText.setText("Welcome, "+sharedPrefs.getString("username", "not available")+"!");
        }

        //db stuff
        db = new MyDatabase(this);
        stats = new ArrayList<String>();
        stats = db.getData(0, "");

        //chart
        valueA();
        Cartesian column = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Standard", Integer.parseInt(txtStandard)));
        data.add(new ValueDataEntry("Doubles", Integer.parseInt(txtDoubles)));
        data.add(new ValueDataEntry("Duel", Integer.parseInt(txtDuel)));
        data.add(new ValueDataEntry("Not Inputted", Integer.parseInt(txtNA)));
        column.setData(data);
        AnyChartView anyChartView = findViewById(R.id.column_chart_view);
        column.getTitle().setEnabled(true);
        column.getTitle().setText("Total Games");
        column.getBackground().fill("#f9f9f9", 100.);
        column.setPalette(new String[]{ "#0C88FC", "#0A6ECC", "#FC7C0C", "#CC640A"});
        anyChartView.setChart(column);
    }

    //firstrun check
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (sharedPrefs.getBoolean("firstrun", true)||sharedPrefs.getString("username", "not available")=="not available") {
            editor.putBoolean("firstrun", false).commit();
            Toast.makeText(this, "No credentials detected - please register", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        if(!isChangingConfigurations()) {
            deleteTempFiles(getCacheDir());
        }
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    public void manualInput(View view) {
        Intent intent= new Intent(this, ManualInputActivity.class);
        startActivity(intent);
    }
    public void autoMode(View view) {
        Intent intent= new Intent(this, AutoModeActivity.class);
        startActivity(intent);
    }

    //top bar settings button
    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.topNavigationSettings) {
            //do something here
            Intent intent= new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //camera
    public void takePicShowThumbnail(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(this, "start thumb", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_THUMB);
        }
    }
    public void takePicNewActivity(View view) {
        dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_NEW);
    }

    public void takePicPreview(View view) {
        dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_PREVIEW);
    }
    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case CAMERA_CAPTURE_IMAGE_THUMB:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//                    myImageThumb.setImageBitmap(imageBitmap);
                    break;

                case CAMERA_CAPTURE_IMAGE_NEW:
                    Toast.makeText(this, "Scoreboard saved", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, MatchListActivity.class);
                    i.putExtra("PATH", mCurrentPhotoPath);
                    startActivity(i);
                    break;

                case CAMERA_CAPTURE_IMAGE_PREVIEW:
//                    previewCapturedImage();
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(this, "User cancelled the capture", Toast.LENGTH_SHORT).show();
        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent(int requestCode) {
        //https://www.youtube.com/watch?v=9ZxRTKvtfnY&t=613s
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (callCameraApplicationIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("ex", "cannot create file");
            }
            // Continue only if the File was successfully created
            String authorities = getApplicationContext().getPackageName() + ".fileprovider";
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, authorities, photoFile);
                callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(callCameraApplicationIntent, requestCode);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void valueA(){

        mStats = db.getData(2,"");
        Cursor cursor = db.getCursor();
        int indexM = cursor.getColumnIndex(Constants.MODE);

        float tGames=0;

        for (int i = 0; i < mStats.size(); i++){
            tGames = tGames +1; //how many games there are in the database
        }

        //Mode
        cursor.moveToFirst();
        int standard=0, doubles=0, duel=0, NA=0;
        while (!cursor.isAfterLast()) {
            String sMode = cursor.getString(indexM);
            if (sMode.equals("0")) {
                standard += 1;
            } else if (sMode.equals("1")) {
                doubles += 1;
            } else if (sMode.equals("2")) {
                duel += 1;
            } else if (sMode.equals("3")) {
                NA += 1;
            }
            cursor.moveToNext();
        }
        txtStandard = String.valueOf(standard);
        txtDoubles = String.valueOf(doubles);
        txtDuel = String.valueOf(duel);
        txtNA = String.valueOf(NA);
    }
}