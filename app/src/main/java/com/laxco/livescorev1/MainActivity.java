package com.laxco.livescorev1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.laxco.livescorev1.Introduction.PrefManager;
import com.laxco.livescorev1.Introduction.WelcomeActivity;

public class MainActivity extends AppCompatActivity {
    private PrefManager prefManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getMainTheme();
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);

        _loadUI();
    }

    private  void  _loadUI(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (prefManager.isFirstTimeLaunch()){
                    //load dashboard
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    MainActivity.this.finish();

                }else {
                    //load dashboard
                    startActivity(new Intent(MainActivity.this,Dashboard.class));
                    MainActivity.this.finish();
                }
            }
        },3000);
    }

//    private void getMainTheme(){
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        int type = preferences.getInt("type", -1);
//
//        if (type == 0) {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_NO);
//
//        } else if (type == 1) {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_YES);
//
//        } else if (type == 2) {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//
//        } else {
//            AppCompatDelegate.setDefaultNightMode(
//                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
//
//        }
//    }
}