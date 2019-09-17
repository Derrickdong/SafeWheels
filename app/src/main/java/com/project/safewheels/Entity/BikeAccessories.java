package com.project.safewheels.Entity;

public class BikeAccessories {
    private String baName;
    private String baRepairDuration;
    private int baImage;

    private int detailImage;

    public BikeAccessories(String baName, String baRepairDuration, int baImage, int dImage) {
        this.baName = baName;
        this.baRepairDuration = baRepairDuration;
        this.baImage = baImage;
        this.detailImage = dImage;
    }

    public String getString() {
        return this.baName + " " + this.baRepairDuration + " days " + this.baImage;
    }

    public int getDetailImage() {
        return detailImage;
    }

    public void setDetailImage(int detailImage) {
        this.detailImage = detailImage;
    }

    public String getBaName() {
        return baName;
    }

    public void setBaName(String baName) {
        this.baName = baName;
    }

    public String getBaRepairDuration() {
        return baRepairDuration;
    }

    public void setBaRepairDuration(String baRepairDuration) {
        this.baRepairDuration = baRepairDuration;
    }

    public int getBaImage() {
        return baImage;
    }

    public void setBaImage(int baImage) {
        this.baImage = baImage;
    }
}
