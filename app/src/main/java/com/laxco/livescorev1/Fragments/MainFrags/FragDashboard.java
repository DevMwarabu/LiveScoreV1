package com.laxco.livescorev1.Fragments.MainFrags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import com.fede987.statusbaralert.StatusBarAlert;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Adapters.FixtureAdapter;
import com.laxco.livescorev1.Adapters.LeagueAdapter;
import com.laxco.livescorev1.Adapters.MatchAdapter;
import com.laxco.livescorev1.Adapters.MatchAdapterNext;
import com.laxco.livescorev1.Adapters.StandingAdapter;
import com.laxco.livescorev1.BuildConfig;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Dashboard;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.League;
import com.laxco.livescorev1.Models.Match;
import com.laxco.livescorev1.Models.MatchNext;
import com.laxco.livescorev1.Models.Standing;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FragDashboard extends Fragment {
    View view;
    private CardView mLiveMatches,mAllMatches,mSearch,mFilter;
    private RecyclerView mRvLeagues,mRvNext,mRvLast;
    private ProgressBar mProgressBar;
    private NestedScrollView mScrollView;
    private TextView mCountry,mLeague;
    private Spinner mSpinner;
    private ImageView mLeagueLogo;
    private ViewPager mViewPager;

    private List<League> leagues;
    private LeagueAdapter leagueAdapter;

    private MatchAdapter matchAdapter;
    private List<Match> matches;

    private MatchAdapterNext matchAdapterNext;
    private List<MatchNext> matchNexts;

    private String TAG = BuildConfig.APPLICATION_ID;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] leagueIds = {"39","78","140","135","79","61","169","253","94","71"};
    private ArrayList<String> seasons = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    private NativeAd native_Ads;
    private AdView mAdView;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private StatusBarAlert statusBarAlert;
    private LinearLayout mLinearLayoutMain,mLinearLayoutSecond;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        view =  inflater.inflate(R.layout.fragment_frag_dashboard, container, false);

        // Start loading ads here...
        loadBunnerAd();
        loadInterstitial();
        Native_Ads_loaded();

        mViewPager = Dashboard.viewPager;


        mSpinner = view.findViewById(R.id.spinner_seasons);


       // loadSpinner(Calendar.getInstance().get(Calendar.YEAR));

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Loading data...");

        leagues = new ArrayList<>();
        leagueAdapter = new LeagueAdapter(getContext(),leagues);

        matches = new ArrayList<>();
        matchAdapter = new MatchAdapter(getContext(),matches);

        matchNexts = new ArrayList<>();
        matchAdapterNext = new MatchAdapterNext(getContext(),matchNexts);


        mLiveMatches = view.findViewById(R.id.card_live);
        mAllMatches = view.findViewById(R.id.card_all);
        mSwipeRefreshLayout =view.findViewById(R.id.swipe_main);
        mSearch = view.findViewById(R.id.card_search);
        mFilter = view.findViewById(R.id.card_filter);
        mRvNext = view.findViewById(R.id.rv_next_matches);
        mRvLast =view. findViewById(R.id.rv_last_matches);
        mRvLeagues = view.findViewById(R.id.rv_leagues);
        mScrollView = view.findViewById(R.id.nested_main);
        mCountry = view.findViewById(R.id.tv_league_country);
        mLeague = view.findViewById(R.id.tv_league_name);
        mLeagueLogo = view.findViewById(R.id.image_league_logo);
        mLinearLayoutMain = view.findViewById(R.id.linear_maain);
        mLinearLayoutSecond = view.findViewById(R.id.linear_maain_secon);

        final LinearLayoutManager layoutManagerLeagues = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        mRvLeagues.setLayoutManager(layoutManagerLeagues);
        mRvLeagues.setItemAnimator(new DefaultItemAnimator());
        mRvLeagues.setHasFixedSize(true);
        mRvLeagues.setItemViewCacheSize(20);
        mRvLeagues.setDrawingCacheEnabled(true);
        mRvLeagues.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        final LinearLayoutManager layoutManagerMatch = new LinearLayoutManager(getContext(),  LinearLayoutManager.VERTICAL, false);
        mRvLast.setLayoutManager(layoutManagerMatch);
        mRvLast.setItemAnimator(new DefaultItemAnimator());
        mRvLast.setHasFixedSize(true);
        mRvLast.setItemViewCacheSize(20);
        mRvLast.setDrawingCacheEnabled(true);
        mRvLast.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        final LinearLayoutManager layoutManagerMatchNext = new LinearLayoutManager(getContext(),  LinearLayoutManager.VERTICAL, false);
        mRvNext.setLayoutManager(layoutManagerMatchNext);
        mRvNext.setItemAnimator(new DefaultItemAnimator());
        mRvNext.setHasFixedSize(true);
        mRvNext.setItemViewCacheSize(20);
        mRvNext.setDrawingCacheEnabled(true);
        mRvNext.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);



        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
    //            load in priority
                for (String id : leagueIds){
                    loadLeaguesData(id);
                }
                loadLast15Data(3);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                leagueAdapter.clear();
                leagueAdapter.addAll(leagues);

                matches.clear();
                matchAdapter.addAll(matches);

                matchNexts.clear();
                matchAdapterNext.addAll(matchNexts);

                //load in priority
                for (String id : leagueIds){
                    loadLeaguesData(id);
                }
                loadLast15Data(3);

            }
        });

        //load standings separately
        //loadStandings("39",String.valueOf((Calendar.getInstance().get(Calendar.YEAR))));

//        SpinnerInteractionListener listener = new SpinnerInteractionListener();
//        mSpinner.setOnTouchListener(listener);
//        mSpinner.setOnItemSelectedListener(listener);

        mLiveMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getContext());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            startActivity(new Intent(getContext(), MoreLives.class).putExtra("isLive",true).putExtra("title","Live Games"));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    startActivity(new Intent(getContext(), MoreLives.class).putExtra("isLive",true).putExtra("title","Live Games"));
                }
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getContext());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                           // startActivity(new Intent(getContext(), MoreLives.class).putExtra("isLeague",false).putExtra("isTeam",false));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    //startActivity(new Intent(getContext(), MoreLives.class).putExtra("isLeague",false).putExtra("isTeam",false));
                }
            }
        });



        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getContext());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            // startActivity(new Intent(Dashboard.getContext(), CombinedUi.class).putExtra("position", 0).putExtra("isNew", false).putExtra("isFinishing",false));
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    //startActivity(new Intent(Dashboard.getContext(), CombinedUi.class).putExtra("position", 0).putExtra("isNew", false).putExtra("isFinishing",false));
                }
            }
        });

        return view;

    }

    @Override
    public void onDestroyView() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (native_Ads != null) {
            native_Ads.destroy();
        }
        super.onDestroyView();
    }

    private void loadLeaguesData(String id){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl+"leagues?id="+id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG+"leagues_data",response.toString());

                    JSONArray jsonArray = response.getJSONArray("response");

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            JSONObject league = jsonObject.getJSONObject("league");
                            JSONObject country = jsonObject.getJSONObject("country");
                            JSONArray seasons = jsonObject.getJSONArray("seasons");
                            //adding to model
                            leagues.add(new League(league,country,seasons));

                            if (id.equals("39")){
                                setMianLeaguButton(country.getString("name"),league.getString("name"),league.getString("logo"));
                            }

                            mRvLeagues.setAdapter(leagueAdapter);
                            leagueAdapter.notifyItemChanged(i);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer", "[" + error.getMessage() + "]");
                mSwipeRefreshLayout.setRefreshing(false);
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

    private void loadLast15Data(int pages){
        mProgressDialog.setMessage("Loading matches...");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl+"fixtures?last="+pages, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG+"last resp",response.toString());

                    JSONArray jsonArray = response.getJSONArray("response");

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            JSONObject fixture = jsonObject.getJSONObject("fixture");
                            JSONObject teams = jsonObject.getJSONObject("teams");
                            JSONObject goals = jsonObject.getJSONObject("goals");
                            JSONObject league = jsonObject.getJSONObject("league");
                            //adding to model
                            matches.add(new Match(teams,goals,league,fixture));

                            mRvLast.setAdapter(matchAdapter);
                            matchAdapter.notifyItemChanged(i);

                            if (i == (jsonArray.length()-1)){
                                //las
                                loadNext15Data(3);
                            }
                        }
                    }else {
                        loadNext15Data(3);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer_ms", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer_ms", "[" + error.getMessage() + "]");
                mSwipeRefreshLayout.setRefreshing(false);
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

    private void loadNext15Data(int pages){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl+"fixtures?next="+pages, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG+"last resp",response.toString());

                    JSONArray jsonArray = response.getJSONArray("response");

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            JSONObject fixture = jsonObject.getJSONObject("fixture");
                            JSONObject teams = jsonObject.getJSONObject("teams");
                            JSONObject goals = jsonObject.getJSONObject("goals");
                            JSONObject league = jsonObject.getJSONObject("league");
                            //adding to model
                            matchNexts.add(new MatchNext(teams,goals,league,fixture));

                            mRvNext.setAdapter(matchAdapterNext);
                            matchAdapterNext.notifyItemChanged(i);

                            if (i == (jsonArray.length()-1)){
                                //standings
                                mProgressDialog.dismiss();
                              //  loadStandings(league_id,season);
                                toggleView(false);
                            }
                        }
                    }else {
                        toggleView(false);
                        mProgressDialog.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer_ms", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer_ms", "[" + error.getMessage() + "]");
                mSwipeRefreshLayout.setRefreshing(false);
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

    @SuppressLint("UseRequireInsteadOfGet")
    private void setMianLeaguButton(String country, String league, String league_url){
        mCountry.setText(country);
        mLeague.setText(league);
        //league
        Glide.with(Objects.requireNonNull(getContext()).getApplicationContext()).load(league_url)
                .apply(new RequestOptions()
                        .fitCenter())
                .placeholder(R.drawable.pld)
                .error(R.drawable.pld)
                .dontAnimate()
                .placeholder(R.drawable.pld)
                .into(mLeagueLogo);
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



    private void loadSpinner(int cureentSeason){
        seasons.add(String.valueOf(cureentSeason));
        seasons.add(String.valueOf((cureentSeason)-1));
        seasons.add(String.valueOf((cureentSeason)-2));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,seasons);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setSelection(0);
    }

    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                mProgressDialog.show();
                // Your selection handling code here

                //loadStandings("39",seasons.get(pos));
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }



    private void loadBunnerAd() {
        mAdView = view.findViewById(R.id.adview);
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



    private void Native_Ads_loaded() {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getResources().getString(R.string.native_Ad));

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        boolean isDestroyed = false;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                            isDestroyed = getActivity().isDestroyed();
//                        }
                        if (isDestroyed) {
                            nativeAd.destroy();
                            return;
                        }
                        if (native_Ads != null) {
                            native_Ads.destroy();
                        }
                        native_Ads = nativeAd;
                        FrameLayout frameLayout = view.findViewById(R.id.fl_adplaceholder);
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