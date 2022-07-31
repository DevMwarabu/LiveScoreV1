package com.laxco.livescorev1.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import com.laxco.livescorev1.Adapters.CountryAdapter;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.OtherLeagueAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Country;
import com.laxco.livescorev1.Models.League;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllLeagues extends AppCompatActivity {

    private List<League> leagues;
    private OtherLeagueAdapter otherLeagueAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<String> leagueIds = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private NestedScrollView mScrollView;
    private HttpUrl.Builder urlBuilder;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String TAG = "Demo",code,url;
    private Gson gson;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_leagues);


        code = getIntent().getExtras().getString("code");
        url = Constants.baseUrl + "leagues?code="+code+"&current=true";


        //initilizing okhttp varriables
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(ConnectionSpec.COMPATIBLE_TLS))
                .build();

        request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", Constants._key)
                .addHeader("X-RapidAPI-Host", Constants._host)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        gson = new Gson();

        leagues = new ArrayList<>();

        mSearchView = findViewById(R.id.searchview);
        mRecyclerView = findViewById(R.id.rv_main);
        mSwipeRefreshLayout = findViewById(R.id.swipe_main);
        //mScrollView = findViewById(R.id.nested_main);

        //load recycle
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
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
                loaData();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                otherLeagueAdapter.clear();
                otherLeagueAdapter.addAll(leagues);
                loaData();

            }
        });


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                otherLeagueAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                otherLeagueAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void loaData() {
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        //usimgUIThread
                        if (response.isSuccessful()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String responseData = response.body().string();
                                        JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");

                                        for (int i = 0; i < responseArray.length(); i++) {
                                            JSONObject jsonObject = responseArray.getJSONObject(i);

                                            JSONObject league = jsonObject.getJSONObject("league");
                                            JSONObject country = jsonObject.getJSONObject("country");
                                            JSONArray seasons = jsonObject.getJSONArray("seasons");
                                            //adding to model
                                            leagues.add(new League(league,country,seasons));

                                            if (i == (responseArray.length() - 1)) {
                                                otherLeagueAdapter = new OtherLeagueAdapter(AllLeagues.this, leagues);
                                                otherLeagueAdapter.setHasStableIds(true);
                                                mRecyclerView.setAdapter(otherLeagueAdapter);

                                                response.body().close();
                                                mSwipeRefreshLayout.setRefreshing(false);
                                                otherLeagueAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(AllLeagues.this, "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }
}