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
import com.laxco.livescorev1.Activites.AllLeagues;
import com.laxco.livescorev1.Activites.FixtureInfo;
import com.laxco.livescorev1.Models.Country;
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

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder>  implements Filterable {
    private Context context;
    private List<Country> countries;
    private List<Country> countriesFiltered;

    public CountryAdapter(Context context, List<Country> countries) {
        this.context = context;
        this.countries = countries;
        this.countriesFiltered = countries;
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
        Country country = countriesFiltered.get(position);
        holder.setData(country.getName(),country.getFlag());
        holder.mCardViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(context, AllLeagues.class)
                        .putExtra("code",country.getCode()));
            }
        });
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
        countriesFiltered.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Country> countries) {
        countries.addAll(countries);
    }

    @Override
    public int getItemCount() {
        return countriesFiltered.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    countriesFiltered = countries;
                } else {
                    List<Country> filteredList = new ArrayList<>();
                    for (Country row : countries) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    countriesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = countriesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                countriesFiltered = (ArrayList<Country>) filterResults.values;
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
