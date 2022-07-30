package com.laxco.livescorev1.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.laxco.livescorev1.Adapters.CountryAdapter;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.BuildConfig;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Dashboard;
import com.laxco.livescorev1.Models.Country;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MoreLives extends AppCompatActivity {
    private CardView mBack, mStatus;
    private TextView mLTextView;
    private RecyclerView mRvLive;

    private List<Fixture> fixtures;
    private FixtureAdapter fixtureAdapter;
    private SearchView mSearchView;
    private CardView mCardViewStatus;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout,mLinearLayoutSecond;
    private String date, title;
    private AdView mAdView;
    private boolean isLive;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String url;
    private String TAG = "Demo";
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_lives);

        isLive = getIntent().getExtras().getBoolean("isLive");
        title = getIntent().getExtras().getString("title");
        mStatus = findViewById(R.id.card_status);
        if (!isLive) {
            date = getIntent().getExtras().getString("date");
            url = Constants.baseUrl + "fixtures?date=" + date + "&status=NS-PEN-1H-2H-ET-P-BT-FT";
        } else {
            url = Constants.baseUrl + "fixtures?live=all";
            mStatus.setVisibility(View.VISIBLE);
        }
        mLTextView = findViewById(R.id.tv_league_name);
        mLTextView.setText(title);


        loadBunnerAd();
        loadInterstitial();

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

        fixtures = new ArrayList<>();
        fixtureAdapter = new FixtureAdapter(this, fixtures);


        mBack = findViewById(R.id.card_back);
        mRvLive = findViewById(R.id.rv_live_matches);
        mSwipeRefreshLayout = findViewById(R.id.swipe_main);
        mSearchView = findViewById(R.id.searchview);
        mLinearLayoutSecond = findViewById(R.id.linear_maain_secon);
        mLinearLayout = findViewById(R.id.linear_maain);
        mStatus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_anim));


        //load recycle
        final LinearLayoutManager layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        mRvLive.setLayoutManager(layoutManager);
        mRvLive.setItemAnimator(new DefaultItemAnimator());
        mRvLive.setHasFixedSize(true);
        mRvLive.setItemViewCacheSize(20);
        mRvLive.setDrawingCacheEnabled(true);
        mRvLive.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        //others
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fixtureAdapter.clear();
                fixtureAdapter.addAll(fixtures);
                loadData();

            }
        });


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fixtureAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fixtureAdapter.getFilter().filter(newText);
                return true;
            }
        });

        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show(MoreLives.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            // startActivity(new Intent(Dashboard.this, CombinedUi.class).putExtra("position", 0).putExtra("isNew", false).putExtra("isFinishing",false));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    //startActivity(new Intent(Dashboard.this, CombinedUi.class).putExtra("position", 0).putExtra("isNew", false).putExtra("isFinishing",false));
                }
            }
        });
    }


    private void loadBunnerAd() {
        mAdView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

    @Override
    protected void onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        super.onDestroy();
    }

    private void loadData() {
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

                                        if (responseArray.length() > 0) {
                                            for (int i = 0; i < responseArray.length(); i++) {
                                                JSONObject jsonObject = responseArray.getJSONObject(i);

                                                JSONObject fixture = jsonObject.getJSONObject("fixture");
                                                JSONObject teams = jsonObject.getJSONObject("teams");
                                                JSONObject goals = jsonObject.getJSONObject("goals");
                                                JSONObject league = jsonObject.getJSONObject("league");
                                                String elapsed = fixture.getJSONObject("status").getString("elapsed");
                                                String home = teams.getJSONObject("home").getString("name");
                                                String away = teams.getJSONObject("away").getString("name");
                                                String leaguename = league.getString("name");
                                                //adding to model
                                                fixtures.add(new Fixture(teams, goals, league, fixture, elapsed, home, away, leaguename));

                                                if (i == (responseArray.length() - 1)) {
                                                    fixtureAdapter = new FixtureAdapter(MoreLives.this, fixtures);
                                                    fixtureAdapter.setHasStableIds(true);
                                                    mRvLive.setAdapter(fixtureAdapter);

                                                    response.body().close();
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                    fixtureAdapter.notifyDataSetChanged();
                                                    toggleView(false);
                                                }
                                            }
                                        } else {

                                            mSwipeRefreshLayout.setRefreshing(false);
                                            toggleView(true);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MoreLives.this, "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void toggleView(boolean isEmpty) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isEmpty) {
                    mLinearLayout.setVisibility(View.GONE);
                    mLinearLayoutSecond.setVisibility(View.VISIBLE);
                } else {
                    mLinearLayoutSecond.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                }

            }
        },3000);
        ;
    }


}