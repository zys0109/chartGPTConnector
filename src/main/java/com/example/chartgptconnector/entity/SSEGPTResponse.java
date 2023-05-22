package com.example.chartgptconnector.entity;

import java.util.List;

public class SSEGPTResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<SSEGPTChoice> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<SSEGPTChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<SSEGPTChoice> choices) {
        this.choices = choices;
    }
}
