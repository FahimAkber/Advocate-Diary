package com.latentsoft.advocatenote.model;

public class AdvocateModel {
    String name;
    String court;
    String email;

    public AdvocateModel() {
    }

    public AdvocateModel(String name, String court, String email) {
        this.name = name;
        this.court = court;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getCourt() {
        return court;
    }

    public String getEmail() {
        return email;
    }
}
