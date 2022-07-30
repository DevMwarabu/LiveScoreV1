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
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Models.Match;
import com.laxco.livescorev1.Models.MatchNext;
import com.laxco.livescorev1.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MatchAdapterNext extends RecyclerView.Adapter<MatchAdapterNext.ViewHolder> {
    private Context context;
    private List<MatchNext> matches;

    public MatchAdapterNext(Context context, List<MatchNext> matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchNext match = matches.get(position);
        try {
            holder.setData(
                    match.getTeams().getJSONObject("home").getString("name"),
                    match.getTeams().getJSONObject("away").getString("name"),
                    match.getTeams().getJSONObject("home").getString("logo"),
                    match.getTeams().getJSONObject("away").getString("logo"),
                    match.getFixture().getInt("timestamp"),
                    match.getFixture().getJSONObject("status").getString("short"),
                    match.getFixture().getString("date")

            );
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        //opening fixture
        holder.mCardViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    view.getContext().startActivity(new Intent(context, FixtureInfo.class).putExtra("id",match.getFixture().getInt("id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void clear() {
        matches.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<MatchNext> matches) {
        matches.addAll(matches);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private boolean isEnabled = false;
        private TextView mHome, mAway, mElapse, mKickOffTime, mKickOffDate;
        private ImageView mLogoHome, mLogoAway;
        private CardView mCardViewMain, mCardViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mHome = itemView.findViewById(R.id.tv_home_team);
            mAway = itemView.findViewById(R.id.tv_away_team);
            mElapse = itemView.findViewById(R.id.tv_elapse);
            mLogoHome = itemView.findViewById(R.id.image_home_logo);
            mLogoAway = itemView.findViewById(R.id.image_away_logo);
            mCardViewStatus = itemView.findViewById(R.id.card_status);
            mCardViewMain = itemView.findViewById(R.id.card_main);
            mKickOffDate = itemView.findViewById(R.id.tv_date_kick_off);
            mKickOffTime = itemView.findViewById(R.id.tv_time_kick_off);
            //mCardViewMain.setEnabled(isEnabled);

            mHome.setSelected(true);
            mAway.setSelected(true);
        }

        private void setData(String home, String away, String home_url, String away_url, int timestamp, String status, String date_kick_off) throws ParseException {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
            Date result;
            try {
                result = df.parse(date_kick_off);
                System.out.println("date:" + result); //prints date in current locale
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC +3"));
                mKickOffDate.setText(sdf.format(result));
            } catch (Exception e) {

            }

            mHome.setText(home);
            mAway.setText(away);
            if (status.equals("ET") || status.equals("1H") || status.equals("HT") || status.equals("2H") || status.equals("P") || status.equals("LIVE")) {
                mCardViewStatus.setVisibility(View.VISIBLE);
                mCardViewMain.setEnabled(true);
            } else {
                mCardViewStatus.setVisibility(View.GONE);
            }

            settingDate(mKickOffTime, timestamp);

            //setting images logos
            Glide.with(context.getApplicationContext()).load(home_url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.ic_baseline_terrain_24)
                    .error(R.drawable.ic_baseline_terrain_24)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_baseline_terrain_24)
                    .into(mLogoHome);
            //away
            Glide.with(context.getApplicationContext()).load(away_url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.ic_baseline_terrain_24)
                    .error(R.drawable.ic_baseline_terrain_24)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_baseline_terrain_24)
                    .into(mLogoAway);
        }

        private void settingDate(TextView textView, long seconds) {
            long timeStamp = seconds * 1000;
            //convert to format date
            @SuppressLint("SimpleDateFormat") DateFormat sdf1 = new SimpleDateFormat("hh:mm", Locale.US);
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC +4"));
            Date netDate1 = (new Date(timeStamp));
            sdf1.setTimeZone(TimeZone.getDefault());
            textView.setText(sdf1.format(netDate1));
            mKickOffDate.setVisibility(View.VISIBLE);
        }
    }
}
