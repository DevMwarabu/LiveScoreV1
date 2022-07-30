package com.laxco.livescorev1.Fragments.MainFrags;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class FragCal extends Fragment {
    private View view;
    private List<Fixture> fixtures;
    private FixtureAdapter fixtureAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<String> leagueIds = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OkHttpClient client;
    private okhttp3.Request request;
    private LinearLayout mLinearLayoutMain, mLinearLayoutSecond;
    private String url = "";
    private String TAG = "Demo";
    private Timer timer = new Timer();
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private CardView mSearch;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fixtures = new ArrayList<>();

        url = Constants.baseUrl + "fixtures?live=all";

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

        loadInterstitial();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_frag_cal, container, false);

        mRecyclerView = view.findViewById(R.id.rv_main);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_main);
        mSearch = view.findViewById(R.id.card_search);
        mLinearLayoutMain = view.findViewById(R.id.linear_maain);
        mLinearLayoutSecond = view.findViewById(R.id.linear_maain_secon);

        //load recycle
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(layoutManager);


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

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getContext());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            startActivity(new Intent(getContext(), MoreLives.class).putExtra("isLive",true).putExtra("title","Live Games"));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    startActivity(new Intent(getContext(), MoreLives.class).putExtra("isLive",true).putExtra("title","Live Games"));
                }
            }
        });

        //updateData();

        return view;
    }

    private void updateData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //what you want to do
                        fixtureAdapter.clear();
                        loadData();
                    }
                }, 0, 1000 * 60);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        timer.cancel();
        super.onDetach();
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getResources().getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
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
                            if (getActivity() !=null) {
                                getActivity().runOnUiThread(new Runnable() {
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

                                                    Log.d(TAG, "league " + leaguename);
                                                    //adding to model
                                                    fixtures.add(new Fixture(teams, goals, league, fixture, elapsed, home, away, leaguename));

                                                    if (i == (responseArray.length() - 1)) {
                                                        fixtureAdapter = new FixtureAdapter(getContext(), fixtures);
                                                        fixtureAdapter.setHasStableIds(true);
                                                        mRecyclerView.setAdapter(fixtureAdapter);

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
                            }
                        } else {
                            Toast.makeText(getContext(), "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
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
                    mSearch.setVisibility(View.GONE);
                    mLinearLayoutSecond.setVisibility(View.VISIBLE);
                } else {
                    mLinearLayoutSecond.setVisibility(View.GONE);
                    mSearch.setVisibility(View.VISIBLE);
                }
            }
        },3000);
        ;
    }
}