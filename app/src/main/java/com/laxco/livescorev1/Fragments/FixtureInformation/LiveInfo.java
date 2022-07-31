package com.laxco.livescorev1.Fragments.FixtureInformation;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.FixtureLastAdapter;
import com.laxco.livescorev1.Adapters.FixtureLastHomeAdapter;
import com.laxco.livescorev1.Adapters.LineUpAdapter;
import com.laxco.livescorev1.Adapters.StandingAdapter;
import com.laxco.livescorev1.Adapters.StandingAwayAdapter;
import com.laxco.livescorev1.Adapters.StandingHomeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandAwayScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandHomeScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandScapeAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.FixtureHome;
import com.laxco.livescorev1.Models.Lineupitem;
import com.laxco.livescorev1.Models.Standing;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class LiveInfo extends Fragment {
    private View view;
    private CardView mCardViewPredictions;
    private LinearLayout mLinearLayoutPrediction,mLinearLayoutTable,mLinearLayoutF1,mLinearLayoutF2,mLinearLayoutMain;
    private TextView mHomeTeam,mAwayTem,mDate,mRef,mStadium,mLeagueName,mLeagueType,mClickToView,mPercentageHome,mPercentageDraw,mPercentageAway,mAdvice;
    private RecyclerView mRecyclerviewTable,mRecyclerViewHome,mRecyclerViewAway;
    private ProgressBar mProgressBarHome,mProgressBarDraw,mProgressBarAway;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String url;
    private ProgressBar progressBar;
    private Gson gson;
    private ImageView mLeagueLogo;
    private List<Standing> standings;
    private StandingAdapter standingAdapter;
    private List<Fixture> fixtures;
    private List<FixtureHome> fixtureHomes;
    private FixtureLastAdapter fixtureLastAdapter;
    private FixtureLastHomeAdapter fixtureLastHomeAdapter;
    private RewardedAd mRewardedAd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        standings = new ArrayList<>();
        fixtures = new ArrayList<>();
        fixtureHomes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_live_info, container, false);
        FixtureInfo fixtureInfo = (FixtureInfo) getActivity();

        url = Constants.baseUrl + "fixtures?id="+fixtureInfo.getId();


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

        loadReaward();


        initView(view,fixtureInfo.getId());

        //setdata
//        getData();


        return view;
    }

    private void initView(View view,String fixture_id){
        mCardViewPredictions = view.findViewById(R.id.card_predictions);
        mLinearLayoutPrediction = view.findViewById(R.id.linear_predictions);
        mHomeTeam = view.findViewById(R.id.tv_home_team);
        mAwayTem = view.findViewById(R.id.tv_away_team);
        mDate = view.findViewById(R.id.tv_date_kick_off);
        mRef = view.findViewById(R.id.tv_ref);
        mStadium = view.findViewById(R.id.tv_stadium);
        mLeagueName = view.findViewById(R.id.tv_league_name);
        mLeagueType = view.findViewById(R.id.tv_league_type);
        mClickToView = view.findViewById(R.id.tv_click_to_view);
        mPercentageHome = view.findViewById(R.id.tv_percentage_home);
        mPercentageDraw = view.findViewById(R.id.tv_percentage_draw);
        mPercentageAway = view.findViewById(R.id.tv_percentage_away);
        mProgressBarHome = view.findViewById(R.id.progressBar_home);
        mProgressBarDraw = view.findViewById(R.id.progressBar_draw);
        mProgressBarAway = view.findViewById(R.id.progressBar_away);
        mLeagueLogo = view.findViewById(R.id.image_league_logo);
        mRecyclerViewAway = view.findViewById(R.id.rv_away);
        mRecyclerViewHome = view.findViewById(R.id.rv_home);
        mRecyclerviewTable = view.findViewById(R.id.rv_standings);
        mLinearLayoutTable = view.findViewById(R.id.linear_table);
        mAdvice = view.findViewById(R.id.tv_advice);
        mLinearLayoutF1 = view.findViewById(R.id.linear_form_1);
        mLinearLayoutF2 = view.findViewById(R.id.linear_form_2);
        mLinearLayoutMain = view.findViewById(R.id.linear_maain);
        progressBar = view.findViewById(R.id.progressBar);

       // mCardViewPredictions.setEnabled(false);


        final LinearLayoutManager layoutManagerResults = new LinearLayoutManager(getContext());
        layoutManagerResults.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerviewTable.setItemAnimator(new DefaultItemAnimator());
        mRecyclerviewTable.setHasFixedSize(true);
        mRecyclerviewTable.setItemViewCacheSize(20);
        mRecyclerviewTable.setDrawingCacheEnabled(true);
        mRecyclerviewTable.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerviewTable.setLayoutManager(layoutManagerResults);


        final LinearLayoutManager layoutManagerHome = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerHome.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewHome.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewHome.setHasFixedSize(true);
        mRecyclerViewHome.setItemViewCacheSize(20);
        mRecyclerViewHome.setDrawingCacheEnabled(true);
        mRecyclerViewHome.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerViewHome.setLayoutManager(layoutManagerHome);


        final LinearLayoutManager layoutManagerAway = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerAway.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewAway.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewAway.setHasFixedSize(true);
        mRecyclerViewAway.setItemViewCacheSize(20);
        mRecyclerViewAway.setDrawingCacheEnabled(true);
        mRecyclerViewAway.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerViewAway.setLayoutManager(layoutManagerAway);


        getData(fixture_id);

        mCardViewPredictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                            mClickToView.setVisibility(View.GONE);
                            mLinearLayoutPrediction.setVisibility(View.VISIBLE);
                            mAdvice.setVisibility(View.VISIBLE);

                            Log.d("Rewarded",""+rewardAmount+" type "+rewardType);
                        }
                    });
                } else {
                    mClickToView.setVisibility(View.GONE);
                    mLinearLayoutPrediction.setVisibility(View.VISIBLE);
                    mAdvice.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void getData(String fixture_id) {

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
                            if (getActivity() !=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");
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
                                            String home_id = jsonObject.getJSONObject("teams").getJSONObject("home").getString("id");
                                            String away_id = jsonObject.getJSONObject("teams").getJSONObject("away").getString("id");
                                            String league = jsonObject.getJSONObject("league").getString("name");
                                            String leagueId = jsonObject.getJSONObject("league").getString("id");
                                            String ref = jsonObject.getJSONObject("fixture").getString("referee");
                                            String date = jsonObject.getJSONObject("fixture").getString("date");
                                            String venue = jsonObject.getJSONObject("fixture").getJSONObject("venue").getString("name");
                                            String leagueLogo = jsonObject.getJSONObject("league").getString("logo");
                                            String leaguetype = jsonObject.getJSONObject("league").getString("round");
                                            String s = jsonObject.getJSONObject("league").getString("season");
                                            //loading data
                                            setData(leaguetype, leagueLogo,ref,venue,league,homeName,awayName,date,leagueId,s,home_id,away_id,fixture_id);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

    }

    private void setData(String lType,String lUrl,String ref,String venue,String league,String homeName,String awayName,String date,
                         String leagueId,String season,String homeId,String awayId,String fixture_id){
        mLeagueType.setText(lType.equals("null")?"-":lType);
        mRef.setText(ref.equals("null")?"-":ref);
        mLeagueName.setText(league.equals("null")?"-":league);
        mHomeTeam.setText(homeName.equals("null")?"-":homeName);
        mAwayTem.setText(awayName.equals("null")?"-":awayName);
        mStadium.setText(venue.equals("null")?"-":venue);
        //home
        Glide.with(getContext().getApplicationContext()).load(lUrl)
                .apply(new RequestOptions()
                        .fitCenter()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLeagueLogo);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Date result;
        try {
            result = df.parse(date);
            System.out.println("date:" + result); //prints date in current locale
            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy hh:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC +3"));
            mDate.setText(sdf.format(result));
        } catch (Exception e) {

        }
        loadTable(leagueId,season,homeId,awayId,fixture_id);
        loadForms(homeId,season,leagueId);
        loadFormsAway(awayId,season,leagueId);
        loadHAFixture(homeId,awayId,season);

        Log.d("Value passe ",""+lType);
    }

    private void loadTable(String leagueId,String season,String homeId,String awayId,String fixture_id){

       String url_= Constants.baseUrl+"standings?league="+leagueId+"&season="+season;
        request = new okhttp3.Request.Builder()
                .url(url_)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
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
                            if (getActivity() !=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");
                                            if (responseArray.length()>0){
                                                JSONObject league = responseArray.getJSONObject(0).getJSONObject("league");
                                                JSONArray standings_all = league.getJSONArray("standings");
                                                JSONArray resData = standings_all.getJSONArray(0);


                                                if (resData.length() > 0) {

                                                    for (int i = 0; i < resData.length(); i++) {
                                                        JSONObject jsonObject = resData.getJSONObject(i);

                                                        JSONObject all = jsonObject.getJSONObject("all");
                                                        JSONObject goals  = all.getJSONObject("goals");
                                                        String rank =  jsonObject.getString("rank");
                                                        String points = jsonObject.getString("points");
                                                        String gd=jsonObject.getString("goalsDiff");
                                                        JSONObject team = jsonObject.getJSONObject("team");
                                                        JSONObject home = jsonObject.getJSONObject("home");
                                                        JSONObject away = jsonObject.getJSONObject("away");

                                                        if (team.getString("id").equals(homeId) || team.getString("id").equals(awayId)){
                                                            //adding to model
                                                            standings.add(new Standing(team,all,goals,rank,points,gd,home,away));

                                                            mLinearLayoutTable.setVisibility(View.VISIBLE);

                                                            standingAdapter = new StandingAdapter(getContext(), standings,season,leagueId);
                                                            standingAdapter.setHasStableIds(true);

                                                            mRecyclerviewTable.setAdapter(standingAdapter);

                                                            response.body().close();
                                                            standingAdapter.notifyDataSetChanged();
                                                            if (standings.size()>1){
                                                                loadPrediction(fixture_id);
                                                                Log.d("Standings size",""+standings.size());
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }

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
                    }
                });

    }

    private void loadPrediction(String fixture_id){

        String url_= Constants.baseUrl+"predictions?fixture="+fixture_id;
        request = new okhttp3.Request.Builder()
                .url(url_)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
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
                            if (getActivity() !=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");
                                            Log.d("Prediction", responseData);
                                            if (responseArray.length()>0){
                                                JSONObject data = responseArray.getJSONObject(0);

                                                JSONObject predictions = data.getJSONObject("predictions");
                                                JSONObject percent = predictions.getJSONObject("percent");
                                                //setting data
                                                mPercentageHome.setText(percent.getString("home"));
                                                mPercentageDraw.setText(percent.getString("draw"));
                                                mPercentageAway.setText(percent.getString("away"));
                                                mAdvice.setText(predictions.getString("advice"));
                                                //adding to progress bars
                                                mProgressBarAway.setProgress(Integer.parseInt(percent.getString("away").substring(0, percent.getString("away").length()-1)));
                                                mProgressBarDraw.setProgress(Integer.parseInt(percent.getString("draw").substring(0, percent.getString("draw").length()-1)));
                                                mProgressBarHome.setProgress(Integer.parseInt(percent.getString("home").substring(0, percent.getString("home").length()-1)));

                                                mCardViewPredictions.setEnabled(true);

                                                response.body().close();
                                            }else {
                                                mAdvice.setText("No predictions votes available");
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
                            mAdvice.setText("No predictions votes available");
                        }
                    }
                });

    }

    private void loadForms(String homeId,String season,String leagueId){
        String url_= Constants.baseUrl+"teams/statistics?league="+leagueId+"&season="+season+"&team="+homeId;
        request = new okhttp3.Request.Builder()
                .url(url_)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
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
                            if (getActivity() !=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType", "NewApi"})
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONObject jsonObject = new JSONObject(responseData).getJSONObject("response");
                                            String form = jsonObject.getString("form");
                                            if (!form.equals("null")){
                                                String  name = jsonObject.getJSONObject("team").getString("name");
                                                if (form.toCharArray().length>5){

                                                    char last_5[] = form.substring((form.length()-5)).toCharArray();
                                                    for(char c : last_5){
                                                        //generate chars
                                                        Log.d("Form ",form+"   Team"+name+" "+c);
                                                        TextView textView = new TextView(getContext());
                                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                        params.setMargins(4,0,4,0);
                                                        textView.setLayoutParams(params);
                                                        textView.setText(String.valueOf(c));
                                                        if (c == 'W'){
                                                            textView.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_item));
                                                        } else if (c == 'L'){
                                                            textView.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_item_lose));
                                                        }else {
                                                            textView.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_item_draw));
                                                        }
                                                        textView.setPadding(8, 2, 8, 2);
//                                                        textView.setWidth(20);
//                                                        textView.setHeight(20);
                                                        textView.setTextAppearance(android.R.style.TextAppearance_Material_Caption);
                                                        //textView.setTextAppearance(com.skydoves.powermenu.R.attr.textAppearanceCaption);
                                                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        mLinearLayoutF1.addView(textView);
                                                    }
                                                }
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
                            Log.d("Form ","form failed..!"+response.body().string());
                        }
                    }
                });


    }

    private void loadFormsAway(String homeId,String season,String leagueId){
        String url_= Constants.baseUrl+"teams/statistics?league="+leagueId+"&season="+season+"&team="+homeId;
        request = new okhttp3.Request.Builder()
                .url(url_)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
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
                            if (getActivity() !=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType", "NewApi"})
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONObject jsonObject = new JSONObject(responseData).getJSONObject("response");
                                            String form = jsonObject.getString("form");
                                            if (!form.equals("null")){
                                                String  name = jsonObject.getJSONObject("team").getString("name");
                                                if (form.toCharArray().length>5){

                                                    char last_5[] = form.substring((form.length()-5)).toCharArray();
                                                    for(char c : last_5){
                                                        //generate chars
                                                        Log.d("Form ",form+"   Team"+name+" "+c);
                                                        TextView textView = new TextView(getContext());
                                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                        params.setMargins(4,0,4,0);
                                                        textView.setLayoutParams(params);
                                                        textView.setText(String.valueOf(c));
                                                        if (c == 'W'){
                                                            textView.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_item));
                                                        } else if (c == 'L'){
                                                            textView.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_item_lose));
                                                        }else {
                                                            textView.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_item_draw));
                                                        }
                                                        textView.setPadding(8, 2, 8, 2);
//                                                        textView.setWidth(20);
//                                                        textView.setHeight(20);
                                                        textView.setTextAppearance(android.R.style.TextAppearance_Material_Caption);
                                                        //textView.setTextAppearance(com.skydoves.powermenu.R.attr.textAppearanceCaption);
                                                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        mLinearLayoutF2.addView(textView);
                                                    }
                                                }
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
                            Log.d("Form ","form failed..!"+response.body().string());
                        }
                    }
                });


    }

    private void loadHAFixture(String id_home,String id_away,String season){

        String url_= Constants.baseUrl+"fixtures?season="+season+"&team="+id_home+"&last=5";
        request = new okhttp3.Request.Builder()
                .url(url_)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
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
                                                    fixtureHomes.add(new FixtureHome(teams, goals, league, fixture, elapsed, home, away, leaguename));

                                                    if (i == (responseArray.length() - 1)) {
                                                        fixtureLastHomeAdapter = new FixtureLastHomeAdapter(getContext(), fixtureHomes);
                                                        fixtureLastHomeAdapter.setHasStableIds(true);
                                                        mRecyclerViewHome.setAdapter(fixtureLastHomeAdapter);

                                                        response.body().close();
                                                        fixtureLastHomeAdapter.notifyDataSetChanged();

                                                        loadHAFixture(id_away,season);
                                                    }
                                                }
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
                        }
                    }
                });

    }

    private void loadHAFixture(String id_away,String season){

        String url_= Constants.baseUrl+"fixtures?season="+season+"&team="+id_away+"&last=5";
        request = new okhttp3.Request.Builder()
                .url(url_)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
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
                                                        fixtureLastAdapter = new FixtureLastAdapter(getContext(), fixtures);
                                                        fixtureLastAdapter.setHasStableIds(true);
                                                        mRecyclerViewAway.setAdapter(fixtureLastAdapter);

                                                        response.body().close();
                                                        fixtureLastAdapter.notifyDataSetChanged();

                                                        progressBar.setVisibility(View.GONE);
                                                        mLinearLayoutMain.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }else {
                                                progressBar.setVisibility(View.GONE);
                                                mLinearLayoutMain.setVisibility(View.VISIBLE);
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
                            progressBar.setVisibility(View.GONE);
                            mLinearLayoutMain.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loadReaward(){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getContext(), getContext().getResources().getString(R.string.reward_ad),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

    }

}