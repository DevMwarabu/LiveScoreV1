package com.laxco.livescorev1.Fragments.LeagueFrags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.LeagueInfo;
import com.laxco.livescorev1.Adapters.MatchAdapterNext;
import com.laxco.livescorev1.Adapters.StandingAdapter;
import com.laxco.livescorev1.Adapters.StandingAwayAdapter;
import com.laxco.livescorev1.Adapters.StandingHomeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandAwayScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandHomeScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandScapeAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.MatchNext;
import com.laxco.livescorev1.Models.Standing;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class Tables extends Fragment {
    private View view;
    private FrameLayout frameLayout;
    private RecyclerView mRvHome,mRvAway,mRvAll;
    private TextView mLeague,mDescription,mSeason;
    private Chip mAll,mHome,mAway;
    private List<Standing> standings;
    private StandingAdapter standingAdapter;
    private StandingHomeAdapter standingHomeAdapter;
    private StandingAwayAdapter standingAwayAdapter;
    private StandingLandHomeScapeAdapter standingLandHomeScapeAdapter;
    private StandingLandAwayScapeAdapter standingLandAwayScapeAdapter;
    private StandingLandScapeAdapter standingLandScapeAdapter;
    private String url;
    private OkHttpClient client;
    private okhttp3.Request request;
    private CardView mRotate;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private String TAG = "Demo",season;
    private Gson gson;
    private boolean isLand = false;
    private LeagueInfo leagueInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leagueInfo = (LeagueInfo) getActivity();

        season = leagueInfo.getSeason();

        standings = new ArrayList<>();

        url= Constants.baseUrl+"standings?league="+leagueInfo.getId()+"&season="+leagueInfo.getSeason();
        request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .build();

        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frameLayout = new FrameLayout(getActivity());
        @SuppressLint({"NewApi", "LocalSuppress"}) LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater2.inflate(R.layout.fragment_tables, null);
        frameLayout.addView(rootView);
        addUi();
        return frameLayout;
    }

    public void addUi() {
        //add ui elements
        mHome = frameLayout.findViewById(R.id.chip_home);
        mAll = frameLayout.findViewById(R.id.chip_all);
        mAway = frameLayout.findViewById(R.id.chip_away);
        mLeague = frameLayout.findViewById(R.id.tv_league_name);
        mSeason = frameLayout.findViewById(R.id.tv_league_season);
        mDescription = frameLayout.findViewById(R.id.tv_league_description);
        mRotate = frameLayout.findViewById(R.id.card_rotate);

        mRvAll = frameLayout.findViewById(R.id.rv_all);
        mRvAway = frameLayout.findViewById(R.id.rv_away);
        mRvHome = frameLayout.findViewById(R.id.rv_home);


        final LinearLayoutManager layoutManagerResults = new LinearLayoutManager(getContext());
        layoutManagerResults.setOrientation(LinearLayoutManager.VERTICAL);
        mRvAll.setItemAnimator(new DefaultItemAnimator());
        mRvAll.setHasFixedSize(true);
        mRvAll.setItemViewCacheSize(20);
        mRvAll.setDrawingCacheEnabled(true);
        mRvAll.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRvAll.setLayoutManager(layoutManagerResults);


        final LinearLayoutManager layoutManagerHome = new LinearLayoutManager(getContext());
        layoutManagerHome.setOrientation(LinearLayoutManager.VERTICAL);
        mRvHome.setItemAnimator(new DefaultItemAnimator());
        mRvHome.setHasFixedSize(true);
        mRvHome.setItemViewCacheSize(20);
        mRvHome.setDrawingCacheEnabled(true);
        mRvHome.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRvHome.setLayoutManager(layoutManagerHome);


        final LinearLayoutManager layoutManagerAway = new LinearLayoutManager(getContext());
        layoutManagerAway.setOrientation(LinearLayoutManager.VERTICAL);
        mRvAway.setItemAnimator(new DefaultItemAnimator());
        mRvAway.setHasFixedSize(true);
        mRvAway.setItemViewCacheSize(20);
        mRvAway.setDrawingCacheEnabled(true);
        mRvAway.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRvAway.setLayoutManager(layoutManagerAway);

        mHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mRvHome.setVisibility(View.VISIBLE);
                }else {
                    mRvHome.setVisibility(View.GONE);
                }
            }
        });
        mAway.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mRvAway.setVisibility(View.VISIBLE);
                }else {
                    mRvAway.setVisibility(View.GONE);
                }
            }
        });
        mAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mRvAll.setVisibility(View.VISIBLE);
                }else {
                    mRvAll.setVisibility(View.GONE);
                }
            }
        });

        mRotate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View view) {
                if (isLand){
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

        //loading data
        loadLandscapePortrait();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int layoutId;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutId = R.layout.table_land_scape;
            isLand = true;
        } else {
            layoutId = R.layout.fragment_tables;
            isLand = false;
        }

        if (frameLayout != null) {
            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(layoutId, null);
            frameLayout.addView(rootView);
            addUi();
        }
    }

    private void loadLandscapePortrait(){

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
                            if (getActivity() !=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");
                                            JSONObject league = responseArray.getJSONObject(0).getJSONObject("league");
                                            JSONArray standings_all = league.getJSONArray("standings");
                                            JSONArray resData = standings_all.getJSONArray(0);

                                            mLeague.setText(league.getString("name"));
                                            mSeason.setText(season);

                                            if (resData.length() > 0) {

                                                for (int i = 0; i < resData.length(); i++) {
                                                    JSONObject jsonObject = resData.getJSONObject(i);

                                                    mDescription.setText(jsonObject.getString("description"));

                                                    JSONObject all = jsonObject.getJSONObject("all");
                                                    JSONObject goals  = all.getJSONObject("goals");
                                                    String rank =  jsonObject.getString("rank");
                                                    String points = jsonObject.getString("points");
                                                    String gd=jsonObject.getString("goalsDiff");
                                                    JSONObject team = jsonObject.getJSONObject("team");
                                                    JSONObject home = jsonObject.getJSONObject("home");
                                                    JSONObject away = jsonObject.getJSONObject("away");



                                                    //adding to model
                                                    standings.add(new Standing(team,all,goals,rank,points,gd,home,away));

                                                    if (i == (responseArray.length() - 1)) {

                                                        standingLandScapeAdapter = new StandingLandScapeAdapter(getContext(), standings,season,leagueInfo.getId());
                                                        standingLandScapeAdapter.setHasStableIds(true);

                                                        standingAdapter = new StandingAdapter(getContext(), standings,season,leagueInfo.getId());
                                                        standingAdapter.setHasStableIds(true);



                                                        standingHomeAdapter = new StandingHomeAdapter(getContext(), standings,season,leagueInfo.getId());
                                                        standingHomeAdapter.setHasStableIds(true);

                                                        standingLandHomeScapeAdapter = new StandingLandHomeScapeAdapter(getContext(), standings,season,leagueInfo.getId());
                                                        standingLandHomeScapeAdapter.setHasStableIds(true);

                                                        standingAwayAdapter = new StandingAwayAdapter(getContext(), standings,season,leagueInfo.getId());
                                                        standingAwayAdapter.setHasStableIds(true);

                                                        standingLandAwayScapeAdapter = new StandingLandAwayScapeAdapter(getContext(), standings,season,leagueInfo.getId());
                                                        standingLandAwayScapeAdapter.setHasStableIds(true);

                                                        if (isLand){
                                                            mRvAll.setAdapter(standingLandScapeAdapter);
                                                            mRvHome.setAdapter(standingLandHomeScapeAdapter);
                                                            mRvAway.setAdapter(standingLandAwayScapeAdapter);
                                                        }else {
                                                            mRvAll.setAdapter(standingAdapter);
                                                            mRvHome.setAdapter(standingHomeAdapter);
                                                            mRvAway.setAdapter(standingAwayAdapter);
                                                        }

                                                        response.body().close();
                                                        standingAdapter.notifyDataSetChanged();
                                                        standingLandScapeAdapter.notifyDataSetChanged();
                                                        standingLandAwayScapeAdapter.notifyDataSetChanged();
                                                        standingLandHomeScapeAdapter.notifyDataSetChanged();
                                                        standingAwayAdapter.notifyDataSetChanged();
                                                        standingHomeAdapter.notifyDataSetChanged();
                                                        //  toggleView(false);
                                                    }
                                                }
                                            }else {
                                                Toast.makeText(getContext(), "No live matches going on..!", Toast.LENGTH_SHORT).show();
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
                        }
                    }
                });

    }
}