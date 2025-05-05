package com.example.Tensorflow;

public class House {
    private double price;
    private int bedrooms;
    private double bathrooms;
    private int sqftLiving;
    private int sqftLot;
    private double floors;
    private int waterfront;
    private int view;
    private int condition;
    private int sqftAbove;
    private int sqftBasement;
    private int yrBuilt;
    private int yrRenovated;

    public House( double price, int bedrooms, double bathrooms, int sqftLiving, int sqftLot, double floors, int waterfront, int view, int condition, int sqftAbove, int sqftBasement, int yrBuilt, int yrRenovated) {
        this.price = price;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.sqftLiving = sqftLiving;
        this.sqftLot = sqftLot;
        this.floors = floors;
        this.waterfront = waterfront;
        this.view = view;
        this.condition = condition;
        this.sqftAbove = sqftAbove;
        this.sqftBasement = sqftBasement;
        this.yrBuilt = yrBuilt;
        this.yrRenovated = yrRenovated;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public double getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(double bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getSqftLiving() {
        return sqftLiving;
    }

    public void setSqftLiving(int sqftLiving) {
        this.sqftLiving = sqftLiving;
    }

    public int getSqftLot() {
        return sqftLot;
    }

    public void setSqftLot(int sqftLot) {
        this.sqftLot = sqftLot;
    }

    public double getFloors() {
        return floors;
    }

    public void setFloors(double floors) {
        this.floors = floors;
    }

    public int getWaterfront() {
        return waterfront;
    }

    public void setWaterfront(int waterfront) {
        this.waterfront = waterfront;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getSqftAbove() {
        return sqftAbove;
    }

    public void setSqftAbove(int sqftAbove) {
        this.sqftAbove = sqftAbove;
    }

    public int getSqftBasement() {
        return sqftBasement;
    }

    public void setSqftBasement(int sqftBasement) {
        this.sqftBasement = sqftBasement;
    }

    public int getYrBuilt() {
        return yrBuilt;
    }

    public void setYrBuilt(int yrBuilt) {
        this.yrBuilt = yrBuilt;
    }

    public int getYrRenovated() {
        return yrRenovated;
    }

    public void setYrRenovated(int yrRenovated) {
        this.yrRenovated = yrRenovated;
    }
}