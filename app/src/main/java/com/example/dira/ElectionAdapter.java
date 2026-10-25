package com.example.dira;

import android.graphics.Color;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private List<Election> electionList;
    private final OnDeleteClickListener onDeleteClickListener;
    private final Set<String> expandedElectionIds = new HashSet<>();

    public ElectionAdapter(List<Election> electionList, OnDeleteClickListener onDeleteClickListener) {
        this.electionList = electionList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ElectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_election, parent, false);
        return new ElectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElectionViewHolder holder, int position) {
        Election election = electionList.get(position);

        holder.tvTitle.setText(election.getTitle());
        holder.tvId.setText("ID: " + election.getId());
        holder.tvDateDuration.setText("Date: " + election.getDate() + " · Duration: " + election.getDuration());

        if (election.getSections() != null && !election.getSections().isEmpty()) {
            StringBuilder sectionsText = new StringBuilder("Sections: ");
            for (int i = 0; i < election.getSections().size(); i++) {
                sectionsText.append(election.getSections().get(i));
                if (i < election.getSections().size() - 1) {
                    sectionsText.append(", ");
                }
            }
            holder.tvSections.setText(sectionsText.toString());
            holder.tvSections.setVisibility(View.VISIBLE);
        } else {
            holder.tvSections.setVisibility(View.GONE);
        }

        if (election.getInstructions() != null && !election.getInstructions().isEmpty()) {
            holder.tvInstructions.setText("Instructions: " + election.getInstructions());
            holder.tvInstructions.setVisibility(View.VISIBLE);
        } else {
            holder.tvInstructions.setVisibility(View.GONE);
        }

        String status = election.getStatus();

        if ("UPCOMING".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("Upcoming");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#EF6C00"));
            holder.tvStatusBadge.getBackground().setTint(Color.parseColor("#FFF3E0"));

        } else if ("OPEN".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("Open");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvStatusBadge.getBackground().setTint(Color.parseColor("#E8F5E9"));

        } else if ("CLOSED".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("Closed");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#C62828"));
            holder.tvStatusBadge.getBackground().setTint(Color.parseColor("#FFEBEE"));

        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("Completed");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#1565C0"));
            holder.tvStatusBadge.getBackground().setTint(Color.parseColor("#E3F2FD"));

        } else {
            holder.tvStatusBadge.setText("Unknown");
            holder.tvStatusBadge.setTextColor(Color.DKGRAY);
            holder.tvStatusBadge.getBackground().setTint(Color.LTGRAY);
        }

        boolean isExpanded = expandedElectionIds.contains(election.getId());
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            boolean currentExpanded = expandedElectionIds.contains(election.getId());
            if (currentExpanded) {
                expandedElectionIds.remove(election.getId());
            } else {
                expandedElectionIds.add(election.getId());
            }
            if (holder.itemView.getParent() instanceof ViewGroup) {
                TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView.getParent());
            }
            notifyItemChanged(holder.getBindingAdapterPosition());
        });

        holder.optionCandidates.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Candidates clicked for " + election.getTitle(), Toast.LENGTH_SHORT).show());

        holder.optionVoters.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Voters clicked for " + election.getTitle(), Toast.LENGTH_SHORT).show());

        holder.optionCloseVoting.setOnClickListener(v -> {
            String currentStatus = election.getStatus();
            String newStatus;

            if ("UPCOMING".equalsIgnoreCase(currentStatus)
                    || "CLOSED".equalsIgnoreCase(currentStatus)) {

                newStatus = "OPEN";

            } else if ("OPEN".equalsIgnoreCase(currentStatus)) {

                newStatus = "CLOSED";

            } else {
                Toast.makeText(
                        v.getContext(),
                        "Voting status cannot be changed for this election.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String previousStatus = currentStatus;
            election.setStatus(newStatus);

            ElectionStorage.saveElection(election, new ElectionStorage.OnElectionSavedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(
                            v.getContext(),
                            "Voting status changed to " + newStatus,
                            Toast.LENGTH_SHORT
                    ).show();

                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        notifyItemChanged(pos);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    election.setStatus(previousStatus);

                    Toast.makeText(
                            v.getContext(),
                            "Failed to update voting status: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        notifyItemChanged(pos);
                    }
                }
            });
        });

        holder.optionElectionDetails.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Election Details clicked for " + election.getTitle(), Toast.LENGTH_SHORT).show());

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
        return electionList != null ? electionList.size() : 0;
    }

    public void updateList(List<Election> newList) {
        this.electionList = newList;
        notifyDataSetChanged();
    }

    static class ElectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvId, tvDateDuration, tvSections, tvInstructions, tvStatusBadge;
        LinearLayout expandableLayout, optionCandidates, optionVoters, optionCloseVoting, optionElectionDetails;
        ImageButton btnDelete;

        public ElectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvId = itemView.findViewById(R.id.tvItemId);
            tvDateDuration = itemView.findViewById(R.id.tvItemDateDuration);
            tvSections = itemView.findViewById(R.id.tvItemSections);
            tvInstructions = itemView.findViewById(R.id.tvItemInstructions);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            optionCandidates = itemView.findViewById(R.id.optionCandidates);
            optionVoters = itemView.findViewById(R.id.optionVoters);
            optionCloseVoting = itemView.findViewById(R.id.optionCloseVoting);
            optionElectionDetails = itemView.findViewById(R.id.optionElectionDetails);

            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
