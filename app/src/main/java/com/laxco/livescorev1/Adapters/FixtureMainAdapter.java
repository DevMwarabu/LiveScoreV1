package com.laxco.livescorev1.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Fragments.MainFrags.FragToday;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.LeagueMain;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FixtureMainAdapter extends RecyclerView.Adapter<FixtureMainAdapter.ViewHolder> {
    private Context context;
    private List<LeagueMain> leagueMains;

    public FixtureMainAdapter(Context context, List<LeagueMain> leagueMains) {
        this.context = context;
        this.leagueMains = leagueMains;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_dash = LayoutInflater.from(parent.getContext()).inflate(R.layout.games_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view_dash);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeagueMain leagueMain = leagueMains.get(position);

        holder.setData(leagueMain.getName(),leagueMain.getCountry(),leagueMain.getLogo(),leagueMain.getId(),leagueMain.getDate());
    }


    public void clear() {
        leagueMains.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<LeagueMain> leagueMains) {
        leagueMains.addAll(leagueMains);
    }

    @Override
    public int getItemCount() {
        return leagueMains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mLName,mLCountry;
        private ImageView imageView;
        private List<Fixture> fixtures;
        private FixtureAdapter fixtureAdapter;
        private RecyclerView mRecyclerView;
        private Timer timer = new Timer();
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fixtures = new ArrayList<>();
            fixtureAdapter = new FixtureAdapter(itemView.getContext(),fixtures);

            mLName = itemView.findViewById(R.id.tv_league_name);
            mLCountry = itemView.findViewById(R.id.tv_league_country);
            imageView = itemView.findViewById(R.id.image_league_logo);
            mRecyclerView = itemView.findViewById(R.id.rv_main);

            //load recycle
            final LinearLayoutManager layoutManager = new GridLayoutManager(itemView.getContext(), 1, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemViewCacheSize(20);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);



        }
        private void setData(String name,String country,String logo,String id,String date){
            mLCountry.setText(country);
            mLName.setText(name);
            Glide.with(context.getApplicationContext()).load(logo)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.ic_baseline_terrain_24)
                    .error(R.drawable.ic_baseline_terrain_24)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(imageView);

            loadData(id,date);


//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                }
//            }, 0, 1000*60);
        }

        private void loadData(String id,String date){
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl+"fixtures?date="+ date, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        Log.d("TAG",response.toString());

                        JSONArray jsonArray = response.getJSONArray("response");

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);


                                JSONObject fixture = jsonObject.getJSONObject("fixture");
                                JSONObject teams = jsonObject.getJSONObject("teams");
                                JSONObject goals = jsonObject.getJSONObject("goals");
                                JSONObject league = jsonObject.getJSONObject("league");
                                String elapsed = fixture.getJSONObject("status").getString("elapsed");
                                String home = teams.getJSONObject("home").getString("name");
                                String away = teams.getJSONObject("away").getString("name");
                                String leaguename = league.getString("name");
                                //adding to model

                                if (league.getString("id").equals(id)){
                                    //adding to model
                                    fixtures.add(new Fixture(teams,goals,league,fixture,elapsed,home,away,leaguename));


                                    Log.d("AllF leagues fIX", "" + fixture.toString());

                                }

                                mRecyclerView.setAdapter(fixtureAdapter);
                                fixtureAdapter.notifyItemChanged(i);
                            }
                        }else {
                            Toast.makeText(itemView.getContext(), "No live matches going on..!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("res_beer", "[" + e.getMessage() + "]");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("res_beer", "[" + error.getMessage() + "]");
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
            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
            //adding the string request to request queue
            requestQueue.add(jsonObjectRequest);
        }
    }
}
