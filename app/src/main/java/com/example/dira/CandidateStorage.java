package com.example.dira;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CandidateStorage {

    public interface OnCandidateSavedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnCandidatesLoadedListener {
        void onSuccess(List<Candidate> candidates);
        void onFailure(Exception e);
    }

    public interface OnCandidateDeletedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    private static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    public static void saveCandidate(String electionId, Candidate candidate, OnCandidateSavedListener listener) {
        if (electionId == null || electionId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Election ID"));
            return;
        }

        String docId = (candidate.getId() != null && !candidate.getId().trim().isEmpty())
                ? candidate.getId().trim()
                : getDb().collection("Elections").document(electionId).collection("candidates").document().getId();

        candidate.setId(docId);
        candidate.setElectionId(electionId);

        getDb().collection("Elections")
                .document(electionId)
                .collection("candidates")
                .document(docId)
                .set(candidate)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    public static void getCandidates(String electionId, OnCandidatesLoadedListener listener) {
        if (electionId == null || electionId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Election ID"));
            return;
        }

        getDb().collection("Elections")
                .document(electionId)
                .collection("candidates")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Candidate> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Candidate c = doc.toObject(Candidate.class);
                        if (c.getId() == null || c.getId().isEmpty()) {
                            c.setId(doc.getId());
                        }
                        list.add(c);
                    }
                    if (listener != null) listener.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    public static void deleteCandidate(String electionId, String candidateId, OnCandidateDeletedListener listener) {
        if (electionId == null || candidateId == null || candidateId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Parameters"));
            return;
        }

        getDb().collection("Elections")
                .document(electionId)
                .collection("candidates")
                .document(candidateId.trim())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }
}
