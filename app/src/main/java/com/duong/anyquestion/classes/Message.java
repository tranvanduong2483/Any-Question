package com.duong.anyquestion.classes;

import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    private int message_id;
    private int conversation_id;
    private String from;
    private String message;
    private boolean typeImage;
    private String time;

    public Message() {
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTypeImage() {
        return typeImage;
    }

    public void setTypeImage(boolean typeImage) {
        this.typeImage = typeImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Message(int conversation_id, String from, String message, boolean typeImage) {
        this.conversation_id = conversation_id;
        this.from = from;
        this.message = message;
        this.typeImage = typeImage;
        SetTimeNow();
    }


    private void SetTimeNow() {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        this.time = today.format("%H:%M");
    }

    public String toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("conversation_id", getConversation_id());
            jsonObject.put("from", getFrom());
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
