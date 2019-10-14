package com.project.safewheels.Entity;


import java.util.ArrayList;
import java.util.List;

/**
 * It is an Entity class that contains a list of favorite objects
 * This is a class that make the implementation easier
 */

public class FavoriteAddresses {

    private List<Favorite> addressList;

    public FavoriteAddresses(List<Favorite> addressList) {
        this.addressList = addressList;
    }

    public List<Favorite> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Favorite> addressList) {
        this.addressList = addressList;
    }

    @Override
    public String toString() {
        return "FavoriteAddresses{" +
                "addressList=" + addressList +
                '}';
    }

    public boolean isContain(String placeId){
        for (Favorite favorite1:addressList){
            if (placeId.equals(favorite1.getId())){
                return true;
            }
        }
        return false;
    }

    public void remove(String placeId){
        ArrayList<Favorite> newList = new ArrayList<>();
        for (Favorite favorite: addressList){
            if (favorite.getId().equals(placeId)){
                newList.add(favorite);
            }
        }
        addressList.removeAll(newList);
    }

    public void add(Favorite favorite){
        addressList.add(favorite);
    }

    public int size(){
        return addressList.size();
    }
}
