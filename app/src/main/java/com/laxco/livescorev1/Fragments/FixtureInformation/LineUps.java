package com.laxco.livescorev1.Fragments.FixtureInformation;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Lineupitem;
import com.laxco.livescorev1.Models.Stat;
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
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class LineUps extends Fragment {
    private View view;
    private OkHttpClient client;
    private okhttp3.Request request;
    private String url = "";
    private LinearLayout mLinearLayoutMain, mLinearLayoutSecond;
    private String TAG = "Demo";
    private Timer timer = new Timer();
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private Gson gson;
    private TextView mHomePLayer,mAwayPlayer,mHomeLineup,mAwayLineup,mHomeSub,mAwaySub,mHomeCoaches,mAwayCoaches;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FixtureInfo fixtureInfo = (FixtureInfo) getActivity();

        url = Constants.baseUrl + "fixtures/lineups?fixture="+fixtureInfo.getId();

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
        view = inflater.inflate(R.layout.fragment_squard, container, false);

        initViews();
        getData();

        return view;
    }

    private void initViews(){
        mHomeLineup = view.findViewById(R.id.tv_home_lineup);
        mAwayLineup = view.findViewById(R.id.tv_away_lineup);
        mHomePLayer = view.findViewById(R.id.tv_home_players);
        mAwayPlayer = view.findViewById(R.id.tv_away_players);
        mHomeCoaches = view.findViewById(R.id.tv_home_coach);
        mAwayCoaches = view.findViewById(R.id.tv_away_coach);
        mHomeSub = view.findViewById(R.id.tv_home_sub);
        mAwaySub = view.findViewById(R.id.tv_away_sub);
        mLinearLayoutMain = view.findViewById(R.id.linear_maain);
        mLinearLayoutSecond = view.findViewById(R.id.linear_maain_secon);
    }

    private void getData(){
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
                                                JSONObject home = responseArray.getJSONObject(0);
                                                JSONObject away = responseArray.getJSONObject(1);

                                                String h_ch = home.getJSONObject("coach").getString("name");
                                                String a_ch = away.getJSONObject("coach").getString("name");
                                                mAwayCoaches.setText(a_ch.equals("null")?"-":a_ch);
                                                mHomeCoaches.setText(h_ch.equals("null")?"-":h_ch);

                                                String h_ln = home.getString("formation");
                                                String a_ln = away.getString("formation");
                                                mAwayLineup.setText(a_ln.equals("null")?"-":a_ln);
                                                mHomeLineup.setText(h_ln.equals("null")?"-":h_ln);

                                                JSONArray startXI_home = home.getJSONArray("startXI");
                                                JSONArray startXI_away = away.getJSONArray("startXI");

                                                for (int i = 0; i < startXI_home.length(); i++) {
                                                    JSONObject jsonObject = startXI_home.getJSONObject(i);
                                                    String number = jsonObject.getJSONObject("player").getString("number");
                                                    String pos = jsonObject.getJSONObject("player").getString("pos");
                                                    String playerName = jsonObject.getJSONObject("player").getString("name");
                                                    String fNumber = "<font color='#808080'>" + number +","+pos+ "'</font><br>";
                                                    //set value to home scorer
                                                    mHomePLayer.append(Html.fromHtml(playerName.equals("null")?"-":playerName+" "+fNumber));
                                                }

                                                for (int i = 0; i < startXI_away.length(); i++) {
                                                    JSONObject jsonObject = startXI_away.getJSONObject(i);
                                                    String number = jsonObject.getJSONObject("player").getString("number");
                                                    String pos = jsonObject.getJSONObject("player").getString("pos");
                                                    String playerName = jsonObject.getJSONObject("player").getString("name");
                                                    String fNumber = "<font color='#808080'>" + number +","+pos+"'</font><br>";
                                                    //set value to home scorer
                                                    mAwayPlayer.append(Html.fromHtml(playerName.equals("null")?"-":playerName+" "+fNumber));
                                                }



                                                JSONArray substitutes_home = home.getJSONArray("substitutes");
                                                JSONArray substitutes_away = away.getJSONArray("substitutes");

                                                for (int i = 0; i < substitutes_home.length(); i++) {
                                                    JSONObject jsonObject = substitutes_home.getJSONObject(i);
                                                    String number = jsonObject.getJSONObject("player").getString("number");
                                                    String pos = jsonObject.getJSONObject("player").getString("pos");
                                                    String playerName = jsonObject.getJSONObject("player").getString("name");
                                                    String fNumber = "<font color='#808080'>" + number +","+pos+  "'</font><br>";
                                                    //set value to home scorer
                                                    mHomeSub.append(Html.fromHtml(playerName.equals("null")?"-":playerName+" "+fNumber));
                                                }

                                                for (int i = 0; i < substitutes_away.length(); i++) {
                                                    JSONObject jsonObject = substitutes_away.getJSONObject(i);
                                                    String number = jsonObject.getJSONObject("player").getString("number");
                                                    String pos = jsonObject.getJSONObject("player").getString("pos");
                                                    String playerName = jsonObject.getJSONObject("player").getString("name");
                                                    String fNumber = "<font color='#808080'>" + number +","+pos+  "'</font><br>";
                                                    //set value to home scorer
                                                    mAwaySub.append(Html.fromHtml(playerName.equals("null")?"-":playerName+" "+fNumber));
                                                }

                                                toggleView(false);


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