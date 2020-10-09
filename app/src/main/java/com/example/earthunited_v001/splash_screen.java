package com.example.earthunited_v001;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(splash_screen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(5000)
                .withBackgroundColor(Color.parseColor("#FFFFFF"));
//                .withHeaderText("Header")
//                .withFooterText("Footer")
//                .withBeforeLogoText("Before logo text")
//                .withAfterLogoText("after logo text")
//                .withLogo(R.mipmap.ic_launcher_round);
//
//        config.getHeaderTextView().setTextColor(Color.WHITE);
//        config.getFooterTextView().setTextColor(Color.WHITE);
//        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
//        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);

    }
}