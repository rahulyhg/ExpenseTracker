package com.expensetracker.modal;

/**
 * Created by Krush on 13-Nov-15.
 */
public class CategoryExpense {
    public long id;
    public byte[] image;
    public String name;
    public String desc;
    public float defValue;
    public int constant_expense, variable_expense;

    public CategoryExpense() {

    }

    public CategoryExpense(long id, byte[] image, String name, String desc, float defValue, int constant_expense, int variable_expense) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.defValue = defValue;
        this.constant_expense = constant_expense;
        this.variable_expense = variable_expense;
    }

    public float getDefValue() {
        return defValue;
    }

    public void setDefValue(float defValue) {
        this.defValue = defValue;
    }

    public int getConstant_expense() {
        return constant_expense;
    }

    public void setConstant_expense(int constant_expense) {
        this.constant_expense = constant_expense;
    }

    public int getVariable_expense() {
        return variable_expense;
    }

    public void setVariable_expense(int variable_expense) {
        this.variable_expense = variable_expense;
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
