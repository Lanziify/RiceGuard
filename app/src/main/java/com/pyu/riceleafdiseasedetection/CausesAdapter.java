package com.pyu.riceleafdiseasedetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CausesAdapter extends RecyclerView.Adapter<CausesAdapter.ViewHolder> {
    private List<Causes> causes;

    public CausesAdapter(List<Causes> causes) {
        this.causes = causes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.causes_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Causes cause = causes.get(position);
        holder.category.setText(cause.getCategory());
        holder.details.setText(cause.getDetails());

    }

    @Override
    public int getItemCount() {
        return causes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView category, details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.causesCategory);
            details = itemView.findViewById(R.id.causesDetails);
        }
    }
}
