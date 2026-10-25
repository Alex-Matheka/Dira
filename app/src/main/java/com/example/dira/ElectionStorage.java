package com.example.dira;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ElectionStorage {

    private static final String COLLECTION_NAME = "Elections";

    public interface OnElectionSavedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnElectionsLoadedListener {
        void onSuccess(List<Election> elections);
        void onFailure(Exception e);
    }

    public interface OnElectionDeletedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    private static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    public static void saveElection(Election election, OnElectionSavedListener listener) {
        String docId = (election.getId() != null && !election.getId().trim().isEmpty())
                ? election.getId().trim()
                : getDb().collection(COLLECTION_NAME).document().getId();

        election.setId(docId);

        getDb().collection(COLLECTION_NAME)
                .document(docId)
                .set(election)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    public static void getElections(OnElectionsLoadedListener listener) {
        getDb().collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Election> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Election e = doc.toObject(Election.class);
                        if (e.getId() == null || e.getId().isEmpty()) {
                            e.setId(doc.getId());
                        }
                        list.add(e);
                    }
                    if (listener != null) listener.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    public static void deleteElection(String electionId, OnElectionDeletedListener listener) {
        if (electionId == null || electionId.trim().isEmpty()) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid Election ID"));
            return;
        }

        getDb().collection(COLLECTION_NAME)
                .document(electionId.trim())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }
}
