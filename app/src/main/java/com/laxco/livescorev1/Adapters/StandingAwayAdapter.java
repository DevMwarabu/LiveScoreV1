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
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Activites.TeamInfo;
import com.laxco.livescorev1.Models.Standing;
import com.laxco.livescorev1.R;

import org.json.JSONException;

import java.util.List;

public class StandingAwayAdapter extends RecyclerView.Adapter<StandingAwayAdapter.ViewHolder> {
    private Context context;
    private String season,leagueId;
    private List<Standing> standings;

    public StandingAwayAdapter(Context context, List<Standing> standings, String season,String leagueId) {
        this.context = context;
        this.standings = standings;
        this.season = season;
        this.leagueId = leagueId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.standings_items, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Standing standing = standings.get(position);
        try {
            holder.setData(
                    standing.getRank(),
                    standing.getTeam().getString("name"),
                    standing.getAway().getString("played"),
                    standing.getAway().getString("win"),
                    standing.getAway().getString("draw"),
                    standing.getAway().getJSONObject("goals").getString("for"),
                    standing.getAway().getJSONObject("goals").getString("against"),
                    standing.getTeam().getString("logo")

            );

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        view.getContext().startActivity(new Intent(context, TeamInfo.class)
                                .putExtra("id",standing.getTeam().getString("id"))
                                .putExtra("season",season)
                                .putExtra("leagueId",leagueId));
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
        standings.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Standing> standings) {
        standings.addAll(standings);
    }

    @Override
    public int getItemCount() {
        return standings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rank,team,mp,w,d,l,gf,ga,gd,pts;
        private ImageView logo;
        private CardView mCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mp = itemView.findViewById(R.id.tv_plays);
            gd = itemView.findViewById(R.id.tv_gd);
            pts = itemView.findViewById(R.id.tv_points);
            rank = itemView.findViewById(R.id.tv_rank);
            team = itemView.findViewById(R.id.tv_team_name);
            logo = itemView.findViewById(R.id.image_team_logo);
            mCardView = itemView.findViewById(R.id.card_main);
        }

        private void setData(String _rank,String _team,String _mp,String win,String draw,String _fg,String _ga,String url){

            int points = (Integer.parseInt(win) * 3)+Integer.parseInt(draw);
            int gaol_diff = Integer.parseInt(_fg)-Integer.parseInt(_ga);

            rank.setText(_rank);
            team.setText(_team);
            mp.setText(_mp);
            gd.setText(String.valueOf(gaol_diff));
            pts.setText(String.valueOf(points));

            //setting images logos
            Glide.with(context.getApplicationContext()).load(url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(logo);
        }

    }
}
