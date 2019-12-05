package com.duong.anyquestion.classes;

import android.accounts.Account;

import org.json.JSONException;
import org.json.JSONObject;

public class User {


    String user_id;
    String Password;
    String FullName;
    String avatar;
    String Address;
    String Email;
    int money;


    public User(String account, String fullName) {
        user_id = account;
        FullName = fullName;
    }




    public User(String account, String password, String fullName, String address, String email) {
        user_id = account;
        Password = password;
        FullName = fullName;
        Address = address;
        Email = email;
    }


    public User() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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
            jsonObject.put("user_id", getUser_id());
            jsonObject.put("Password", getPassword());
            jsonObject.put("FullName", getFullName());
            jsonObject.put("Address", getAddress());
            jsonObject.put("Email", getEmail());
            jsonObject.put("money", getMoney());
            jsonObject.put("avatar", getAvatar());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }
}
