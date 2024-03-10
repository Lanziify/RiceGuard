package com.pyu.riceleafdiseasedetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private List<Recommendations> recommendations;

    public RecommendationAdapter(List<Recommendations> recommendations) {
        this.recommendations = recommendations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendations_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recommendations recommendation = recommendations.get(position);
        holder.method.setText(recommendation.getMethod());
        holder.details.setText(recommendation.getDetails());
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView method, details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            method = itemView.findViewById(R.id.recommendationMethod);
            details = itemView.findViewById(R.id.recommendationDetails);
        }
    }
}
