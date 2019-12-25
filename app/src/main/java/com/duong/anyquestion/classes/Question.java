package com.duong.anyquestion.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Question implements Serializable {
    int question_id;
    int field_id;
    String title;
    String image;
    String detailed_description;
    int money;
    String user_id;

    public Question(int field_id, String tittle, String image, String detailed_description, int money, String user_id) {
        this.field_id = field_id;
        this.title = tittle;
        this.image = image;
        this.detailed_description = detailed_description;
        this.money = money;
        this.user_id = user_id;
    }

    public Question() {
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getField_id() {
        return field_id;
    }

    public void setField_id(int field_id) {
        this.field_id = field_id;
    }

    public String getTittle() {
        return title;
    }

    public void setTittle(String tittle) {
        this.title = tittle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetailed_description() {
        return detailed_description;
    }

    public void setDetailed_description(String detailed_description) {
        this.detailed_description = detailed_description;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("question_id", question_id);
            jsonObject.put("title", title);
            jsonObject.put("field_id", field_id);
            jsonObject.put("image", image);
            jsonObject.put("detailed_description", detailed_description);
            jsonObject.put("money", money);
            jsonObject.put("user_id", user_id);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}


