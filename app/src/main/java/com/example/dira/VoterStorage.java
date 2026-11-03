package com.example.dira;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VoterStorage {

    public interface OnVoterSavedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnVotersLoadedListener {
        void onSuccess(List<Voter> voters);
        void onFailure(Exception e);
    }

    public interface OnVoterDeletedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    private static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    public static void saveVoter(String electionId, Voter voter, OnVoterSavedListener listener) {
        if (electionId == null || electionId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Election ID"));
            return;
        }

        String docId = (voter.getId() != null && !voter.getId().trim().isEmpty())
                ? voter.getId().trim()
                : getDb().collection("Elections").document(electionId).collection("voters").document().getId();

        voter.setId(docId);
        voter.setElectionId(electionId);

        getDb().collection("Elections")
                .document(electionId)
                .collection("voters")
                .document(docId)
                .set(voter)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    public static void getVoters(String electionId, OnVotersLoadedListener listener) {
        if (electionId == null || electionId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Election ID"));
            return;
        }

        getDb().collection("Elections")
                .document(electionId)
                .collection("voters")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Voter> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Voter v = doc.toObject(Voter.class);
                        if (v.getId() == null || v.getId().isEmpty()) {
                            v.setId(doc.getId());
                        }
                        list.add(v);
                    }
                    if (listener != null) listener.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    public static void deleteVoter(String electionId, String voterId, OnVoterDeletedListener listener) {
        if (electionId == null || voterId == null || voterId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Parameters"));
            return;
        }

        getDb().collection("Elections")
                .document(electionId)
                .collection("voters")
                .document(voterId.trim())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }
}
