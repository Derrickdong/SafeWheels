package com.project.safewheels.Entity;

public class RepairDetail {
    private String steps;
    private int image;

    public RepairDetail(String steps, int image) {
        this.steps = steps;
        this.image = image;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
