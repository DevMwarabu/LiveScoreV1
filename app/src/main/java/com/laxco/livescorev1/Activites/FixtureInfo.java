package com.laxco.livescorev1.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.BuildConfig;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Fragments.FixtureInformation.H2H;
import com.laxco.livescorev1.Fragments.FixtureInformation.Summary;
import com.laxco.livescorev1.Fragments.LeagueFrags.MyFragmentAdapter;
import com.laxco.livescorev1.Fragments.FixtureInformation.LiveInfo;
import com.laxco.livescorev1.Fragments.FixtureInformation.LineUps;
import com.laxco.livescorev1.Fragments.FixtureInformation.Stats;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class FixtureInfo extends AppCompatActivity {
    private String season,referee,f_venue,leagueName,leagueType,leagueUrl,homeTeam,awayTeam;
    private int id;
    private TextView mHome, mAway, mElapse, mScoreHome, mScoreAway, mScoreHomeSecond, mScoreAwaySecond, mHomeScorer, mAwayScorer,mTime,mDate;
    private ImageView mLogoHome, mLogoAway, mLogoHomeSecond, mLogoAwaySecond;
    private CardView mBack;
    private LinearLayout mLinearLayoutScores,mLinearLayoutScoresMain,mLinearLayoutTimeMain;
    private View mViewStatus;
    private String TAG = BuildConfig.APPLICATION_ID;
    private JSONObject teams;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private View mView;
    private List<Fragment> fragments;
    private MyFragmentAdapter myFragmentAdapter;
    public static String fixture_id, season_lg;
    public static Context context;
    private Timer timer = new Timer();
    private AdView mAdView;
    private boolean isLoaded = false;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String url;
    private Gson gson;
    public static String homeId,awayId;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture_info);

        id = getIntent().getExtras().getInt("id");

        fragments = new ArrayList<>();
        timer = new Timer();

        url = Constants.baseUrl + "fixtures?id=" + String.valueOf(id);

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .build();

        request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        gson = new Gson();

        initViews();
        //loading
        getData();
        //recall();

    }

    private void recall(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        },  60000);//wait 0 ms before doing the action and do it evry 1000ms (1second)

        timer.cancel();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        mHomeScorer = findViewById(R.id.tv_home_scorers);
        mAwayScorer = findViewById(R.id.tv_away_scorers);
        mLogoAwaySecond = findViewById(R.id.image_away_logo_second);
        mLogoHomeSecond = findViewById(R.id.image_home_logo_second);
        mLogoAway = findViewById(R.id.image_away_logo);
        mLogoHome = findViewById(R.id.image_home_logo);
        mHome = findViewById(R.id.tv_home_team);
        mAway = findViewById(R.id.tv_away_team);
        mElapse = findViewById(R.id.tv_time_elapsed);
        mLinearLayoutScores = findViewById(R.id.linear_scores);
        mScoreHomeSecond = findViewById(R.id.tv_home_score_second);
        mScoreHome = findViewById(R.id.tv_home_score);
        mScoreAway = findViewById(R.id.tv_away_score);
        mScoreAwaySecond = findViewById(R.id.tv_away_score_second);
        mView = findViewById(R.id.view_status);
        mBack = findViewById(R.id.card_back);
        mLinearLayoutScoresMain = findViewById(R.id.linear_scores_main);
        mLinearLayoutTimeMain = findViewById(R.id.linear_time_main);
        mTime = findViewById(R.id.tv_time_kick_off);
        mDate = findViewById(R.id.tv_date_kick_off);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void addingTabs(TabLayout tabLayout) {
        fragments.add(new LiveInfo());
        fragments.add(new Summary());
        fragments.add(new Stats());
        fragments.add(new LineUps());
        fragments.add(new H2H());
        ///adding tabs
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        //setting title
        tabLayout.getTabAt(0).setText(title(0));
        tabLayout.getTabAt(1).setText(title(1));
        tabLayout.getTabAt(2).setText(title(2));
        tabLayout.getTabAt(3).setText(title(3));
        tabLayout.getTabAt(4).setText(title(4));

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
                return "Info";
            case 1:
                return "Summary";
            case 2:
                return "Stats";
            case 3:
                return "Line-Ups";
            default:
                return "H2H";
        }
    }

    private void getData() {
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                        //usimgUIThread
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String responseData = response.body().string();
                                        JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");
                                        if (responseArray.length()>0){

                                            JSONObject jsonObject = responseArray.getJSONObject(0);

                                            String homeName = jsonObject.getJSONObject("teams").getJSONObject("home").getString("name");
                                            String awayName = jsonObject.getJSONObject("teams").getJSONObject("away").getString("name");
                                            String homeScore = jsonObject.getJSONObject("goals").getString("home").equals("null") ? "0" : jsonObject.getJSONObject("goals").getString("home");
                                            String awayScore = jsonObject.getJSONObject("goals").getString("away").equals("null") ? "0" : jsonObject.getJSONObject("goals").getString("away");
                                            String home_url = jsonObject.getJSONObject("teams").getJSONObject("home").getString("logo");
                                            String away_url = jsonObject.getJSONObject("teams").getJSONObject("away").getString("logo");
                                            JSONArray events = jsonObject.getJSONArray("events");
                                            String shortStatus = jsonObject.getJSONObject("fixture").getJSONObject("status").getString("short");
                                            String elapsed = jsonObject.getJSONObject("fixture").getJSONObject("status").getString("elapsed");
                                            homeId = jsonObject.getJSONObject("teams").getJSONObject("home").getString("id");
                                            awayId = jsonObject.getJSONObject("teams").getJSONObject("away").getString("id");
                                            String league = jsonObject.getJSONObject("league").getString("name");
                                            String ref = jsonObject.getJSONObject("fixture").getString("referee");
                                            String date = jsonObject.getJSONObject("fixture").getString("date");
                                            String venue = jsonObject.getJSONObject("fixture").getJSONObject("venue").getString("name");
                                            String leagueLogo = jsonObject.getJSONObject("league").getString("logo");
                                            String leaguetype = jsonObject.getJSONObject("league").getString("round");
                                            String s = jsonObject.getJSONObject("league").getString("season");
                                            //loading data
                                            setData(homeName, awayName, homeScore, awayScore, home_url, away_url, events, shortStatus, elapsed, homeId,league,ref,venue,leagueLogo,leaguetype,s,date);

                                            //loading tabs
                                            addingTabs(tabLayout);
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });

    }

    private void setData(String homeName, String awayName, String homeScore, String awayScore, String home_url, String away_url, JSONArray events, String shortStatus,
                         String elapsed, String home_id,String league,String ref,String venue,String leagueLogo,String leaguetype,String s,String date) {

        homeTeam = homeName;
        awayTeam = awayName;
        leagueName = league;
        season = s;
        referee = ref;
        f_venue = venue;
        leagueUrl= leagueLogo;
        leagueType = leaguetype;


        mHome.setText(homeName.equals("null")?"-":homeName);
        mScoreHome.setText(homeScore.equals("null")?"-":homeScore);
        mAway.setText(awayName.equals("null")?"-":awayName);
        mScoreAway.setText(awayScore.equals("null")?"-":awayScore);

        mScoreHomeSecond.setText(homeScore.equals("null")?"-":homeScore);
        mScoreAwaySecond.setText(awayScore.equals("null")?"-":awayScore);
        //setting images
        //home
        Glide.with(getApplicationContext()).load(away_url)
                .apply(new RequestOptions()
                        .fitCenter()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLogoAway);
        //away
        Glide.with(getApplicationContext()).load(home_url)
                .apply(new RequestOptions()
                        .fitCenter()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLogoHome);
        //home
        Glide.with(getApplicationContext()).load(away_url)
                .apply(new RequestOptions()
                        .fitCenter()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLogoAwaySecond);
        //away
        Glide.with(getApplicationContext()).load(home_url)
                .apply(new RequestOptions()
                        .fitCenter()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLogoHomeSecond);



        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Date result;
        try {
            result = df.parse(date);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
            SimpleDateFormat sdf_tim = new SimpleDateFormat("hh:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC +3"));
            sdf_tim.setTimeZone(TimeZone.getTimeZone("UTC +3"));
            mDate.setText(sdf.format(result));
            mTime.setText(sdf_tim.format(result));
        } catch (Exception e) {

        }

        if (shortStatus.equals("FT")) {
            mElapse.setText("Full Time");
            mElapse.setTextColor(getResources().getColor(io.supercharge.shimmerlayout.R.color.shimmer_color));
            mLinearLayoutScoresMain.setVisibility(View.VISIBLE);

        } else {
            if (!shortStatus.equals("NS")) {
                mView.setVisibility(View.VISIBLE);
                mLinearLayoutScoresMain.setVisibility(View.VISIBLE);
                mElapse.setVisibility(View.VISIBLE);
            }else {
                mLinearLayoutTimeMain.setVisibility(View.VISIBLE);
            }
            mElapse.setText(elapsed.equals("null")?"":elapsed+"'");
            mElapse.setTextColor(getResources().getColor(R.color.primaryColor));
        }

        //loading events
        if (events.length() > 0) {
            mLinearLayoutScores.setVisibility(View.VISIBLE);
            try {
                for (int i = 0; i < events.length(); i++) {
                    JSONObject jsonObject = events.getJSONObject(i);
                    if (jsonObject.getString("type").equals("Goal")) {
                        String timeElapsed = jsonObject.getJSONObject("time").getString("elapsed");
                        String playerName = jsonObject.getJSONObject("player").getString("name");
                        String time = "<font color='#808080'>" + timeElapsed + "'</font><br>";

                        if (jsonObject.getJSONObject("team").getString("id").equals(home_id)) {
                            //set value to home scorer
                            mHomeScorer.append(Html.fromHtml(playerName.equals("null")?"-":playerName+" "+time));
                        } else {
                            //set value to away scorer
                            mAwayScorer.append(Html.fromHtml(playerName.equals("null")?"-":playerName+" "+time));
                        }

                        Log.d("PLayer", playerName);
                    }
                }
            } catch (Exception e) {

            }
        }

    }

    @Override
    protected void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public String getSeason() {
        return season;
    }

    public String getId() {
        return String.valueOf(id);
    }
}