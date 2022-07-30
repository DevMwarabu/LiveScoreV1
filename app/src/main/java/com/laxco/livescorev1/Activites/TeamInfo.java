package com.laxco.livescorev1.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.gson.Gson;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.LineupAdapter;
import com.laxco.livescorev1.Adapters.Players.AssistAdapter;
import com.laxco.livescorev1.Adapters.Players.GoalAdapter;
import com.laxco.livescorev1.Adapters.Players.RedAdapter;
import com.laxco.livescorev1.Adapters.Players.ShotsAdapter;
import com.laxco.livescorev1.Adapters.Players.YellowAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Lineup;
import com.laxco.livescorev1.Models.PlayeState;
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

public class TeamInfo extends AppCompatActivity {
    private CardView mBack;
    private TextView mName,mLeague,mPH,mWH,mDH,mLH,mFH,mAH,mGDH,mPTSH,mPA,mWA,mDA,mLA,mFA,mAA,mGDA,mPTSA,
    mWinHome,mWinAway,mLoseHome,mLosseAway,mPENcored,mPENMIssed,mPTT,mWTT,mDTT,mLTT,mFTT,mATT,mGDTT,mPTSTT;
    private RecyclerView mRecyclerView;
    private ImageView mLogo;
    private String id,season,leagueId;
    private OkHttpClient client;
    private okhttp3.Request request;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private String url;
    private String TAG = "Demo";
    private Gson gson;
    private List<Lineup> lineups;
    private LineupAdapter lineupAdapter;

    private NativeAd native_Ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_info);
        initViews();

        lineups = new ArrayList<>();

        Native_Ads_loaded();


        id = getIntent().getExtras().getString("id");
        season = getIntent().getExtras().getString("season");
        leagueId = getIntent().getExtras().getString("leagueId");


        url= Constants.baseUrl+"teams/statistics?league="+leagueId+"&season="+season+"&team="+id;

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


        final LinearLayoutManager layoutManagerResults = new LinearLayoutManager(this);
        layoutManagerResults.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(layoutManagerResults);




        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadData();
    }
    private void initViews(){
        mBack = findViewById(R.id.card_back);
        mName = findViewById(R.id.tv_team_name);
        mLeague = findViewById(R.id.tv_league_name);
        mPH = findViewById(R.id.tv_plays);
        mWH = findViewById(R.id.tv_w);
        mDH = findViewById(R.id.tv_d);
        mLH = findViewById(R.id.tv_l);
        mFH = findViewById(R.id.tv_f);
        mAH = findViewById(R.id.tv_a);
        mGDH = findViewById(R.id.tv_gd);
        mPTSH = findViewById(R.id.tv_points);
        mPA = findViewById(R.id.tv_plays_a);
        mWA = findViewById(R.id.tv_w_a);
        mDA = findViewById(R.id.tv_d_a);
        mLA = findViewById(R.id.tv_l_a);
        mFA = findViewById(R.id.tv_f_a);
        mAA = findViewById(R.id.tv_a_a);
        mGDA = findViewById(R.id.tv_gd_a);
        mPTSA = findViewById(R.id.tv_points_a);
        mWinHome = findViewById(R.id.tv_b_win_home);
        mWinAway = findViewById(R.id.tv_b_win_away);
        mLosseAway = findViewById(R.id.tv_b_loss_away);
        mLoseHome = findViewById(R.id.tv_b_loss_home);
        mPENcored = findViewById(R.id.tv_penalties_scored);
        mPENMIssed = findViewById(R.id.tv_penalties_missed);
        mPTT = findViewById(R.id.tv_plays_ttl);
        mWTT = findViewById(R.id.tv_w_ttl);
        mDTT = findViewById(R.id.tv_d_ttl);
        mLTT = findViewById(R.id.tv_l_ttl);
        mFTT = findViewById(R.id.tv_f_ttl);
        mATT = findViewById(R.id.tv_a_ttl);
        mGDTT = findViewById(R.id.tv_gd_ttl);
        mPTSTT = findViewById(R.id.tv_points_ttl);
        mRecyclerView = findViewById(R.id.rv_main);
        mLogo = findViewById(R.id.image_team_logo);
    }


    private void loadData(){

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
                            runOnUiThread(new Runnable() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void run() {
                                    try {
                                        String responseData = response.body().string();
                                        JSONObject data = new JSONObject(responseData).getJSONObject("response");
                                        mLeague.setText(data.getJSONObject("league").getString("name"));
                                        mName.setText(data.getJSONObject("team").getString("name"));

                                        mPH.setText(data.getJSONObject("fixtures").getJSONObject("played").getString("home"));
                                        mPA.setText(data.getJSONObject("fixtures").getJSONObject("played").getString("away"));
                                        mPTT.setText(data.getJSONObject("fixtures").getJSONObject("played").getString("total"));

                                        mWH.setText(data.getJSONObject("fixtures").getJSONObject("wins").getString("home"));
                                        mWA.setText(data.getJSONObject("fixtures").getJSONObject("wins").getString("away"));
                                        mWTT.setText(data.getJSONObject("fixtures").getJSONObject("wins").getString("total"));

                                        mDH.setText(data.getJSONObject("fixtures").getJSONObject("draws").getString("home"));
                                        mDA.setText(data.getJSONObject("fixtures").getJSONObject("draws").getString("away"));
                                        mDTT.setText(data.getJSONObject("fixtures").getJSONObject("draws").getString("total"));

                                        mLH.setText(data.getJSONObject("fixtures").getJSONObject("loses").getString("home"));
                                        mLA.setText(data.getJSONObject("fixtures").getJSONObject("loses").getString("away"));
                                        mLTT.setText(data.getJSONObject("fixtures").getJSONObject("loses").getString("total"));

                                        mFH.setText(data.getJSONObject("goals").getJSONObject("for").getJSONObject("total").getString("home"));
                                        mFA.setText(data.getJSONObject("goals").getJSONObject("for").getJSONObject("total").getString("away"));
                                        mFTT.setText(data.getJSONObject("goals").getJSONObject("for").getJSONObject("total").getString("total"));

                                        mAH.setText(data.getJSONObject("goals").getJSONObject("against").getJSONObject("total").getString("home"));
                                        mAA.setText(data.getJSONObject("goals").getJSONObject("against").getJSONObject("total").getString("away"));
                                        mATT.setText(data.getJSONObject("goals").getJSONObject("against").getJSONObject("total").getString("total"));

                                        mGDH.setText(String.valueOf((Integer.parseInt(data.getJSONObject("goals").getJSONObject("for").getJSONObject("total").getString("home"))-Integer.parseInt(data.getJSONObject("goals").getJSONObject("against").getJSONObject("total").getString("home")))));
                                        mGDA.setText(String.valueOf((Integer.parseInt(data.getJSONObject("goals").getJSONObject("for").getJSONObject("total").getString("away"))-Integer.parseInt(data.getJSONObject("goals").getJSONObject("against").getJSONObject("total").getString("away")))));
                                        mGDTT.setText(String.valueOf((Integer.parseInt(data.getJSONObject("goals").getJSONObject("for").getJSONObject("total").getString("total"))-Integer.parseInt(data.getJSONObject("goals").getJSONObject("against").getJSONObject("total").getString("total")))));

                                        int pts_a = (Integer.parseInt(data.getJSONObject("fixtures").getJSONObject("wins").getString("away"))*3)+ Integer.parseInt(data.getJSONObject("fixtures").getJSONObject("draws").getString("away"));
                                        int pts_h = (Integer.parseInt(data.getJSONObject("fixtures").getJSONObject("wins").getString("home"))*3)+ Integer.parseInt(data.getJSONObject("fixtures").getJSONObject("draws").getString("home"));

                                        mPTSA.setText(String.valueOf(pts_a));
                                        mPTSH.setText(String.valueOf(pts_h));
                                        mPTSTT.setText(String.valueOf((pts_a+pts_h)));

                                        mWinAway.setText(data.getJSONObject("biggest").getJSONObject("wins").getString("away"));
                                        mWinHome.setText(data.getJSONObject("biggest").getJSONObject("wins").getString("home"));

                                        mLoseHome.setText(data.getJSONObject("biggest").getJSONObject("loses").getString("home"));
                                        mLosseAway.setText(data.getJSONObject("biggest").getJSONObject("loses").getString("away"));

                                        mPENcored.setText(data.getJSONObject("penalty").getJSONObject("scored").getString("total"));
                                        mPENMIssed.setText(data.getJSONObject("penalty").getJSONObject("missed").getString("total"));

                                        //setting images logos
                                        Glide.with(getApplicationContext()).load(data.getJSONObject("team").getString("logo"))
                                                .apply(new RequestOptions()
                                                        .fitCenter()
                                                        .dontTransform()
                                                        .format(DecodeFormat.PREFER_ARGB_8888))
                                                .placeholder(R.drawable.pld)
                                                .error(R.drawable.pld)
                                                .dontAnimate()
                                                .placeholder(R.drawable.pld)
                                                .into(mLogo);

                                        //getting formation
                                        JSONArray lineupsArray = data.getJSONArray("lineups");
                                        for (int i=0; i<lineupsArray.length(); i++){
                                            JSONObject jsonObject = lineupsArray.getJSONObject(i);
                                            String formation = jsonObject.getString("formation");
                                            String played = jsonObject.getString("played");

                                            lineups.add(new Lineup(formation,played));

                                            if (i == (lineupsArray.length() - 1)) {
                                                lineupAdapter = new LineupAdapter(TeamInfo.this,lineups);
                                                lineupAdapter.setHasStableIds(true);
                                                mRecyclerView.setAdapter(lineupAdapter);

                                                response.body().close();
                                                lineupAdapter.notifyDataSetChanged();
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
                            Toast.makeText(TeamInfo.this, "Something went wrong please try again...!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }



    private void Native_Ads_loaded() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.native_Ad));

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        boolean isDestroyed = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = isDestroyed();
                        }
                        if (isDestroyed) {
                            nativeAd.destroy();
                            return;
                        }
                        if (native_Ads != null) {
                            native_Ads.destroy();
                        }
                        native_Ads = nativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                String error = String.format(
                        "domain: %s, code: %d, message: %s",
                        loadAdError.getDomain(),
                        loadAdError.getCode(),
                        loadAdError.getMessage());
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
        VideoController vc = nativeAd.getMediaContent().getVideoController();
        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }
}