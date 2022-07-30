package com.laxco.livescorev1.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.laxco.livescorev1.Introduction.PrefManager;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyPolicy extends AppCompatActivity {
    private Button mAgree,mCancel;
    private PrefManager prefManager;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_policy2);

//        from = getIntent().getExtras().getInt("from");
        //ADS

        loadInterstitial();

        prefManager = new PrefManager(this);

        mCancel = findViewById(R.id.btn_cancel);
        mAgree = findViewById(R.id.btn_i_agree);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/privacy-policy.html");


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        mAgree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (mInterstitialAd != null) {
//                    // Show the ad
//                    mInterstitialAd.show(MyPolicy.this);
//                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                        @Override
//                        public void onAdDismissedFullScreenContent() {
//                            Log.d("TAG", "The ad was dismissed.");
//                            prefManager.setTerms(true);
//                            if (from == 0){
//                                finish();
//                                startActivity(new Intent(MyPolicy.this,SignIn.class));
//                            }else  if (from == 2){
////                                startActivity(new Intent(MyPolicy.this, DataTermsFaqs.class).putExtra("position", 0)
////                                        .putExtra("data", getData(0).toString()).putExtra("from",2));
//                            }else {
//                                onBackPressed();
//                            }
//                        }
//
//                        @Override
//                        public void onAdShowedFullScreenContent() {
//                            mInterstitialAd = null;
//                            loadInterstitial();
//                            Log.d("TAG", "The ad was shown.");
//                        }
//                    });
//                } else {
//                    prefManager.setTerms(true);
//                    if (from == 0){
//                        finish();
//                     //   startActivity(new Intent(MyPolicy.this,SignIn.class));
//                    }else  if (from == 2){
////                       // startActivity(new Intent(MyPolicy.this, DataTermsFaqs.class).putExtra("position", 0)
////                                .putExtra("data", getData(0).toString()).putExtra("from",2));
//                    }else {
//                        onBackPressed();
//                    }
//                }
//            }
//        });


    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getResources().getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }
        });
    }


//    private JSONArray getData(int i) {
//        try {
//
//            String jsonDataString = readJSONDataFromFile();
//
//            JSONArray jsonArray = new JSONArray(jsonDataString);
//
//            JSONObject itemObj = jsonArray.getJSONObject(i);
//
//            return itemObj.getJSONArray("data");
//
//        } catch (IOException | JSONException e) {
//            return null;
//        }
//
//    }
//
//    private String readJSONDataFromFile() throws IOException {
//
//        InputStream inputStream = null;
//        StringBuilder builder = new StringBuilder();
//
//        try {
//
//            String jsonString = null;
//            inputStream = getResources().openRawResource(R.raw.data);
//            BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(inputStream, "UTF-8"));
//
//            while ((jsonString = bufferedReader.readLine()) != null) {
//                builder.append(jsonString);
//            }
//
//        } finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        }
//        return new String(builder);
//    }
}