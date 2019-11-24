package com.duong.anyquestion.classes;

public class History {


    int conversation_id;
    String title;
    String name;
    int star;

    public History(int conversation_id, String tittle, String field, int star) {
        this.conversation_id = conversation_id;
        this.title = tittle;
        this.name = field;
        this.star = star;
    }

    public History() {

    }
}
