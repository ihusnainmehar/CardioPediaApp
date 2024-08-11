package com.example.cardiopediaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.squareup.picasso.Picasso;

import com.squareup.picasso.Picasso;

import com.squareup.picasso.Picasso;

public class InvestigationAdapter extends RecyclerView.Adapter<InvestigationAdapter.InvestigationViewHolder> {
    private List<Investigation> investigationList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public InvestigationAdapter(Context context, List<Investigation> investigationList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.investigationList = investigationList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public InvestigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_investigation, parent, false);
        return new InvestigationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestigationViewHolder holder, int position) {
        Investigation investigation = investigationList.get(position);
        holder.tvName.setText(investigation.name);
        Picasso.get()
                .load(investigation.url)
                .placeholder(R.drawable.appicon)
                .into(holder.ivPreview);
        holder.btnDelete.setOnClickListener(v -> onItemClickListener.onDeleteClick(investigation));
        holder.btnDownload.setOnClickListener(v -> onItemClickListener.onDownloadClick(investigation));
    }

    @Override
    public int getItemCount() {
        return investigationList.size();
    }

    public interface OnItemClickListener {
        void onDeleteClick(Investigation investigation);
        void onDownloadClick(Investigation investigation);
    }

    static class InvestigationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivPreview;
        Button btnDelete;
        Button btnDownload;

        InvestigationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivPreview = itemView.findViewById(R.id.ivPreview);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}
