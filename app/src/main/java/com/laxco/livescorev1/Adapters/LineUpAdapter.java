package com.laxco.livescorev1.Adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Constants.Constants;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.Lineupitem;
import com.laxco.livescorev1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class LineUpAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Lineupitem> lineupitems;

    public LineUpAdapter(Context mContext, List<Lineupitem> lineupitems) {
        this.mContext = mContext;
        this.lineupitems = lineupitems;
    }

    @Override
    public int getItemCount() {
        return lineupitems.size();
    }
    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Lineupitem lineupitem = lineupitems.get(position);

        if (lineupitem.isHome()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_item_away, parent, false);
            return new HomeHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_item_home, parent, false);
            return new AwayHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Lineupitem lineupitem = lineupitems.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((HomeHolder) holder).bind(lineupitem);
                break;
            case VIEW_TYPE_MESSAGE_SENT:
                ((AwayHolder) holder).bind(lineupitem);
        }
    }

    private class HomeHolder extends RecyclerView.ViewHolder {
        TextView mPlayerName, mDetails,mElapsed;
        CircleImageView mProfile;
        ImageView mItem;

        HomeHolder(View itemView) {
            super(itemView);

            mPlayerName =itemView.findViewById(R.id.tv_player_name);
            mDetails =  itemView.findViewById(R.id.tv_detail);
            mElapsed =  itemView.findViewById(R.id.tv_elapse);
            mProfile =  itemView.findViewById(R.id.image_profile);
            mItem = itemView.findViewById(R.id.image_event_details);
        }

        void bind(Lineupitem lineupitem) {
            try {
                mPlayerName.setText(lineupitem.getPlayer().getString("name"));
                mDetails.setText(lineupitem.getDetails());
                mElapsed.setText(lineupitem.getTime().getString("elapsed"));
                if (lineupitem.getType().equals("subs")){
                    mItem.setVisibility(View.VISIBLE);
                    mItem.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sub));
                }
                if (lineupitem.getType().equals("Card")){
                    mItem.setVisibility(View.VISIBLE);
                    if (!lineupitem.getDetails().equals("Yellow Card")){
                        mItem.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_square_svgrepo_com));
                    }else {
                        mItem.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_yellow_square_svgrepo_com));
                    }
                }
                //setting image
                gettingPlayerDetails(lineupitem.getPlayer().getString("id"),lineupitem.getSeason(),mProfile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class AwayHolder extends RecyclerView.ViewHolder {
        TextView mPlayerName, mDetails,mElapsed;
        CircleImageView mProfile;
        ImageView mItem;


        AwayHolder(View itemView) {
            super(itemView);

            mPlayerName =itemView.findViewById(R.id.tv_player_name);
            mDetails =  itemView.findViewById(R.id.tv_detail);
            mElapsed =  itemView.findViewById(R.id.tv_elapse);
            mProfile =  itemView.findViewById(R.id.image_profile);
            mItem = itemView.findViewById(R.id.image_event_details);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        void bind(Lineupitem lineupitem) {
            try {
                mPlayerName.setText(lineupitem.getPlayer().getString("name"));
                mDetails.setText(lineupitem.getDetails());
                mElapsed.setText(lineupitem.getTime().getString("elapsed"));
                if (lineupitem.getType().equals("subs")){
                    mItem.setVisibility(View.VISIBLE);
                    mItem.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sub));
                }
                if (lineupitem.getType().equals("Card")){
                    mItem.setVisibility(View.VISIBLE);
                    if (!lineupitem.getDetails().equals("Yellow Card")){
                        mItem.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_square_svgrepo_com));
                    }else {
                        mItem.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_yellow_square_svgrepo_com));
                    }
                }
                //setting image
                gettingPlayerDetails(lineupitem.getPlayer().getString("id"),lineupitem.getSeason(),mProfile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void gettingPlayerDetails(String id,String season,CircleImageView mCircleImageView){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl+"players?id="+id+"&season="+season, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG,response.toString());

                    JSONArray jsonArray = response.getJSONArray("response");
                    String photo = jsonArray.getJSONObject(0).getJSONObject("player").getString("photo");
                    //league
                    Glide.with(mContext.getApplicationContext()).load(photo)
                            .apply(new RequestOptions()
                                    .fitCenter()
                                    .dontTransform()
                                    .format(DecodeFormat.PREFER_ARGB_8888))
                            .placeholder(R.drawable.ic_baseline_terrain_24)
                            .error(R.drawable.ic_baseline_terrain_24)
                            .dontAnimate()
                            .placeholder(R.drawable.ic_baseline_terrain_24)
                            .into(mCircleImageView);

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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        //adding the string request to request queue
        requestQueue.add(jsonObjectRequest);
    }
}
