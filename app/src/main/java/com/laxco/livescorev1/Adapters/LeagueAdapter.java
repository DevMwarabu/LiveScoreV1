package com.laxco.livescorev1.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Activites.LeagueInfo;
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Models.Fixture;
import com.laxco.livescorev1.Models.League;
import com.laxco.livescorev1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.ViewHolder> {
    private Context context;
    private List<League> leagues;
    private String currentSeason;

    public LeagueAdapter(Context context, List<League> leagues) {
        this.context = context;
        this.leagues = leagues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leagues_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        League league = leagues.get(position);
        try {


            for(int i=0; i<league.getSeasons().length(); i++){
                JSONObject jsonObject = league.getSeasons().getJSONObject(i);
                if (jsonObject.getBoolean("current")){
                    currentSeason = jsonObject.getString("year");
                    break;
                }
            }

            holder.setData(league.getCountry().getString("name"),league.getLeague().getString("name"),league.getLeague().getString("logo"));


            holder.mCardViewMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        view.getContext().startActivity(new Intent(context, LeagueInfo.class)
                                .putExtra("country",league.getCountry().getString("name"))
                                .putExtra("name",league.getLeague().getString("name"))
                                .putExtra("logo",league.getLeague().getString("logo"))
                                .putExtra("id",league.getLeague().getString("id"))
                                .putExtra("type",league.getLeague().getString("type"))
                                .putExtra("isPlayerStats",league.getSeasons().getJSONObject(0).getJSONObject("coverage").getJSONObject("fixtures").getBoolean("statistics_players"))
                                .putExtra("isStandings",league.getSeasons().getJSONObject(0).getJSONObject("coverage").getBoolean("standings"))
                                .putExtra("season",currentSeason));
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
        leagues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<League> leagues) {
        leagues.addAll(leagues);
    }

    @Override
    public int getItemCount() {
        return leagues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCountry,mLeague;
        private ImageView mLeagueLogo;
        private CardView mCardViewMain;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCountry = itemView.findViewById(R.id.tv_league_country);
            mLeague = itemView.findViewById(R.id.tv_league_name);
            mLeagueLogo = itemView.findViewById(R.id.image_league_logo);
            mCardViewMain = itemView.findViewById(R.id.card_main);

            mLeague.setSelected(true);
        }

        private void setData(String country,String league,String league_url){
            mCountry.setText(country);
            mLeague.setText(league);
            //league
            Glide.with(context.getApplicationContext()).load(league_url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(mLeagueLogo);
        }
    }
}
