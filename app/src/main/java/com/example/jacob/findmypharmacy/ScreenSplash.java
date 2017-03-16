package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class ScreenSplash extends Activity {

    private static int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_splash);

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
