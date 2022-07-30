package com.laxco.livescorev1.Fragments.LeagueFrags;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.laxco.livescorev1.Activites.LeagueInfo;
import com.laxco.livescorev1.Adapters.Players.AssistAdapter;
import com.laxco.livescorev1.Adapters.Players.GoalAdapter;
import com.laxco.livescorev1.Adapters.Players.RedAdapter;
import com.laxco.livescorev1.Adapters.Players.ShotsAdapter;
import com.laxco.livescorev1.Adapters.Players.YellowAdapter;
import com.laxco.livescorev1.Adapters.StandingAdapter;
import com.laxco.livescorev1.Adapters.StandingAwayAdapter;
import com.laxco.livescorev1.Adapters.StandingHomeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandAwayScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandHomeScapeAdapter;
import com.laxco.livescorev1.Adapters.StandingLandScapeAdapter;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.PlayeState;
import com.laxco.livescorev1.Models.Standing;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
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

public class PlayerStats extends Fragment {
    private View mView;
    private Chip mAll,mGoals,mAssists,mRedCards,mYellowCards,mShortsOnTarget;
    private CardView mAllCardView,mGoalsCardView,mAssistsCardView,mRedCardsCardView,mYellowCardsCardView,mShortsOnTargetCardView;
    private LinearLayout mAllLinearLayout,mGoalsLinearLayout,mAssistsLinearLayout,mRedCardsLinearLayout,mYellowCardsLinearLayout,mShortsOnTargetLinearLayout;
    private RecyclerView mAllView,mGoalsView,mAssistsView,mRedCardsView,mYellowCardsView,mShortsOnTargetView;
    private String url;
    private OkHttpClient client;
    private okhttp3.Request request;
    private CardView mRotate;
    private ChipGroup mChipGroup;
    private AdView mAdView;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private String TAG = "Demo",season;
    private Gson gson;
    private List<PlayeState> playeStates;
    private GoalAdapter goalAdapter;
    private AssistAdapter assistAdapter;
    private RedAdapter redAdapter;
    private YellowAdapter yellowAdapter;
    private ShotsAdapter shotsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LeagueInfo leagueInfo = (LeagueInfo) getActivity();

        season = leagueInfo.getSeason();

        playeStates = new ArrayList<>();

        url= Constants.baseUrl+"players?league="+leagueInfo.getId()+"&season="+leagueInfo.getSeason();

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
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_player_stats, container, false);

        addUI();
        toggling();
        clicks();
        loadBunnerAd();

        return mView;
    }

    private void addUI(){

        mChipGroup = mView.findViewById(R.id.chip_group_main);

        mAll = mView.findViewById(R.id.chip_all);
        mGoals = mView.findViewById(R.id.chip_goals);
        mAssists = mView.findViewById(R.id.chip_assists);
        mRedCards = mView.findViewById(R.id.chip_red_cards);
        mYellowCards = mView.findViewById(R.id.chip_yellow_cards);
        mShortsOnTarget = mView.findViewById(R.id.chip_shorts_on_target);

        mGoalsCardView = mView.findViewById(R.id.card_view_all_goals);
        mAssistsCardView = mView.findViewById(R.id.card_view_all_assists);
        mRedCardsCardView = mView.findViewById(R.id.card_view_all_red);
        mYellowCardsCardView = mView.findViewById(R.id.card_view_all_yellow);
        mShortsOnTargetCardView = mView.findViewById(R.id.card_view_all_shorts);

        mAllView = mView.findViewById(R.id.rv_all);
        mGoalsView = mView.findViewById(R.id.rv_goals);
        mAssistsView = mView.findViewById(R.id.rv_assists);
        mRedCardsView = mView.findViewById(R.id.rv_red_cards);
        mYellowCardsView = mView.findViewById(R.id.rv_yellow_cards);
        mShortsOnTargetView = mView.findViewById(R.id.rv_shorts);

        mAllLinearLayout = mView.findViewById(R.id.linear_1);
        mGoalsLinearLayout = mView.findViewById(R.id.linear_2);
        mAssistsLinearLayout = mView.findViewById(R.id.linear_3);
        mRedCardsLinearLayout = mView.findViewById(R.id.linear_4);
        mYellowCardsLinearLayout = mView.findViewById(R.id.linear_5);
        mShortsOnTargetLinearLayout = mView.findViewById(R.id.linear_6);

        //loaing linearLayout managers
        final LinearLayoutManager layoutManagerResults = new LinearLayoutManager(getContext());
        layoutManagerResults.setOrientation(LinearLayoutManager.VERTICAL);

        mAllView.setItemAnimator(new DefaultItemAnimator());
        mAllView.setHasFixedSize(true);
        mAllView.setItemViewCacheSize(20);
        mAllView.setDrawingCacheEnabled(true);
        mAllView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mAllView.setLayoutManager(layoutManagerResults);

        final LinearLayoutManager layoutManagerGoals = new LinearLayoutManager(getContext());
        layoutManagerGoals.setOrientation(LinearLayoutManager.VERTICAL);

        mGoalsView.setItemAnimator(new DefaultItemAnimator());
        mGoalsView.setHasFixedSize(true);
        mGoalsView.setItemViewCacheSize(20);
        mGoalsView.setDrawingCacheEnabled(true);
        mGoalsView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mGoalsView.setLayoutManager(layoutManagerGoals);

        final LinearLayoutManager layoutManagerAssist = new LinearLayoutManager(getContext());
        layoutManagerGoals.setOrientation(LinearLayoutManager.VERTICAL);
        mAssistsView.setItemAnimator(new DefaultItemAnimator());
        mAssistsView.setHasFixedSize(true);
        mAssistsView.setItemViewCacheSize(20);
        mAssistsView.setDrawingCacheEnabled(true);
        mAssistsView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mAssistsView.setLayoutManager(layoutManagerAssist);

        final LinearLayoutManager layoutManagerRedCards = new LinearLayoutManager(getContext());
        layoutManagerGoals.setOrientation(LinearLayoutManager.VERTICAL);
        mRedCardsView.setItemAnimator(new DefaultItemAnimator());
        mRedCardsView.setHasFixedSize(true);
        mRedCardsView.setItemViewCacheSize(20);
        mRedCardsView.setDrawingCacheEnabled(true);
        mRedCardsView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRedCardsView.setLayoutManager(layoutManagerRedCards);

        final LinearLayoutManager layoutManagerYellowCards = new LinearLayoutManager(getContext());
        layoutManagerGoals.setOrientation(LinearLayoutManager.VERTICAL);
        mYellowCardsView.setItemAnimator(new DefaultItemAnimator());
        mYellowCardsView.setHasFixedSize(true);
        mYellowCardsView.setItemViewCacheSize(20);
        mYellowCardsView.setDrawingCacheEnabled(true);
        mYellowCardsView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mYellowCardsView.setLayoutManager(layoutManagerYellowCards);

        final LinearLayoutManager layoutManagerOnTarget = new LinearLayoutManager(getContext());
        layoutManagerGoals.setOrientation(LinearLayoutManager.VERTICAL);
        mShortsOnTargetView.setItemAnimator(new DefaultItemAnimator());
        mShortsOnTargetView.setHasFixedSize(true);
        mShortsOnTargetView.setItemViewCacheSize(20);
        mShortsOnTargetView.setDrawingCacheEnabled(true);
        mShortsOnTargetView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mShortsOnTargetView.setLayoutManager(layoutManagerOnTarget);

        mAll.setChecked(true);

        loadData();

    }

    private void clicks(){
        mGoalsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoals.setChecked(true);
            }
        });
        mAssistsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAssists.setChecked(true);
            }
        });
        mRedCardsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRedCards.setChecked(true);
            }
        });
        mYellowCardsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mYellowCards.setChecked(true);
            }
        });
        mShortsOnTargetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShortsOnTarget.setChecked(true);
            }
        });
    }

    private void toggling(){
        mAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mAllLinearLayout.setVisibility(View.VISIBLE);
                    mGoalsLinearLayout.setVisibility(View.VISIBLE);
                    mAssistsLinearLayout.setVisibility(View.VISIBLE);
                    mRedCardsLinearLayout.setVisibility(View.VISIBLE);
                    mYellowCardsLinearLayout.setVisibility(View.VISIBLE);
                    mShortsOnTargetLinearLayout.setVisibility(View.VISIBLE);

                    mAdView.setVisibility(View.VISIBLE);

                    if (playeStates.size()>5){
                        goalAdapter = new GoalAdapter(getContext(), playeStates.subList(0,5));
                        assistAdapter = new AssistAdapter(getContext(), playeStates.subList(0,5));
                        redAdapter = new RedAdapter(getContext(), playeStates.subList(0,5));
                        yellowAdapter = new YellowAdapter(getContext(), playeStates.subList(0,5));
                        shotsAdapter = new ShotsAdapter(getContext(), playeStates.subList(0,5));

                        goalAdapter.setHasStableIds(true);
                        assistAdapter.setHasStableIds(true);
                        redAdapter.setHasStableIds(true);
                        yellowAdapter.setHasStableIds(true);
                        shotsAdapter.setHasStableIds(true);

                        mGoalsView.setAdapter(goalAdapter);
                        mAssistsView.setAdapter(assistAdapter);
                        mRedCardsView.setAdapter(redAdapter);
                        mYellowCardsView.setAdapter(yellowAdapter);
                        mShortsOnTargetView.setAdapter(shotsAdapter);

                        goalAdapter.notifyDataSetChanged();
                        assistAdapter.notifyDataSetChanged();
                        redAdapter.notifyDataSetChanged();
                        yellowAdapter.notifyDataSetChanged();
                        shotsAdapter.notifyDataSetChanged();
                    }else {
                        goalAdapter = new GoalAdapter(getContext(), playeStates);
                        assistAdapter = new AssistAdapter(getContext(), playeStates);
                        redAdapter = new RedAdapter(getContext(), playeStates);
                        yellowAdapter = new YellowAdapter(getContext(), playeStates);
                        shotsAdapter = new ShotsAdapter(getContext(), playeStates);

                        goalAdapter.setHasStableIds(true);
                        assistAdapter.setHasStableIds(true);
                        redAdapter.setHasStableIds(true);
                        yellowAdapter.setHasStableIds(true);
                        shotsAdapter.setHasStableIds(true);

                        mGoalsView.setAdapter(goalAdapter);
                        mAssistsView.setAdapter(assistAdapter);
                        mRedCardsView.setAdapter(redAdapter);
                        mYellowCardsView.setAdapter(yellowAdapter);
                        mShortsOnTargetView.setAdapter(shotsAdapter);

                        goalAdapter.notifyDataSetChanged();
                        assistAdapter.notifyDataSetChanged();
                        redAdapter.notifyDataSetChanged();
                        yellowAdapter.notifyDataSetChanged();
                        shotsAdapter.notifyDataSetChanged();

                        mAdView.setVisibility(View.GONE);
                    }

                }else {

                    mAllLinearLayout.setVisibility(View.GONE);
                    mGoalsLinearLayout.setVisibility(View.GONE);
                    mAssistsLinearLayout.setVisibility(View.GONE);
                    mRedCardsLinearLayout.setVisibility(View.GONE);
                    mYellowCardsLinearLayout.setVisibility(View.GONE);
                    mShortsOnTargetLinearLayout.setVisibility(View.GONE);


                    goalAdapter = new GoalAdapter(getContext(), playeStates);
                    assistAdapter = new AssistAdapter(getContext(), playeStates);
                    redAdapter = new RedAdapter(getContext(), playeStates);
                    yellowAdapter = new YellowAdapter(getContext(), playeStates);
                    shotsAdapter = new ShotsAdapter(getContext(), playeStates);

                    goalAdapter.setHasStableIds(true);
                    assistAdapter.setHasStableIds(true);
                    redAdapter.setHasStableIds(true);
                    yellowAdapter.setHasStableIds(true);
                    shotsAdapter.setHasStableIds(true);

                    mGoalsView.setAdapter(goalAdapter);
                    mAssistsView.setAdapter(assistAdapter);
                    mRedCardsView.setAdapter(redAdapter);
                    mYellowCardsView.setAdapter(yellowAdapter);
                    mShortsOnTargetView.setAdapter(shotsAdapter);

                    goalAdapter.notifyDataSetChanged();
                    assistAdapter.notifyDataSetChanged();
                    redAdapter.notifyDataSetChanged();
                    yellowAdapter.notifyDataSetChanged();
                    shotsAdapter.notifyDataSetChanged();
                }
            }
        });
        mGoals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mGoalsLinearLayout.setVisibility(View.VISIBLE);

                    mAdView.setVisibility(View.VISIBLE);

                    goalAdapter = new GoalAdapter(getContext(), playeStates);
                    goalAdapter.setHasStableIds(true);
                    mGoalsView.setAdapter(goalAdapter);
                    goalAdapter.notifyDataSetChanged();

                    mGoalsCardView.setVisibility(View.GONE);

                }else {
                    mGoalsLinearLayout.setVisibility(View.GONE);
                    mGoalsCardView.setVisibility(View.VISIBLE);
                }
            }
        });

        mAssists.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    mAssistsLinearLayout.setVisibility(View.VISIBLE);

                    mAdView.setVisibility(View.VISIBLE);
                    mAssistsCardView.setVisibility(View.GONE);

                    assistAdapter = new AssistAdapter(getContext(), playeStates);
                    assistAdapter.setHasStableIds(true);
                    mAssistsView.setAdapter(assistAdapter);
                    assistAdapter.notifyDataSetChanged();
                }else {
                    mAssistsLinearLayout.setVisibility(View.GONE);
                    mAssistsCardView.setVisibility(View.VISIBLE);
                }
            }
        });

        mRedCards.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    mRedCardsLinearLayout.setVisibility(View.VISIBLE);

                    mAdView.setVisibility(View.VISIBLE);
                    mRedCardsCardView.setVisibility(View.GONE);

                    redAdapter = new RedAdapter(getContext(), playeStates);
                    redAdapter.setHasStableIds(true);
                    mRedCardsView.setAdapter(redAdapter);
                    redAdapter.notifyDataSetChanged();
                }else {
                    mRedCardsLinearLayout.setVisibility(View.GONE);
                    mRedCardsCardView.setVisibility(View.VISIBLE);
                }
            }
        });

        mYellowCards.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    mYellowCardsLinearLayout.setVisibility(View.VISIBLE);

                    mAdView.setVisibility(View.VISIBLE);
                    mYellowCardsCardView.setVisibility(View.GONE);

                    yellowAdapter = new YellowAdapter(getContext(), playeStates);
                    yellowAdapter.setHasStableIds(true);
                    mYellowCardsView.setAdapter(yellowAdapter);
                    yellowAdapter.notifyDataSetChanged();
                }else {
                    mYellowCardsLinearLayout.setVisibility(View.GONE);
                    mYellowCardsCardView.setVisibility(View.VISIBLE);
                }
            }
        });

        mShortsOnTarget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    mShortsOnTargetLinearLayout.setVisibility(View.VISIBLE);
                    mShortsOnTargetCardView.setVisibility(View.GONE);

                    mAdView.setVisibility(View.VISIBLE);

                    shotsAdapter = new ShotsAdapter(getContext(), playeStates);
                    shotsAdapter.setHasStableIds(true);
                    mShortsOnTargetView.setAdapter(shotsAdapter);
                    shotsAdapter.notifyDataSetChanged();
                }else {
                    mShortsOnTargetLinearLayout.setVisibility(View.GONE);
                    mShortsOnTargetCardView.setVisibility(View.VISIBLE);
                }
            }
        });


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
                            if (getActivity() !=null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void run() {
                                        try {
                                            String responseData = response.body().string();
                                            JSONArray responseArray = new JSONObject(responseData).getJSONArray("response");

                                            if (responseArray.length() > 0) {

                                                for (int i = 0; i < responseArray.length(); i++) {
                                                    JSONObject jsonObject = responseArray.getJSONObject(i);

                                                    JSONObject player = jsonObject.getJSONObject("player");
                                                    JSONArray statistics = jsonObject.getJSONArray("statistics");

                                                    //adding to model
                                                    playeStates.add(new PlayeState(player, statistics));

                                                    if (i == (responseArray.length() - 1)) {

                                                        if (playeStates.size() > 5) {
                                                            goalAdapter = new GoalAdapter(getContext(), playeStates.subList(0, 5));
                                                            assistAdapter = new AssistAdapter(getContext(), playeStates.subList(0, 5));
                                                            redAdapter = new RedAdapter(getContext(), playeStates.subList(0, 5));
                                                            yellowAdapter = new YellowAdapter(getContext(), playeStates.subList(0, 5));
                                                            shotsAdapter = new ShotsAdapter(getContext(), playeStates.subList(0, 5));

                                                            goalAdapter.setHasStableIds(true);
                                                            assistAdapter.setHasStableIds(true);
                                                            redAdapter.setHasStableIds(true);
                                                            yellowAdapter.setHasStableIds(true);
                                                            shotsAdapter.setHasStableIds(true);

                                                            mGoalsView.setAdapter(goalAdapter);
                                                            mAssistsView.setAdapter(assistAdapter);
                                                            mRedCardsView.setAdapter(redAdapter);
                                                            mYellowCardsView.setAdapter(yellowAdapter);
                                                            mShortsOnTargetView.setAdapter(shotsAdapter);

                                                            goalAdapter.notifyDataSetChanged();
                                                            assistAdapter.notifyDataSetChanged();
                                                            redAdapter.notifyDataSetChanged();
                                                            yellowAdapter.notifyDataSetChanged();
                                                            shotsAdapter.notifyDataSetChanged();
                                                        } else {
                                                            goalAdapter = new GoalAdapter(getContext(), playeStates);
                                                            assistAdapter = new AssistAdapter(getContext(), playeStates);
                                                            redAdapter = new RedAdapter(getContext(), playeStates);
                                                            yellowAdapter = new YellowAdapter(getContext(), playeStates);
                                                            shotsAdapter = new ShotsAdapter(getContext(), playeStates);

                                                            goalAdapter.setHasStableIds(true);
                                                            assistAdapter.setHasStableIds(true);
                                                            redAdapter.setHasStableIds(true);
                                                            yellowAdapter.setHasStableIds(true);
                                                            shotsAdapter.setHasStableIds(true);

                                                            mGoalsView.setAdapter(goalAdapter);
                                                            mAssistsView.setAdapter(assistAdapter);
                                                            mRedCardsView.setAdapter(redAdapter);
                                                            mYellowCardsView.setAdapter(yellowAdapter);
                                                            mShortsOnTargetView.setAdapter(shotsAdapter);

                                                            goalAdapter.notifyDataSetChanged();
                                                            assistAdapter.notifyDataSetChanged();
                                                            redAdapter.notifyDataSetChanged();
                                                            yellowAdapter.notifyDataSetChanged();
                                                            shotsAdapter.notifyDataSetChanged();
                                                        }

                                                        response.body().close();
                                                        //  toggleView(false);
                                                    }
                                                }
                                            } else {
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



    private void loadBunnerAd() {
        mAdView = mView.findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getResources().getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }
        });
    }
}