package com.rlmatchtracker.interface2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameInput;
    Spinner platformSelect;
    ImageView comic;

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
        setContentView(R.layout.activity_register);
        setTitle("Register");

        usernameInput = findViewById(R.id.usernameInput);
        platformSelect = findViewById(R.id.platformSelect);
        comic = findViewById(R.id.comicView);
        //show a random comic
        Random r = new Random();
        int randomInt = r.nextInt(9 - 1) + 1;
        final int resourceId = getResources().getIdentifier("watercolor"+randomInt,"drawable", getPackageName());
        comic.setImageResource(resourceId);
    }

    public void registerUser(View view){
        SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (usernameInput.getText().toString().contains(" ")) {
            editor.putInt("platform", platformSelect.getSelectedItemPosition());
//            editor.putInt("theme", 0);
            editor.commit();

            Toast.makeText(this, "Username cannot contain spaces", Toast.LENGTH_LONG).show();
        } else {
            editor.putString("username", usernameInput.getText().toString());
            editor.putInt("platform", platformSelect.getSelectedItemPosition());
//            editor.putInt("theme", 0);
            editor.commit();

            Toast.makeText(this, "User data saved", Toast.LENGTH_LONG).show();

            Intent intent= new Intent(this, MenuActivity.class);
            startActivity(intent);
        }
    }
}
