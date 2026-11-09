package com.example.dira;

import java.util.HashMap;
import java.util.Map;

public class Ballot {
    private String id;
    private String electionId;
    private String voterId;
    private Map<String, String> selectedCandidates;
    private String timestamp;
    private String receiptReference;
    private String transactionHash;

    public Ballot() {
        selectedCandidates = new HashMap<>();
    }

    public Ballot(String id, String electionId, String voterId, Map<String, String> selectedCandidates, String timestamp, String receiptReference, String transactionHash) {
        this.id = id;
        this.electionId = electionId;
        this.voterId = voterId;
        this.selectedCandidates = selectedCandidates != null ? selectedCandidates : new HashMap<>();
        this.timestamp = timestamp;
        this.receiptReference = receiptReference;
        this.transactionHash = transactionHash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public Map<String, String> getSelectedCandidates() {
        return selectedCandidates;
    }

    public void setSelectedCandidates(Map<String, String> selectedCandidates) {
        this.selectedCandidates = selectedCandidates;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiptReference() {
        return receiptReference;
    }

    public void setReceiptReference(String receiptReference) {
        this.receiptReference = receiptReference;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
