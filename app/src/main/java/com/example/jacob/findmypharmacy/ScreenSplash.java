package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Locale;

public class ScreenSplash extends Activity {
    private String languageToLoad;
    private static int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_splash);

        SharedPreferences saved_sett = getSharedPreferences("UserSettings", 0);
        if(saved_sett.getString("LangString", "").equals("Romanian")){
            languageToLoad = "ro"; // your language
        }
        else if(saved_sett.getString("LangString", "").equals("Russian")){
            languageToLoad = "ru"; // your language
        }
        else if(saved_sett.getString("LangString", "").equals("Francais")){
            languageToLoad = "fr"; // your language
        }
        else if(saved_sett.getString("LangString", "").equals("English")){
            languageToLoad = "en"; // your language

        } else {
            languageToLoad = "en"; // your language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //Adding the Roboto Font Family
        TextView the_app_name = (TextView)findViewById(R.id.the_app_name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/RobotoSlab-Bold.ttf");
        the_app_name.setTypeface(custom_font);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ScreenSplash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
