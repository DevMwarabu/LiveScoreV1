package com.laxco.livescorev1.Fragments.FixtureInformation;

import android.content.Context;
import android.os.Bundle;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Adapters.StatAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Stat;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats extends Fragment {
    private View view;
    public static RecyclerView mRecyclerView;
    public static List<Stat> stats;
    public static StatAdapter statAdapter;
    private String TAG= "res_beer_stats",fixture_id,away_val,home_val;
    private Context context = FixtureInfo.context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayoutMain,mLinearLayoutSecond;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stats, container, false);

        stats = new ArrayList<>();
        statAdapter = new StatAdapter(getContext(),stats);
        fixture_id = FixtureInfo.fixture_id;

        mRecyclerView = view.findViewById(R.id.rv_main);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_main);
        mLinearLayoutMain = view.findViewById(R.id.linear_maain);
        mLinearLayoutSecond = view.findViewById(R.id.linear_maain_secon);

        //load recycle
        final LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);



        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData(fixture_id);

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                statAdapter.clear();
                statAdapter.addAll(stats);
                //load in priority
                loadData(fixture_id);

            }
        });



        return view;
    }

    private  void loadData(String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl + "fixtures/statistics?fixture=" + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG, response.toString());

                    JSONArray jsonArray = response.getJSONArray("response");

                    Log.d("home_data_size",""+jsonArray.length());

                    if (jsonArray.length()>0){

                        for (int j=0;j< jsonArray.length();j++){
                            if (j == 0){
                                JSONObject home = jsonArray.getJSONObject(j);
                                JSONArray statistics = home.getJSONArray("statistics");

                                if (statistics.length() > 0) {

                                    for (int i = 0; i < statistics.length(); i++) {
                                        JSONObject jsonObject = statistics.getJSONObject(i);
                                        String type = jsonObject.getString("type");
                                        home_val = jsonObject.getString("value");
                                        away_val = jsonArray.getJSONObject(1).getJSONArray("statistics").getJSONObject(i).getString("value");

                                        if (home_val == "null" ){
                                            home_val = "0";
                                        }

                                        if (away_val == "null"){
                                            away_val = "0";
                                        }
                                        if (home_val.contains("%") && !home_val.equals("0")){

                                            String f_home_val = home_val.substring(0, home_val.length()-1);

                                            String f_away_val = away_val.substring(0, away_val.length()-1);

                                            stats.add(new Stat(type,f_home_val,f_away_val,false));

                                            Log.d("StringBuffers",home_val+"to "+f_home_val.toString()+" and "+away_val+" to "+f_away_val.toString());
                                        }else {

                                            stats.add(new Stat(type,home_val,away_val,true));

                                        }

                                        mRecyclerView.setAdapter(statAdapter);
                                        if (i ==(statistics.length()-1)){
                                            mSwipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                }else {
                                    Toast.makeText(context, "No stats available..!", Toast.LENGTH_SHORT).show();
                                    mSwipeRefreshLayout.setRefreshing(false);
                                   // FixtureInfo.viewPager.setCurrentItem(1,true);
                                    mLinearLayoutMain.setVisibility(View.GONE);
                                    mLinearLayoutSecond.setVisibility(View.VISIBLE);

                                }

                            }
                        }

                    }else {
                        Toast.makeText(context, "No stats available..!", Toast.LENGTH_SHORT).show();
                       // FixtureInfo.viewPager.setCurrentItem(1,true);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mLinearLayoutMain.setVisibility(View.GONE);
                        mLinearLayoutSecond.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                    mLinearLayoutMain.setVisibility(View.GONE);
                    mLinearLayoutSecond.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "[" + error.getMessage() + "]");
                mSwipeRefreshLayout.setRefreshing(false);
                mLinearLayoutMain.setVisibility(View.GONE);
                mLinearLayoutSecond.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("X-RapidAPI-Key", Constants._key);
                map.put("X-RapidAPI-Host", Constants._host);
                return map;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //adding the string request to request queue
        requestQueue.add(jsonObjectRequest);
    }
}