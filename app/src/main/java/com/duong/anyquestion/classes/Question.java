package com.duong.anyquestion.classes;

public class Question {
    int id;
    String tittle;
    String field;
    String imageString;
    String descriptdetail;
    String keywords;

    public Question(String tittle, String field, String imageString, String descriptdetail, String keywords) {
        this.tittle = tittle;
        this.field = field;
        this.imageString = imageString;
        this.descriptdetail = descriptdetail;
        this.keywords = keywords;
    }

    public Question(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getDescriptdetail() {
        return descriptdetail;
    }

    public void setDescriptdetail(String descriptdetail) {
        this.descriptdetail = descriptdetail;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}


