package com.expensetracker.modal;

/**
 * Created by Krush on 13-Nov-15.
 */
public class Account {
    public long id;
    public int isSelected;
    public byte[] image;
    public String name;

    public Account() {

    }

    public Account(long id, byte[] image, String name, int isSelected) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.isSelected = isSelected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
