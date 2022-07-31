package com.laxco.livescorev1.Fragments.FixtureInformation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.LineUpAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.Lineupitem;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class Summary extends Fragment {
    private View view;
    private List<Lineupitem> lineupitems;
    private LineUpAdapter lineUpAdapter;
    private RecyclerView mRecyclerView;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String url = "";
    private LinearLayout mLinearLayoutMain, mLinearLayoutSecond;
    private String TAG = "Demo";
    private Timer timer = new Timer();
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private Gson gson;
    private String season,homeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FixtureInfo fixtureInfo = (FixtureInfo)getActivity();

        lineupitems = new ArrayList<>();

        homeId = FixtureInfo.homeId;

        season = fixtureInfo.getSeason();
        url = Constants.baseUrl + "fixtures/events?fixture="+fixtureInfo.getId();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_summary, container, false);

        mRecyclerView = view.findViewById(R.id.rv_main);
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


        loadData(season,homeId);

        return view;
    }

    private void loadData(String season,String homeId) {
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
                                                    String team_id = jsonObject.getJSONObject("team").getString("id");
                                                    String type = jsonObject.getString("type");
                                                    String details = jsonObject.getString("detail");
                                                    JSONObject time = jsonObject.getJSONObject("time");
                                                    JSONObject player = jsonObject.getJSONObject("player");

                                                    if (team_id.equals(homeId)){
                                                        //adding to model
                                                        lineupitems.add(new Lineupitem(team_id,type,details,time,player,true,season));
                                                    }else {
                                                        //adding to model
                                                        lineupitems.add(new Lineupitem(team_id,type,details,time,player,false,season));
                                                    }

                                                    if (i == (responseArray.length() - 1)) {
                                                        lineUpAdapter = new LineUpAdapter(getContext(), lineupitems);
                                                        lineUpAdapter.setHasStableIds(true);
                                                        mRecyclerView.setAdapter(lineUpAdapter);

                                                        response.body().close();
                                                        lineUpAdapter.notifyDataSetChanged();
                                                        toggleView(false);
                                                    }
                                                }
                                            }else {

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
                        }
                    }
                });
    }

    private void toggleView(boolean isEmpty) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isEmpty) {
                    mLinearLayoutMain.setVisibility(View.GONE);
                    mLinearLayoutSecond.setVisibility(View.VISIBLE);
                } else {
                    mLinearLayoutSecond.setVisibility(View.GONE);
                    mLinearLayoutMain.setVisibility(View.VISIBLE);
                }
            }
        },3000);
        ;
    }
}