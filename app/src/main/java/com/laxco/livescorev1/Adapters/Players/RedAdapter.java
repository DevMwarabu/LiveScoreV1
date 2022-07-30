package com.laxco.livescorev1.Adapters.Players;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.laxco.livescorev1.Models.Country;
import com.laxco.livescorev1.Models.PlayeState;
import com.laxco.livescorev1.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RedAdapter extends RecyclerView.Adapter<RedAdapter.ViewHolder> {
    private Context context;
    private List<PlayeState> playeStates;

    public RedAdapter(Context context, List<PlayeState> playeStates) {
        this.context = context;
        this.playeStates = playeStates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_dash = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_stats_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view_dash);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayeState playeState = playeStates.get(position);
        try{
            holder.setData(
                    playeState.getPlayer().getString("name"),
                    playeState.getStatistics().getJSONObject(0).getJSONObject("team").getString("name"),
                    playeState.getStatistics().getJSONObject(0).getJSONObject("cards").getString("red"),
                    position,
                    playeState.getPlayer().getString("photo"),
                    playeState.getStatistics().getJSONObject(0).getJSONObject("team").getString("logo")
            );
        }catch (Exception e){

        }
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
        playeStates.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Country> countries) {
        countries.addAll(countries);
    }

    @Override
    public int getItemCount() {
        return playeStates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName,mTeamName,mPoints,mPosition;
        private CircleImageView mProfile;
        private ImageView mTeamLogo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tv_player_name);
            mTeamLogo = itemView.findViewById(R.id.image_team_logo);
            mTeamName = itemView.findViewById(R.id.tv_team_name);
            mPoints = itemView.findViewById(R.id.tv_points);
            mPosition = itemView.findViewById(R.id.tv_position);
            mProfile = itemView.findViewById(R.id.image_profile);
        }

        private void setData(String name,String team_name,String points,int position,String photo,String logo){

            mPosition.setText(String.valueOf((position+1)));
            mName.setText(name);
            mTeamName.setText(team_name);
            if (points.equals("null")){
                mPoints.setText("o");

            }else {
                mPoints.setText(points);
            }

            //imagesloaing
            Glide.with(context.getApplicationContext()).load(photo)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(mProfile);

            Glide.with(context.getApplicationContext()).load(logo)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(mTeamLogo);
        }

    }
}
