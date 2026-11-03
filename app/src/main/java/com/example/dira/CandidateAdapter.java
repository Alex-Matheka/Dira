package com.example.dira;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    public interface OnCandidateDeleteClickListener {
        void onDeleteClick(int position);
    }

    private List<Candidate> candidateList;
    private final OnCandidateDeleteClickListener onDeleteClickListener;

    public CandidateAdapter(List<Candidate> candidateList, OnCandidateDeleteClickListener onDeleteClickListener) {
        this.candidateList = candidateList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);

        holder.tvName.setText(candidate.getName());
        holder.tvPosition.setText("Position: " + candidate.getPosition());
        holder.tvId.setText("ID: " + candidate.getId());

        if (candidate.getParty() != null && !candidate.getParty().isEmpty()) {
            holder.tvParty.setText("Party/Tag: " + candidate.getParty());
            holder.tvParty.setVisibility(View.VISIBLE);
        } else {
            holder.tvParty.setVisibility(View.GONE);
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
        return candidateList != null ? candidateList.size() : 0;
    }

    public void updateList(List<Candidate> newList) {
        this.candidateList = newList;
        notifyDataSetChanged();
    }

    static class CandidateViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition, tvId, tvParty;
        ImageButton btnDelete;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCandidateName);
            tvPosition = itemView.findViewById(R.id.tvCandidatePosition);
            tvId = itemView.findViewById(R.id.tvCandidateId);
            tvParty = itemView.findViewById(R.id.tvCandidateParty);
            btnDelete = itemView.findViewById(R.id.btnDeleteCandidate);
        }
    }
}
