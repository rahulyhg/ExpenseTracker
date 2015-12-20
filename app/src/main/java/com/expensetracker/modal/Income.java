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
    public String account_name;
    public int category;
    public String category_name;
    public long date;

    public Income(float amount, long id, byte[] image, String title, int account, String account_name, int category, String category_name, long date) {
        this.amount = amount;
        this.id = id;
        this.image = image;
        this.title = title;
        this.account = account;
        this.account_name = account_name;
        this.category = category;
        this.category_name = category_name;
        this.date = date;
    }

    public Income() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
