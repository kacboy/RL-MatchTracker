package com.rlmatchtracker.interface2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoModeActivity extends AppCompatActivity implements SensorEventListener {
    public static SensorManager mySensorManager;
    Sensor accelSensor;
    boolean opened;
    Integer theme, countdown;
    TextView autoText1, autoText2;
    ImageView circle;
    MyDatabase db;

    //camera stuff
    private static final int CAMERA_CAPTURE_IMAGE_THUMB = 1;
    private static final int CAMERA_CAPTURE_IMAGE_NEW = 2;
    private static final int CAMERA_CAPTURE_IMAGE_PREVIEW = 3;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme
        //https://www.javarticles.com/2015/04/android-set-theme-dynamically.html
        SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        theme = sharedPrefs.getInt("theme", 0);
        if (theme.equals(1)) {
            setTheme(R.style.AltTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_auto_mode);
        setTitle("Auto Mode");

        //set default text properties and circle theme cause contentView needs to be inflated first
        autoText1 = findViewById(R.id.autoText1);
        autoText2 = findViewById(R.id.autoText2);
        circle = findViewById(R.id.circleView);
        autoText1.setTypeface(autoText1.getTypeface(), Typeface.BOLD);
        autoText1.setTextColor(Color.parseColor("#2A2A2A"));
        autoText2.setTypeface(null, Typeface.NORMAL);
        autoText2.setTextColor(Color.parseColor("#aaaaaa"));
        if (theme.equals(1)) {
            circle.setImageResource(R.drawable.auto_o1);
        } else {
            circle.setImageResource(R.drawable.auto_b1);
        }

        //keep device awake on this screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //sensor stuff
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //load database
        db = new MyDatabase(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //reset to step 1
        autoText1.setTypeface(autoText1.getTypeface(), Typeface.BOLD);
        autoText1.setTextColor(Color.parseColor("#2A2A2A"));
        autoText2.setTypeface(null, Typeface.NORMAL);
        autoText2.setTextColor(Color.parseColor("#aaaaaa"));
        if (theme.equals(1)) {
            circle.setImageResource(R.drawable.auto_o1);
        } else {
            circle.setImageResource(R.drawable.auto_b1);
        }
        //reset the opened check
        opened = true;
        countdown=1;
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                autoText1.setText("1. Put device down face-up in " + millisUntilFinished/1000 + " seconds and begin your match in-game");
            }
            public void onFinish() {
                autoText1.setText("1. Put device down face-up and begin your match in-game");
                countdown=0;
            }
        }.start();

        //start the sensor
        mySensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        //turn off sensors
        mySensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //get specific sensor value
        int type = event.sensor.getType();

        //get specific sensor value
        if (type == Sensor.TYPE_ACCELEROMETER) {
            //vibration + device movement logic
            float accelX = event.values[0];
            float accelY = event.values[1];
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (accelX <= (float)0.5 && accelX >= (float)-0.5 && accelY <= (float)0.5 && accelY >= (float)-0.5){
                //if statements depending to check first time
                if (opened == true && countdown <=0) {
                    Toast.makeText(this, "Device flat - game time!", Toast.LENGTH_SHORT).show();
                    if (v.hasVibrator() == true) {
                        v.vibrate(500);
                    }
                    autoText1.setText("1. Put device down face-up and begin your match in-game");
                    autoText1.setTypeface(null, Typeface.NORMAL);
                    autoText1.setTextColor(Color.parseColor("#aaaaaa"));
                    autoText2.setTypeface(autoText2.getTypeface(), Typeface.BOLD);
                    autoText2.setTextColor(Color.parseColor("#2A2A2A"));
                    if (theme.equals(1)) {
                        circle.setImageResource(R.drawable.auto_o2);
                    } else {
                        circle.setImageResource(R.drawable.auto_b2);
                    }
                    opened = false;
                }
            } else if (opened == false){
                //open camera
              Toast.makeText(this, "Activating camera", Toast.LENGTH_SHORT).show();
              opened = true;
              dispatchTakePictureIntent(CAMERA_CAPTURE_IMAGE_NEW);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
                    break;

                case CAMERA_CAPTURE_IMAGE_NEW:
                    Toast.makeText(this, "Scoreboard saved", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, AutoModeActivity.class);

                    //CAMERA STUFFS COMPRESSION PLUS DATA STORAGE
                    //get the time
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy_HH:mm");
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String time = formatter.format(timestamp);
                    // give your image file url in mCurrentPhotoPath
                    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (mCurrentPhotoPath != null) {
                        compress(mCurrentPhotoPath, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        db.insertData(photoString, 0, 3, 0, 0, 0, 0, 0, 0, 0, time);

                        //delete original photos
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File[] files = storageDir.listFiles();
                        if (files != null) {
                            for (File f : files) {
                                f.delete();
                            }
                        }
                    }

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

    public Bitmap compress(String path, ByteArrayOutputStream byteArrayOutputStream) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        // In case you want to compress your image, default was 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        return bitmap;
    }
}
