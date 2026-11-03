package com.example.dira;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoterAdapter extends RecyclerView.Adapter<VoterAdapter.VoterViewHolder> {

    public interface OnVoterDeleteClickListener {
        void onDeleteClick(int position);
    }

    private List<Voter> voterList;
    private final OnVoterDeleteClickListener onDeleteClickListener;

    public VoterAdapter(List<Voter> voterList, OnVoterDeleteClickListener onDeleteClickListener) {
        this.voterList = voterList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public VoterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voter, parent, false);
        return new VoterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoterViewHolder holder, int position) {
        Voter voter = voterList.get(position);

        holder.tvName.setText(voter.getName());
        holder.tvPhone.setText("Phone: " + voter.getPhone());
        holder.tvId.setText("ID: " + voter.getId());

        String status = voter.getStatus();
        if ("Voted".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("Voted");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#1565C0"));
            holder.tvStatusBadge.getBackground().setTint(Color.parseColor("#E3F2FD"));
        } else {
            holder.tvStatusBadge.setText("Registered");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvStatusBadge.getBackground().setTint(Color.parseColor("#E8F5E9"));
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return voterList != null ? voterList.size() : 0;
    }

    public void updateList(List<Voter> newList) {
        this.voterList = newList;
        notifyDataSetChanged();
    }

    static class VoterViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvId, tvStatusBadge;
        ImageButton btnDelete;

        public VoterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVoterName);
            tvPhone = itemView.findViewById(R.id.tvVoterPhone);
            tvId = itemView.findViewById(R.id.tvVoterId);
            tvStatusBadge = itemView.findViewById(R.id.tvVoterStatusBadge);
            btnDelete = itemView.findViewById(R.id.btnDeleteVoter);
        }
    }
}
