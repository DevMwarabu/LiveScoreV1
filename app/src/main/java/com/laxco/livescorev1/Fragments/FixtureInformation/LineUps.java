package com.laxco.livescorev1.Fragments.FixtureInformation;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Stat;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class LineUps extends Fragment {
    private View view;
    private String TAG = "response_summarry";
    private TextView mRef,mDate,mVenue,mCity,mCurrent,mElapsed;
    private ImageView mLeagueLogo;
    private TextView mCountry,mLeague;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_squard, container, false);

        mRef = view.findViewById(R.id.tv_ref);
        mDate = view.findViewById(R.id.tv_date_kick_off);
        mVenue = view.findViewById(R.id.tv_venue);
        mCity = view.findViewById(R.id.tv_city);
        mCurrent = view.findViewById(R.id.tv_current);
        mElapsed = view.findViewById(R.id.tv_time_elapsed);
        mCountry = view.findViewById(R.id.tv_league_country);
        mLeague = view.findViewById(R.id.tv_league_name);
        mLeagueLogo = view.findViewById(R.id.image_league_logo);



        settingData(FixtureInfo.fixture_id);




        return view;
    }

    private void settingData(String fixture_id){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl + "fixtures?id=" + fixture_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG, response.toString());

                    JSONArray jsonArray = response.getJSONArray("response");
                    mRef.setText(jsonArray.getJSONObject(0).getJSONObject("fixture").getString("referee"));
                    mVenue.setText(jsonArray.getJSONObject(0).getJSONObject("fixture").getJSONObject("venue").getString("name"));
                    mCity.setText(jsonArray.getJSONObject(0).getJSONObject("fixture").getJSONObject("venue").getString("city"));
                    mCurrent.setText(jsonArray.getJSONObject(0).getJSONObject("fixture").getJSONObject("status").getString("short"));
                    mElapsed.setText(jsonArray.getJSONObject(0).getJSONObject("fixture").getJSONObject("status").getString("elapsed"));


                    mCountry.setText(jsonArray.getJSONObject(0).getJSONObject("league").getString("country"));
                    mLeague.setText(jsonArray.getJSONObject(0).getJSONObject("league").getString("name"));
                    //league
                    Glide.with(getContext().getApplicationContext()).load(jsonArray.getJSONObject(0).getJSONObject("league").getString("logo"))
                            .apply(new RequestOptions()
                                    .fitCenter()
                                    .dontTransform()
                                    .format(DecodeFormat.PREFER_ARGB_8888))
                            .placeholder(R.drawable.ic_baseline_terrain_24)
                            .error(R.drawable.ic_baseline_terrain_24)
                            .dontAnimate()
                            .placeholder(R.drawable.ic_baseline_terrain_24)
                            .into(mLeagueLogo);


                    String date_kick_off = jsonArray.getJSONObject(0).getJSONObject("fixture").getString("date");

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
                    Date result;

                    result = df.parse(date_kick_off);
                    System.out.println("date:"+result); //prints date in current locale
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,  yyyy");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC +3"));
                    mDate.setText(sdf.format(result));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "[" + e.getMessage() + "]");
//                    mLinearLayoutMain.setVisibility(View.GONE);
//                    mLinearLayoutSecond.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "[" + error.getMessage() + "]");
//                mLinearLayoutMain.setVisibility(View.GONE);
//                mLinearLayoutSecond.setVisibility(View.VISIBLE);
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