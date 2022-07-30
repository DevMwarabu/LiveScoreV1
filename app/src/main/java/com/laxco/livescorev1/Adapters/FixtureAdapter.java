package com.laxco.livescorev1.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Models.Country;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Fixture> fixtures;
    private List<Fixture> fixturesFilered;

    public FixtureAdapter(Context context, List<Fixture> fixtures) {
        this.context = context;
        this.fixtures = fixtures;
        this.fixturesFilered = fixtures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_dash = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixture_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view_dash);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fixture fixture = fixturesFilered.get(position);
        try {
            holder.setData(
                    fixture.getTeams().getJSONObject("home").getString("name"),
                    fixture.getTeams().getJSONObject("away").getString("name"),
                    fixture.getGoals().getString("home"),
                    fixture.getGoals().getString("away"),
                    fixture.getTeams().getJSONObject("home").getString("logo"),
                    fixture.getTeams().getJSONObject("away").getString("logo"),
                    fixture.getFixture().getJSONObject("status").getString("short"),
                    fixture.getFixture().getInt("timestamp"),
                    fixture.getFixture().getJSONObject("status").getString("elapsed")

            );

            //opening fixture
            holder.mCardViewMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        view.getContext().startActivity(new Intent(context, FixtureInfo.class).putExtra("id",fixture.getFixture().getInt("id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void clear() {
        fixturesFilered.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Fixture> fixtures) {
        fixtures.addAll(fixtures);
    }

    @Override
    public int getItemCount() {
        return fixturesFilered.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    fixturesFilered = fixtures;
                } else {
                    List<Fixture> filteredList = new ArrayList<>();
                    for (Fixture row : filteredList) {

                        // name match condition. this might differ depending on your requirement
                        try {
                            if (row.getTeams().getJSONObject("home").getString("name").toLowerCase().contains(charString.toLowerCase())
                            || row.getTeams().getJSONObject("away").getString("name").toLowerCase().contains(charString.toLowerCase())
                            || row.getFixture().getJSONObject("status").getString("elapsed").toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    fixturesFilered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = fixturesFilered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                fixturesFilered = (ArrayList<Fixture>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mHome,mAway,mScoreHome,mScoreAway,mGameStatus,mGameTime;
        private ImageView mLogoHome,mLogoAway;
        private CardView mCardViewMain;
        private View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mHome = itemView.findViewById(R.id.tv_home_team);
            mAway = itemView.findViewById(R.id.tv_away_team);
            mScoreHome = itemView.findViewById(R.id.tv_home_score);
            mScoreAway = itemView.findViewById(R.id.tv_away_score);
            mLogoHome = itemView.findViewById(R.id.image_home_logo);
            mLogoAway = itemView.findViewById(R.id.image_away_logo);
            mCardViewMain = itemView.findViewById(R.id.card_main);
            mGameStatus = itemView.findViewById(R.id.tv_game_status);
            mGameTime = itemView.findViewById(R.id.tv_time_kick_off);
            mView = itemView.findViewById(R.id.view_status);
            //mCardViewMain.setEnabled(false);


        }

        @SuppressLint("NewApi")
        private void setData(String home, String away, String h_score, String a_score, String home_url, String away_url,String status,int timestamp,String elapsed){
            mHome.setText(home);
            mAway.setText(away);
            mScoreHome.setText(h_score);
            mScoreAway.setText(a_score);
            mGameStatus.setText(status);


            if (!status.equals("NS")) {
                mScoreAway.setVisibility(View.VISIBLE);
                mScoreHome.setVisibility(View.VISIBLE);
                mView.setVisibility(View.VISIBLE);
                mCardViewMain.setEnabled(true);
                mGameStatus.setVisibility(View.VISIBLE);
            }else {
               mGameTime.setVisibility(View.VISIBLE);
            }

            if(status.equals("1H")|| status.equals("2H") || status.equals("LIVE")){
                mGameStatus.setText(elapsed+"'");
            }


            settingDate(mGameTime,timestamp);

            //setting images logos
            Glide.with(context.getApplicationContext()).load(home_url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(mLogoHome);
            //away
            Glide.with(context.getApplicationContext()).load(away_url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(mLogoAway);
        }

        private void settingDate(TextView textView,long seconds){
            long timeStamp = seconds*1000;
            //convert to format date
            @SuppressLint("SimpleDateFormat") DateFormat sdf1 = new SimpleDateFormat("hh:mm", Locale.US);
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC +4"));
            Date netDate1 = (new Date(timeStamp));
            sdf1.setTimeZone(TimeZone.getDefault());
            textView.setText(sdf1.format(netDate1));
        }
    }
}
