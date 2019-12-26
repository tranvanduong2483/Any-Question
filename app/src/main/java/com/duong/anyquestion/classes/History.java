package com.duong.anyquestion.classes;

public class History {
    private int conversation_id;
    private int question_id;
    private String title;
    private String image;
    private String name;
    private float star;
    private String id_user;
    private String id_expert;

    public History(int conversation_id, int question_id, String title, String image, String name, float star, String id_user, String id_expert) {
        this.conversation_id = conversation_id;
        this.question_id = question_id;
        this.title = title;
        this.image = image;
        this.name = name;
        this.star = star;
        this.id_user = id_user;
        this.id_expert = id_expert;
    }

    public History() {

    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_expert() {
        return id_expert;
    }

    public void setId_expert(String id_expert) {
        this.id_expert = id_expert;
    }
}
