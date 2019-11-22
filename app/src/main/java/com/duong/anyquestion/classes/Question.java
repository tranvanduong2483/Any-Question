package com.duong.anyquestion.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Question {
    int id;
    String tittle;
    int field_id;
    String imageString;
    String note;
    int money;

    public Question(int id, String tittle, int field_id, String imageString, String note, int money) {
        this.id = id;
        this.tittle = tittle;
        this.field_id = field_id;
        this.imageString = imageString;
        this.note = note;
        this.money = money;
    }

    public Question() {
    }

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

    public int getFieldID() {
        return field_id;
    }

    public void setFieldId(int field) {
        this.field_id = field;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getId());
            jsonObject.put("tittle", getTittle());
            jsonObject.put("field", getFieldID());
            jsonObject.put("imageString", getImageString());
            jsonObject.put("note", getNote());
            jsonObject.put("money", getMoney());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}


