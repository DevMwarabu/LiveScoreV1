package com.laxco.livescorev1.Fragments.LeagueFrags;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.LeagueInfo;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.MatchAdapterNext;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.MatchNext;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class Matches extends Fragment {
    private View view;
    private List<Fixture> fixtures;
    private FixtureAdapter fixtureAdapter;
    private List<MatchNext> matchNexts;
    private MatchAdapterNext matchAdapterNext;
    private RecyclerView mRecyclerViewFixture,mRecyclerViewReults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayoutMain,mLinearLayoutSecond;
    private OkHttpClient client;
    private okhttp3.Request request;
    private CardView mSearch;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private String url_f ,url_rslt;
    private String TAG = "Demo";
    private Gson gson;
    private Chip mChipMatches,mChipResults;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LeagueInfo leagueInfo = (LeagueInfo) getActivity();

        matchNexts = new ArrayList<>();
        fixtures = new ArrayList<>();

        url_f= Constants.baseUrl+"fixtures?league="+leagueInfo.getId()+"&season="+leagueInfo.getSeason();
        url_rslt= Constants.baseUrl+"fixtures?league="+leagueInfo.getId()+"&season="+leagueInfo.getSeason()+"&status=NS-PEN-1H-2H-ET-P-BT-FT-AET-PEN-SUSP-INT-CANC-ABD-AWD-WO-LIVE";

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .build();

        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_matches, container, false);

        mRecyclerViewReults = view.findViewById(R.id.rv_results);
        mRecyclerViewFixture = view.findViewById(R.id.rv_fixtures);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_main);
        mLinearLayoutMain = view.findViewById(R.id.linear_maain);
        mSearch = view.findViewById(R.id.card_search);
        mLinearLayoutSecond = view.findViewById(R.id.linear_maain_secon);
        mChipMatches = view.findViewById(R.id.chip_fixture);
        mChipResults = view.findViewById(R.id.chip_results);

        //load recycle
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewFixture.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewFixture.setHasFixedSize(true);
        mRecyclerViewFixture.setItemViewCacheSize(20);
        mRecyclerViewFixture.setDrawingCacheEnabled(true);
        mRecyclerViewFixture.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerViewFixture.setLayoutManager(layoutManager);

        final LinearLayoutManager layoutManagerResults = new LinearLayoutManager(getContext());
        layoutManagerResults.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewReults.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewReults.setHasFixedSize(true);
        mRecyclerViewReults.setItemViewCacheSize(20);
        mRecyclerViewReults.setDrawingCacheEnabled(true);
        mRecyclerViewReults.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerViewReults.setLayoutManager(layoutManagerResults);



        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData();
                loadData(url_rslt);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                matchAdapterNext.clear();
                matchAdapterNext.addAll(matchNexts);
                fixtureAdapter.clear();
                fixtureAdapter.addAll(fixtures);
                loadData();
                loadData(url_rslt);

            }
        });

        mChipMatches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleViews(b);
            }
        });




        return view;
    }



    private void loadData(){
        request = new okhttp3.Request.Builder()
                .url(url_f)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                        //usimgUIThread
                        if (response.isSuccessful()){
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

                                                    //adding to model
                                                    matchNexts.add(new MatchNext(teams, goals, league, fixture));

                                                    if (i == (responseArray.length() - 1)) {
                                                        matchAdapterNext = new MatchAdapterNext(getContext(), matchNexts);
                                                        matchAdapterNext.setHasStableIds(true);
                                                        mRecyclerViewFixture.setAdapter(matchAdapterNext);

                                                        response.body().close();
                                                        mSwipeRefreshLayout.setRefreshing(false);
                                                        matchAdapterNext.notifyDataSetChanged();
                                                        //  toggleView(false);
                                                    }
                                                }
                                            } else {

                                                mSwipeRefreshLayout.setRefreshing(false);
                                                // toggleView(true);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }else {
                            Toast.makeText(getContext(), "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void loadData(String url){
        request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                        //usimgUIThread
                        if (response.isSuccessful()){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String responseData = response.body().string();
                                        JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");

                                        if (responseArray.length() > 0){
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
                                                fixtures.add(new Fixture(teams,goals,league,fixture,elapsed,home,away,leaguename));

                                                if (i == (responseArray.length() - 1)) {
                                                    fixtureAdapter = new FixtureAdapter(getContext(), fixtures);
                                                    fixtureAdapter.setHasStableIds(true);
                                                    mRecyclerViewReults.setAdapter(fixtureAdapter);

                                                    response.body().close();
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                    fixtureAdapter.notifyDataSetChanged();
                                                    //  toggleView(false);
                                                }
                                            }
                                        }else {

                                            mSwipeRefreshLayout.setRefreshing(false);
                                            // toggleView(true);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getContext(), "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void toggleViews(boolean isMatches){
        if (isMatches){
            mRecyclerViewFixture.setVisibility(View.VISIBLE);
            mRecyclerViewReults.setVisibility(View.GONE);
        }else {
            mRecyclerViewReults.setVisibility(View.VISIBLE);
            mRecyclerViewFixture.setVisibility(View.GONE);
        }
    }
}