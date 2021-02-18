package com.latentsoft.advocatenote.model;

import java.io.Serializable;

public class CaseModel  implements Serializable {

    String id;
    String title;
    String description;
    String complainant;
    String defendant;
    long date;

    public CaseModel(String id, String title, String description, String complainant, String defendant, long date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.complainant = complainant;
        this.defendant = defendant;
        this.date = date;
    }

    public CaseModel() {

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getComplainant() {
        return complainant;
    }

    public String getDefendant() {
        return defendant;
    }

    public long getDate() {
        return date;
    }
}
