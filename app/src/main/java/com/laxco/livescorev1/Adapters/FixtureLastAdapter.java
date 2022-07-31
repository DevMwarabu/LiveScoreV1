package com.laxco.livescorev1.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.R;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FixtureLastAdapter extends RecyclerView.Adapter<FixtureLastAdapter.ViewHolder> {
    private Context context;
    private List<Fixture> fixtures;

    public FixtureLastAdapter(Context context, List<Fixture> fixtures) {
        this.context = context;
        this.fixtures = fixtures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_dash = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_game_item, parent, false);
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
        Fixture fixture = fixtures.get(position);
        try {
            holder.setData(
                    fixture.getGoals().getString("home"),
                    fixture.getGoals().getString("away"),
                    fixture.getTeams().getJSONObject("home").getString("logo"),
                    fixture.getTeams().getJSONObject("away").getString("logo")

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
        fixtures.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Fixture> fixtures) {
        fixtures.addAll(fixtures);
    }

    @Override
    public int getItemCount() {
        return fixtures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mScoreHome,mScoreAway;
        private ImageView mLogoHome,mLogoAway;
        private CardView mCardViewMain;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mScoreHome = itemView.findViewById(R.id.tv_home_score);
            mScoreAway = itemView.findViewById(R.id.tv_away_score);
            mLogoHome = itemView.findViewById(R.id.image_home_logo);
            mLogoAway = itemView.findViewById(R.id.image_away_logo);
            mCardViewMain = itemView.findViewById(R.id.card_main);
            //mCardViewMain.setEnabled(false);


        }

        @SuppressLint("NewApi")
        private void setData(String h_score, String a_score, String home_url, String away_url){
            mScoreHome.setText(h_score.equals("null")?"0":h_score);
            mScoreAway.setText(a_score.equals("null")?"0":a_score);

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
    }
}
