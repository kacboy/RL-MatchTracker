package com.rlmatchtracker.interface2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {
    String platformURL;

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
        setContentView(R.layout.activity_web);
        setTitle("Tracker Network");

        WebView view = (WebView) findViewById(R.id.webView);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        //interpret platform
        String username = sharedPrefs.getString("username", "not available");
        Integer platform = sharedPrefs.getInt("platform", 0);
        if (platform == 0) {
            platformURL = "steam";
        } else if (platform == 1) {
            platformURL = "ps";
        } else if (platform == 2) {
            platformURL = "xbox";
        }
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl("https://rocketleague.tracker.network/profile/" + platformURL + "/" + username);
    }

    //top bar settings button
    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.web_open_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.topNavigationWeb) {
            //do something here
            SharedPreferences sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            String username = sharedPrefs.getString("username", "not available");
            Integer platform = sharedPrefs.getInt("platform", 0);
            if (platform == 0) {
                platformURL = "steam";
            } else if (platform == 1) {
                platformURL = "ps";
            } else if (platform == 2) {
                platformURL = "xbox";
            }
            Uri webpage = Uri.parse("https://rocketleague.tracker.network/profile/" + platformURL + "/" + username);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(webIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
