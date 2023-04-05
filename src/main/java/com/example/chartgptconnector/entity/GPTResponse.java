package com.example.chartgptconnector.entity;

import java.util.List;

//请求openai返回对象
public class GPTResponse {
    private String id;
    private String object;
    private Integer created;
    private String model;
    private List<GPTChoice> choices;
    private GPTUsage usage;

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

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<GPTChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<GPTChoice> choices) {
        this.choices = choices;
    }

    public GPTUsage getUsage() {
        return usage;
    }

    public void setUsage(GPTUsage usage) {
        this.usage = usage;
    }
}
