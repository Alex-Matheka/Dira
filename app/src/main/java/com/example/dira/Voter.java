package com.example.dira;

public class Voter {
    private String id;
    private String electionId;
    private String name;
    private String phone;
    private String status = "Registered";

    public Voter() {
    }

    public Voter(String id, String electionId, String name, String phone, String status) {
        this.id = id;
        this.electionId = electionId;
        this.name = name;
        this.phone = phone;
        this.status = status != null ? status : "Registered";
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status != null ? status : "Registered";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
