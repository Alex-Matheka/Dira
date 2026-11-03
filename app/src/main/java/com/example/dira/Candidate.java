package com.example.dira;

public class Candidate {
    private String id;
    private String electionId;
    private String name;
    private String position;
    private String party;

    public Candidate() {
    }

    public Candidate(String id, String electionId, String name, String position, String party) {
        this.id = id;
        this.electionId = electionId;
        this.name = name;
        this.position = position;
        this.party = party;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }
}
