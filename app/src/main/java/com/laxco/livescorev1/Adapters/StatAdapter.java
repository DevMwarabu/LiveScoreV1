package com.laxco.livescorev1.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.laxco.livescorev1.Activites.MoreLives;
import com.laxco.livescorev1.Models.League;
import com.laxco.livescorev1.Models.Stat;
import com.laxco.livescorev1.R;

import org.json.JSONException;

import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {
    private Context context;
    private List<Stat> stats;

    public StatAdapter(Context context, List<Stat> stats) {
        this.context = context;
        this.stats = stats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.states_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stat stat = stats.get(position);
        holder.setData(stat.getType(), stat.getHome_val(),stat.getAway_val(),stat.isToCalculate());
    }


    public void clear() {
        stats.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Stat> stats) {
        stats.addAll(stats);
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle,mHome,mAway;
        private LinearProgressIndicator mProgressBarHome,mProgressBarAway;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_title);
            mHome = itemView.findViewById(R.id.tv_home_team_val);
            mAway = itemView.findViewById(R.id.tv_away_team_val);
            mProgressBarAway = itemView.findViewById(R.id.progressBar_away);
            mProgressBarHome = itemView.findViewById(R.id.progressBar_home);

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("ResourceAsColor")
        private void setData(String type, String home_val, String away_val, boolean isToCalculate){
            mTitle.setText(type);
            //doing calculations

            if (isToCalculate){

                float total = (Integer.parseInt(home_val)+Integer.parseInt(away_val));
                int progressHome = Math.round((Integer.parseInt(home_val)/total)*100);
                int progressAway =  Math.round((Integer.parseInt(away_val)/total)*100);

                mAway.setText(away_val);
                if (progressAway>50){
                    mProgressBarAway.setIndicatorColor(itemView.getContext().getColor(R.color.primaryColor));
                }else {
                    mProgressBarAway.setIndicatorColor(itemView.getContext().getColor(io.supercharge.shimmerlayout.R.color.shimmer_color));
                }
                if (progressHome>50){
                    mProgressBarHome.setIndicatorColor(itemView.getContext().getColor(R.color.primaryColor));
                }else {
                    mProgressBarHome.setIndicatorColor(itemView.getContext().getColor(io.supercharge.shimmerlayout.R.color.shimmer_color));
                }
                mProgressBarAway.setProgress(progressAway);
                mHome.setText(home_val);
                mProgressBarHome.setProgress(progressHome);

            }else {
                mAway.setText(away_val);
                mProgressBarAway.setProgress(Integer.parseInt(away_val));

                mHome.setText(home_val);
                mProgressBarHome.setProgress(Integer.parseInt(home_val));

                if (Integer.parseInt(away_val)>50){
                    mProgressBarAway.setIndicatorColor(itemView.getContext().getColor(R.color.primaryColor));
                }else {
                    mProgressBarAway.setIndicatorColor(itemView.getContext().getColor(io.supercharge.shimmerlayout.R.color.shimmer_color));
                }
                if (Integer.parseInt(home_val)>50){
                    mProgressBarHome.setIndicatorColor(itemView.getContext().getColor(R.color.primaryColor));
                }else {
                    mProgressBarHome.setIndicatorColor(itemView.getContext().getColor(io.supercharge.shimmerlayout.R.color.shimmer_color));
                }

            }
        }
    }
}
