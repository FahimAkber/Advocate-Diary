package com.latentsoft.advocatenote.model;

public class HistoryModel {
    String id;
    String comment;
    String nextStep;
    long date;

    public HistoryModel(String id, String comment, String nextStep, long date) {
        this.id = id;
        this.comment = comment;
        this.nextStep = nextStep;
        this.date = date;
    }

    public HistoryModel() {
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getNextStep() {
        return nextStep;
    }

    public long getDate() {
        return date;
    }
}
