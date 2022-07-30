package com.laxco.livescorev1.Adapters;

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
import com.laxco.livescorev1.Activites.AllLeagues;
import com.laxco.livescorev1.Models.Country;
import com.laxco.livescorev1.Models.Lineup;
import com.laxco.livescorev1.R;

import java.util.ArrayList;
import java.util.List;

public class LineupAdapter extends RecyclerView.Adapter<LineupAdapter.ViewHolder>  {
    private Context context;
    private List<Lineup> lineups;

    public LineupAdapter(Context context, List<Lineup> lineups) {
        this.context = context;
        this.lineups = lineups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_dash = LayoutInflater.from(parent.getContext()).inflate(R.layout.lineupitem, parent, false);
        context = parent.getContext();
        return new ViewHolder(view_dash);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lineup lineup = lineups.get(position);
        holder.setData(lineup.getFormation(),lineup.getPlayed());
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void clear() {
        lineups.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Lineup> lineups) {
        lineups.addAll(lineups);
    }

    @Override
    public int getItemCount() {
        return lineups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFormation,mPlayed;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mFormation = itemView.findViewById(R.id.tv_formation);
            mPlayed = itemView.findViewById(R.id.tv_played);
        }

        private void setData(String formation,String played){
            mFormation.setText(formation);
            mPlayed.setText(played);
        }

    }
}
