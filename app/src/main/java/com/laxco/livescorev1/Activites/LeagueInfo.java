package com.laxco.livescorev1.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.laxco.livescorev1.Fragments.LeagueFrags.Matches;
import com.laxco.livescorev1.Fragments.LeagueFrags.MyFragmentAdapter;
import com.laxco.livescorev1.Fragments.LeagueFrags.PlayerStats;
import com.laxco.livescorev1.Fragments.LeagueFrags.Tables;
import com.laxco.livescorev1.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeagueInfo extends AppCompatActivity {
    private String id, name, country, type, logo,season;
    private boolean isStandings = false, isPlayerStats = false;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private List<Fragment> fragments;
    private MyFragmentAdapter myFragmentAdapter;
    private TextView mName, mType, mCountry;
    private AdView mAdView;
    private ImageView mLogo;
    private CardView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_info);

        fragments = new ArrayList<>();


        loadBunnerAd();

        isPlayerStats = getIntent().getExtras().getBoolean("isPlayerStats");
        isStandings = getIntent().getExtras().getBoolean("isStandings");
        id = getIntent().getExtras().getString("id");
        logo = getIntent().getExtras().getString("logo");
        country = getIntent().getExtras().getString("country");
        type = getIntent().getExtras().getString("type");
        name = getIntent().getExtras().getString("name");
        season = getIntent().getExtras().getString("season");

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        mName = findViewById(R.id.tv_league_name);
        mType = findViewById(R.id.tv_league_type);
        mCountry = findViewById(R.id.tv_league_country);
        mLogo = findViewById(R.id.image_league_logo);
        mBack = findViewById(R.id.card_back);

        //settingheader
        settingHeader(country, name, logo, type);
        //loading tabs
        addingTabs(tabLayout);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    public String getSeason(){
        return season;
    }
    public String getId(){
        return id;
    }

    private void addingTabs(TabLayout tabLayout) {
        //loading frags
        if (isStandings && !isPlayerStats) {
            fragments.add(new Matches());
            fragments.add(new Tables());
            ///adding tabs
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            //setting title
            tabLayout.getTabAt(0).setText(title(0));
            tabLayout.getTabAt(1).setText(title(1));
        }

        if (isPlayerStats && !isStandings) {
            fragments.add(new Matches());
            fragments.add(new PlayerStats());
            ///adding tabs
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            //setting title
            tabLayout.getTabAt(0).setText(title(0));
            tabLayout.getTabAt(2).setText(title(2));
        }
        if (isPlayerStats && isStandings) {
            fragments.add(new Matches());
            fragments.add(new Tables());
            fragments.add(new PlayerStats());
            ///adding tabs
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            //setting title
            tabLayout.getTabAt(0).setText(title(0));
            tabLayout.getTabAt(1).setText(title(1));
            tabLayout.getTabAt(2).setText(title(2));

        }

        if (!isStandings && !isPlayerStats) {
            fragments.add(new Matches());
            ///adding tabs
            tabLayout.addTab(tabLayout.newTab());
            //setting title
            tabLayout.getTabAt(0).setText(title(0));
        }
        //pagerAdapter
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(myFragmentAdapter);
        viewPager.setOffscreenPageLimit(4);

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
    }

    private String title(int tabPositon) {
        switch (tabPositon) {
            case 0:
                return "Matches";
            case 1:
                return "Tables";
            case 2:
            default:
                return "Player stats";
        }
    }




    private void loadBunnerAd() {
        mAdView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    private void settingHeader(String country, String name, String logo, String type) {
        mCountry.setText("  ." + country);
        mName.setText(name);
        mType.setText(type);
        //league
        Glide.with(getApplicationContext()).load(logo)
                .apply(new RequestOptions()
                        .fitCenter()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLogo);
    }
}