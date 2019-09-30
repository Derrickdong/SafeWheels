package com.project.safewheels.Entity;

public class EmergencyListItem {

    private String title;
    private String Intro;
    private int image;

    public EmergencyListItem(){

    }

    public EmergencyListItem(String title, String intro, int image) {
        this.title = title;
        Intro = intro;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return Intro;
    }

    public void setIntro(String intro) {
        Intro = intro;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
