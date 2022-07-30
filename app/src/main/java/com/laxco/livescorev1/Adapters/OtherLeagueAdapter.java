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

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.laxco.livescorev1.Activites.LeagueInfo;
import com.laxco.livescorev1.Models.Country;
import com.laxco.livescorev1.Models.League;
import com.laxco.livescorev1.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class OtherLeagueAdapter extends RecyclerView.Adapter<OtherLeagueAdapter.ViewHolder>  implements Filterable {
    private Context context;
    private List<League> leagues;
    private List<League> leaguesFiltered;

    public OtherLeagueAdapter(Context context, List<League> leagues) {
        this.context = context;
        this.leagues = leagues;
        this.leaguesFiltered = leagues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_dash = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view_dash);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        League league = leaguesFiltered.get(position);
        try {
            holder.setData(league.getLeague().getString("name"),league.getLeague().getString("logo"));
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
                                .putExtra("season",league.getSeasons().getJSONObject(0).getString("year")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
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
        leaguesFiltered.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<League> leagues) {
        leagues.addAll(leagues);
    }

    @Override
    public int getItemCount() {
        return leaguesFiltered.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    leaguesFiltered = leagues;
                } else {
                    List<League> filteredList = new ArrayList<>();
                    for (League row :leagues) {

                        // name match condition. this might differ depending on your requirement
                        try {
                            if (row.getLeague().getString("name").toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    leaguesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = leaguesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                leaguesFiltered = (ArrayList<League>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private CardView mCardViewMain;
        private ImageView mFlag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tv_name);
            mFlag = itemView.findViewById(R.id.image_flag);
            mCardViewMain = itemView.findViewById(R.id.card_main);
        }

        private void setData(String name,String flag){
            mName.setText(name);

            Glide.with(context.getApplicationContext()).load(flag)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontTransform()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .placeholder(R.drawable.pld)
                    .error(R.drawable.pld)
                    .dontAnimate()
                    .placeholder(R.drawable.pld)
                    .into(mFlag);
        }

    }
}
