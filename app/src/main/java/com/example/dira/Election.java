package com.example.dira;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Election {
    private String id;
    private String title;
    private String date;
    private String duration;
    private List<String> sections;
    private String instructions;
    public static final String STATUS_UPCOMING = "UPCOMING";
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private String status = STATUS_UPCOMING;

    public Election() {
        sections = new ArrayList<>();
    }

    public Election(String id, String title, String date, String duration, List<String> sections, String instructions) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.duration = duration;
        this.sections = sections != null ? sections : new ArrayList<>();
        this.instructions = instructions;
        this.status = STATUS_UPCOMING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public String getInstructions() {
        return instructions;
    }


    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getStatus() {
        return status != null ? status : STATUS_UPCOMING;
    }

    public void setStatus(String status) {
        this.status = (status == null || status.trim().isEmpty())
                ? STATUS_UPCOMING
                : status.trim().toUpperCase(java.util.Locale.ROOT);
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("title", title);
        json.put("date", date);
        json.put("duration", duration);

        JSONArray secArray = new JSONArray();
        if (sections != null) {
            for (String sec : sections) {
                secArray.put(sec);
            }
        }
        json.put("sections", secArray);
        json.put("instructions", instructions);
        json.put("status", status);
        return json;
    }

    public static Election fromJsonObject(JSONObject json) throws JSONException {
        Election election = new Election();
        election.setId(json.optString("id", ""));
        election.setTitle(json.optString("title", ""));
        election.setDate(json.optString("date", ""));
        election.setDuration(json.optString("duration", ""));
        election.setInstructions(json.optString("instructions", ""));
        election.setStatus(
                json.optString("status", STATUS_UPCOMING)
        );

        List<String> secList = new ArrayList<>();
        JSONArray secArray = json.optJSONArray("sections");
        if (secArray != null) {
            for (int i = 0; i < secArray.length(); i++) {
                secList.add(secArray.getString(i));
            }
        }
        election.setSections(secList);
        return election;
    }
}
