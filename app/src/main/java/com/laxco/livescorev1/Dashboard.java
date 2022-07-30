package com.laxco.livescorev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.tabs.TabLayout;
import com.laxco.livescorev1.Activites.Countries;
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.LeagueAdapter;
import com.laxco.livescorev1.Adapters.MatchAdapter;
import com.laxco.livescorev1.Adapters.MatchAdapterNext;
import com.laxco.livescorev1.Adapters.PagerAdapter;
import com.laxco.livescorev1.Adapters.StandingAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.League;
import com.laxco.livescorev1.Models.Match;
import com.laxco.livescorev1.Models.MatchNext;
import com.laxco.livescorev1.Models.Standing;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private CardView mThemes,mSearch;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private RadioGroup mRadioGroupMain;
    private RadioButton mLight, mDark, mSystem, mBattery;
    private CardView mTabCard;
    private AdView mAdView;

    private int[] tabIcons = {
            R.drawable.ic_sports_soccer_fill0_wght400_grad0_opsz48,
            R.drawable.lives
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getMainTheme();
        setContentView(R.layout.activity_dashboard);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int type = preferences.getInt("type", -1);


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        mThemes = findViewById(R.id.card_theme);
        mSearch = findViewById(R.id.card_search);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());


        loadBunnerAd();
        loadInterstitial();

        
        viewPager = findViewById(R.id.viewPager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), 1);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(7);



        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(4).setCustomView(R.layout.tab_icon);

        mTabCard = tabLayout.getTabAt(4).getCustomView().findViewById(R.id.card_status);
        mTabCard.setAnimation(AnimationUtils.loadAnimation(this,R.anim.bounce_anim));

        try {
            Objects.requireNonNull(tabLayout.getTabAt(1)).setText(title(1));
            Objects.requireNonNull(tabLayout.getTabAt(2)).setText(title(2));
            Objects.requireNonNull(tabLayout.getTabAt(3)).setText(title(3));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        viewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint({"NewApi", "ResourceType"})
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @SuppressLint("ResourceType")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



//        mThemes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mInterstitialAd != null) {
//                    // Show the ad
//                    mInterstitialAd.show(Dashboard.this);
//                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                        @Override
//                        public void onAdDismissedFullScreenContent() {
//                            Log.d("TAG", "The ad was dismissed.");
//                            showDialogThemes(type);
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
//                    showDialogThemes(type);
//                }
//            }
//        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show(Dashboard.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            startActivity(new Intent(Dashboard.this, Countries.class));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    startActivity(new Intent(Dashboard.this, Countries.class));
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        super.onDestroy();
    }

    private String title(int tabPositon) throws ParseException {
        switch (tabPositon) {
            case 0:
                return "Home";
            case 1:
                return getDatePrev(getDate());
            case 2:
                return "TODAY\n"+getDate();
            case 3:
                return getDateNext(getDate());
            default:
                return "Cal";
        }
    }


    private String getDate(){
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate.toUpperCase();
    }


    private String getDatePrev(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM");

        Date myDate = dateFormat.parse(date);
        Date oneDayBefore = new Date(myDate.getTime() - 2);

        String result = dateFormat.format(oneDayBefore);
        return result.toUpperCase();
    }


    private String getDateNext(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        //ading a day
        calendar.add(Calendar.DAY_OF_YEAR,1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String formattedDate = df.format(tomorrow);
        return formattedDate.toUpperCase();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() !=0){
            viewPager.setCurrentItem(0,true);
        }else {
            super.onBackPressed();
        }
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




    private void loadBunnerAd() {
        mAdView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}