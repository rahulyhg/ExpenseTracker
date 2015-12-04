package com.expensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Krush on 10-Nov-15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Expense.db";
    //Defining ACCOUNT Table
    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String ACCOUNT_COLUMN_ID = "_id";
    public static final String ACCOUNT_COLUMN_NAME = "name";
    public static final String ACCOUNT_COLUMN_IMAGE = "image"; //Optional
    public static final String ACCOUNT_COLUMN_SELECTED = "selected";
    //Defining Category Income Table
    public static final String ICATEGORY_TABLE_NAME = "i_category";
    public static final String ICATEGORY_COLUMN_ID = "_id";
    public static final String ICATEGORY_COLUMN_NAME = "name";
    public static final String ICATEGORY_COLUMN_IMAGE = "image"; //Optional
    public static final String ICATEGORY_COLUMN_DESC = "description"; //Optional
    public static final String ICATEGORY_COLUMN_DEFVAL = "def_value"; //Optional
    //Defining Category Expense Table
    public static final String ECATEGORY_TABLE_NAME = "e_category";
    public static final String ECATEGORY_COLUMN_ID = "_id";
    public static final String ECATEGORY_COLUMN_NAME = "name";
    public static final String ECATEGORY_COLUMN_IMAGE = "image"; //Optional
    public static final String ECATEGORY_COLUMN_DESC = "description"; //Optional
    public static final String ECATEGORY_COLUMN_DEFVAL = "def_value"; //Optional
    public static final String ECATEGORY_COLUMN_CONSTANT = "constant"; // Optional
    public static final String ECATEGORY_COLUMN_VARIABLE = "variable"; // Optional
    //Defining Income Table
    public static final String INCOME_TABLE_NAME = "income";
    public static final String INCOME_COLUMN_ID = "_id";
    public static final String INCOME_COLUMN_AMOUNT = "amount";
    public static final String INCOME_COLUMN_IMAGE = "image";
    public static final String INCOME_COLUMN_TITLE = "title";
    public static final String INCOME_COLUMN_CATEGORY = "category";
    public static final String INCOME_COLUMN_ACCOUNT = "account";
    public static final String INCOME_COLUMN_DATE = "date";


    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ACCOUNT_TABLE_NAME + "(" +
                ACCOUNT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ACCOUNT_COLUMN_NAME + " TEXT, " + ACCOUNT_COLUMN_IMAGE + " BLOB, " + ACCOUNT_COLUMN_SELECTED + " INTEGER)");

        db.execSQL("CREATE TABLE " + ICATEGORY_TABLE_NAME + "(" +
                ICATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ICATEGORY_COLUMN_NAME + " TEXT, " + ICATEGORY_COLUMN_IMAGE + " BLOB, " + ICATEGORY_COLUMN_DESC + " TEXT, " +
                ICATEGORY_COLUMN_DEFVAL + " FLOAT)");

        db.execSQL("CREATE TABLE " + ECATEGORY_TABLE_NAME + "(" +
                ECATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ECATEGORY_COLUMN_NAME + " TEXT, " + ECATEGORY_COLUMN_IMAGE + " BLOB, " + ECATEGORY_COLUMN_DESC + " TEXT, " +
                ECATEGORY_COLUMN_DEFVAL + " FLOAT, " + ECATEGORY_COLUMN_CONSTANT + " INTEGER, " +
                ECATEGORY_COLUMN_VARIABLE + " INTEGER)");

        db.execSQL("CREATE TABLE " + INCOME_TABLE_NAME + "(" +
                INCOME_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                INCOME_COLUMN_AMOUNT + " FLOAT, " + INCOME_COLUMN_IMAGE + " BLOB, " + INCOME_COLUMN_CATEGORY + " INTEGER, " +
                INCOME_COLUMN_TITLE + " TEXT, " + INCOME_COLUMN_ACCOUNT + " INTEGER, " +
                INCOME_COLUMN_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ICATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ECATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ECATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INCOME_TABLE_NAME);

        onCreate(db);
    }

    public boolean insertAccount(String name, byte[] image, int isSelected) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_IMAGE, image);
        contentValues.put(ACCOUNT_COLUMN_SELECTED, isSelected);
        db.insert(ACCOUNT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateAccount(int id, String name, int isSelected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_SELECTED, isSelected);
        db.update(ACCOUNT_TABLE_NAME, contentValues, ACCOUNT_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateAccountWithImage(int id, String name, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_IMAGE, image);
        db.update(ACCOUNT_TABLE_NAME, contentValues, ACCOUNT_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getAccount(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " +
                ACCOUNT_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public int getAccount(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " +
                ACCOUNT_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        return res.getInt(res.getColumnIndex(ACCOUNT_COLUMN_ID));
    }

    public Cursor getAllAccounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME, null);
        return res;
    }

    public int deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ACCOUNT_TABLE_NAME,
                ACCOUNT_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    // Income Category
    public boolean insertCategoryI(String name, String description, byte[] image, float defValue) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICATEGORY_COLUMN_NAME, name);
        contentValues.put(ICATEGORY_COLUMN_DESC, description);
        contentValues.put(ICATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ICATEGORY_COLUMN_DEFVAL, defValue);
        db.insert(ICATEGORY_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateCategoryIWithImage(int id, String name, String desc, byte[] image, float defValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICATEGORY_COLUMN_NAME, name);
        contentValues.put(ICATEGORY_COLUMN_DESC, desc);
        contentValues.put(ICATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ICATEGORY_COLUMN_DEFVAL, defValue);
        db.update(ICATEGORY_TABLE_NAME, contentValues, ICATEGORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getCategoryI(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ICATEGORY_TABLE_NAME + " WHERE " +
                ICATEGORY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public int getCategoryI(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ICATEGORY_TABLE_NAME + " WHERE " +
                ICATEGORY_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        return res.getInt(res.getColumnIndex(ICATEGORY_COLUMN_ID));
    }

    public Cursor getAllCategoriesI() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ICATEGORY_TABLE_NAME, null);
        return res;
    }

    public int deleteCategoryI(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ICATEGORY_TABLE_NAME,
                ICATEGORY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    // Expense Category
    public boolean insertCategoryE(String name, String description, byte[] image, float defValue, int constant, int variable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ECATEGORY_COLUMN_NAME, name);
        contentValues.put(ECATEGORY_COLUMN_DESC, description);
        contentValues.put(ECATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ECATEGORY_COLUMN_DEFVAL, defValue);
        contentValues.put(ECATEGORY_COLUMN_CONSTANT, constant);
        contentValues.put(ECATEGORY_COLUMN_VARIABLE, variable);
        db.insert(ECATEGORY_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateCategoryEWithImage(int id, String name, String desc, byte[] image, float defValue, int constant, int variable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ECATEGORY_COLUMN_NAME, name);
        contentValues.put(ECATEGORY_COLUMN_DESC, desc);
        contentValues.put(ECATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ECATEGORY_COLUMN_DEFVAL, defValue);
        contentValues.put(ECATEGORY_COLUMN_CONSTANT, constant);
        contentValues.put(ECATEGORY_COLUMN_VARIABLE, variable);
        db.update(ECATEGORY_TABLE_NAME, contentValues, ECATEGORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getCategoryE(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME + " WHERE " +
                ECATEGORY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public int getCategoryE(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME + " WHERE " +
                ECATEGORY_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        return res.getInt(res.getColumnIndex(ECATEGORY_COLUMN_ID));
    }

    public Cursor getAllCategoriesE() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME, null);
        return res;
    }

    public int deleteCategoryE(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ECATEGORY_TABLE_NAME,
                ECATEGORY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    // Income Table
    public boolean insertIncome(float amount, String description, byte[] image, int account, int category, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME_COLUMN_AMOUNT, amount);
        contentValues.put(INCOME_COLUMN_TITLE, description);
        contentValues.put(INCOME_COLUMN_IMAGE, image);
        contentValues.put(INCOME_COLUMN_ACCOUNT, account);
        contentValues.put(INCOME_COLUMN_CATEGORY, category);
        contentValues.put(INCOME_COLUMN_DATE, date);
        db.insert(INCOME_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateIncomeWithImage(int id, float amount, String description, byte[] image, int account, int category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME_COLUMN_AMOUNT, amount);
        contentValues.put(INCOME_COLUMN_TITLE, description);
        contentValues.put(INCOME_COLUMN_IMAGE, image);
        contentValues.put(INCOME_COLUMN_ACCOUNT, account);
        contentValues.put(INCOME_COLUMN_CATEGORY, category);
        contentValues.put(INCOME_COLUMN_DATE, date);
        db.update(INCOME_TABLE_NAME, contentValues, INCOME_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getIncome(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + INCOME_TABLE_NAME + " WHERE " +
                INCOME_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public int getIncome(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME + " WHERE " +
                ECATEGORY_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        return res.getInt(res.getColumnIndex(ECATEGORY_COLUMN_ID));
    }

    public Cursor getAllIncomes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + INCOME_TABLE_NAME, null);
        return res;
    }

    public int deleteIncome(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INCOME_TABLE_NAME,
                INCOME_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
}
