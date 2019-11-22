package com.duong.anyquestion.classes;

import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    private int message_id;
    private String sender;
    private String message;
    private boolean typeImage;
    private String time;

    public int getMessage_id() {
        return message_id;
    }

    public Message( String sender, String message, boolean typeImage) {
        this.sender = sender;
        this.message = message;
        this.typeImage = typeImage;
        SetTimeNow();
    }

    public Message(String sender, String message) {
        this.message = message;
        this.sender = sender;
        this.typeImage = false;
        SetTimeNow();
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isTypeImage() {
        return typeImage;
    }

    public void setTypeImage(boolean typeImage) {
        this.typeImage = typeImage;
    }



    private void SetTimeNow(){
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        this.time = today.format("%H:%M");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender.getAccount();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("message_id", getMessage_id());
            jsonObject.put("sender", getSender());
            jsonObject.put("message", getMessage());
            jsonObject.put("typeImage", isTypeImage());
            jsonObject.put("time", getTime());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}
