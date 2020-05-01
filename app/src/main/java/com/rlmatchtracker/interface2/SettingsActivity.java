package com.rlmatchtracker.interface2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    EditText usernameInput;
    TextView versionText;
    Spinner platformSelect, themeSelect;

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
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        String username = sharedPrefs.getString("username", "not available");
        Integer platform = sharedPrefs.getInt("platform", 0);

        usernameInput = findViewById(R.id.usernameInput);
        usernameInput.setText(username);

        platformSelect = findViewById(R.id.platformSelect);
        platformSelect.setSelection(platform);

        themeSelect = findViewById(R.id.themeSelect);
        themeSelect.setSelection(theme);

        versionText = findViewById(R.id.versionText);
        versionText.setText("Rocket League Match Tracker v0."+Constants.DATABASE_VERSION+"\nCreated by Marcus Blackstock & Seongchan Yoo");
    }

    public void updateSettings(View view){
        SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (usernameInput.getText().toString().contains(" ")) {
            editor.putInt("platform", platformSelect.getSelectedItemPosition());
            editor.putInt("theme", themeSelect.getSelectedItemPosition());
            editor.commit();

            Toast.makeText(this, "Username cannot contain spaces", Toast.LENGTH_LONG).show();
        } else {
            editor.putString("username", usernameInput.getText().toString());
            editor.putInt("platform", platformSelect.getSelectedItemPosition());
            editor.putInt("theme", themeSelect.getSelectedItemPosition());
            editor.commit();

            Toast.makeText(this, "User data updated", Toast.LENGTH_LONG).show();

            Intent intent= new Intent(this, MenuActivity.class);
            startActivity(intent);
        }
    }

    public void deletePopup(View view) {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }
    public void deleteDatabase() {
        Toast.makeText(this, "Database deleted successfully", Toast.LENGTH_LONG).show();
        MyDatabase db = new MyDatabase(this);
        db.resetTable();
    }
    public void compressDatabase(View view) {
        Toast.makeText(this, "Database compressed successfully", Toast.LENGTH_LONG).show();
        MyDatabase db = new MyDatabase(this);
        db.compressTable();
    }


    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete Database")
                .setMessage("Are you sure you want to delete all match data?")
//                .setIcon(R.drawable.icon_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteDatabase();
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
}
