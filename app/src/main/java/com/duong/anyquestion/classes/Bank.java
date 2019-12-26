package com.duong.anyquestion.classes;

public class Bank {
    int bank_id;
    String bank_name;

    public Bank(int bank_id, String bank_name) {
        this.bank_id = bank_id;
        this.bank_name = bank_name;
    }

    public Bank() {

    }

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    @Override
    public String toString() {
        return bank_name;
    }
}
