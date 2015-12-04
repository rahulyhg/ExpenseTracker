package com.expensetracker.modal;

/**
 * Created by Krush on 04-Dec-15.
 */
public class Income {

    public float amount;
    public long id;
    public byte[] image;
    public String title;
    public int account;
    public int category;
    public String date;

    public Income(float amount, long id, byte[] image, String title, int account, int category, String date) {
        this.amount = amount;
        this.id = id;
        this.image = image;
        this.title = title;
        this.account = account;
        this.category = category;
        this.date = date;
    }

    public Income() {

    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public String getDescription() {
        return title;
    }

    public void setDescription(String title) {
        this.title = title;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
