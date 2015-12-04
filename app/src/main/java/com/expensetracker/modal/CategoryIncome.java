package com.expensetracker.modal;

/**
 * Created by Krush on 13-Nov-15.
 */
public class CategoryIncome {
    public long id;
    public byte[] image;
    public String name;
    public String desc;
    public float defValue;

    public CategoryIncome() {

    }

    public CategoryIncome(long id, byte[] image, String name, String desc, float defValue) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.defValue = defValue;
    }

    public float getDefValue() {
        return defValue;
    }

    public void setDefValue(float defValue) {
        this.defValue = defValue;
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
