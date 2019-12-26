package com.duong.anyquestion.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentRequest {
    int request_id;
    String expert_id;
    int bank_id;
    int money;
    String account_number;
    String account_name;

    public PaymentRequest(String expert_id, int bank_id, int money, String account_number, String account_name) {
        this.expert_id = expert_id;
        this.bank_id = bank_id;
        this.money = money;
        this.account_number = account_number;
        this.account_name = account_name;
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public String getExpert_id() {
        return expert_id;
    }

    public void setExpert_id(String expert_id) {
        this.expert_id = expert_id;
    }

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("expert_id", getExpert_id());
            jsonObject.put("bank_id", getBank_id());
            jsonObject.put("money", getMoney());
            jsonObject.put("account_number", getAccount_number());
            jsonObject.put("account_name", getAccount_name());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }


}
