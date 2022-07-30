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
import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Adapters.LineUpAdapter;
import com.laxco.livescorev1.Adapters.StandingAdapter;
import com.laxco.livescorev1.Adapters.StandingAwayAdapter;
import com.laxco.livescorev1.Adapters.StandingHomeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandAwayScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandHomeScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandScapeAdapter;
import com.laxco.livescorev1.Constants.Constants;
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
    private LinearLayout mLinearLayoutPrediction,mLinearLayoutTable;
    private TextView mHomeTeam,mAwayTem,mDate,mRef,mStadium,mLeagueName,mLeagueType,mClickToView,mPercentageHome,mPercentageDraw,mPercentageAway,mAdvice;
    private RecyclerView mRecyclerviewTable,mRecyclerViewHome,mRecyclerViewAway;
    private ProgressBar mProgressBarHome,mProgressBarDraw,mProgressBarAway;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String url;
    private Gson gson;
    private ImageView mLeagueLogo;
    private List<Standing> standings;
    private StandingAdapter standingAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        standings = new ArrayList<>();
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
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_main);
        mLinearLayoutTable = view.findViewById(R.id.linear_table);
        mAdvice = view.findViewById(R.id.tv_advice);

       // mCardViewPredictions.setEnabled(false);


        final LinearLayoutManager layoutManagerResults = new LinearLayoutManager(getContext());
        layoutManagerResults.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerviewTable.setItemAnimator(new DefaultItemAnimator());
        mRecyclerviewTable.setHasFixedSize(true);
        mRecyclerviewTable.setItemViewCacheSize(20);
        mRecyclerviewTable.setDrawingCacheEnabled(true);
        mRecyclerviewTable.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerviewTable.setLayoutManager(layoutManagerResults);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
              //  mSwipeRefreshLayout.setRefreshing(true);
                getData(fixture_id);

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                standingAdapter.clear();
                standingAdapter.addAll(standings);
                //load in priority
                getData(fixture_id);

            }
        });

        mCardViewPredictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickToView.setVisibility(View.GONE);
                mLinearLayoutPrediction.setVisibility(View.VISIBLE);
                mAdvice.setVisibility(View.VISIBLE);
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
                                                            mSwipeRefreshLayout.setRefreshing(false);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }else {
                                                mSwipeRefreshLayout.setRefreshing(false);
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
                            mSwipeRefreshLayout.setRefreshing(false);
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
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdvice.setText("No predictions votes available");
                        }
                    }
                });

    }

}