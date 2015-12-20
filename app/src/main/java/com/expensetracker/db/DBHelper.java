package com.expensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.expensetracker.expensetracker.DateUtili;

/**
 * Created by Krush on 10-Nov-15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "Expense.db";
    //Defining ACCOUNT Table
    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String ACCOUNT_COLUMN_ID = "account_id";
    public static final String ACCOUNT_COLUMN_NAME = "account_name";
    public static final String ACCOUNT_COLUMN_IMAGE = "account_image"; //Optional
    public static final String ACCOUNT_COLUMN_SELECTED = "account_selected";
    //Defining Category Income Table
    public static final String ICATEGORY_TABLE_NAME = "i_category";
    public static final String ICATEGORY_COLUMN_ID = "i_id";
    public static final String ICATEGORY_COLUMN_NAME = "i_name";
    public static final String ICATEGORY_COLUMN_IMAGE = "i_image"; //Optional
    public static final String ICATEGORY_COLUMN_DESC = "i_description"; //Optional
    public static final String ICATEGORY_COLUMN_DEFVAL = "i_def_value"; //Optional
    //Defining Category Expense Table
    public static final String ECATEGORY_TABLE_NAME = "e_category";
    public static final String ECATEGORY_COLUMN_ID = "e_id";
    public static final String ECATEGORY_COLUMN_NAME = "e_name";
    public static final String ECATEGORY_COLUMN_IMAGE = "e_image"; //Optional
    public static final String ECATEGORY_COLUMN_DESC = "e_description"; //Optional
    public static final String ECATEGORY_COLUMN_DEFVAL = "e_def_value"; //Optional
    public static final String ECATEGORY_COLUMN_CONSTANT = "e_constant"; // Optional
    public static final String ECATEGORY_COLUMN_VARIABLE = "e_variable"; // Optional
    public static final String INCOME_TABLE_NAME = "income";
    public static final String INCOME_COLUMN_ID = "income_id";
    public static final String INCOME_COLUMN_AMOUNT = "income_amount";
    public static final String INCOME_COLUMN_IMAGE = "income_image";
    public static final String INCOME_COLUMN_TITLE = "income_title";
    public static final String INCOME_COLUMN_CATEGORY = "income_category_id";
    public static final String INCOME_COLUMN_ACCOUNT = "income_account_id";
    public static final String INCOME_COLUMN_DATE = "income_date";
    private static final int DATABASE_VERSION = 1;
    //Defining Income Table
    public static long AUTO_ID;
    private static DateUtili dateUtili = new DateUtili();

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
        Log.e(TAG, "CREATE TABLE " + ACCOUNT_TABLE_NAME + "(" +
                ACCOUNT_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                ACCOUNT_COLUMN_NAME + " TEXT NOT NULL, " + ACCOUNT_COLUMN_IMAGE + " BLOB, " + ACCOUNT_COLUMN_SELECTED + " INTEGER)");
        db.execSQL("CREATE TABLE " + ACCOUNT_TABLE_NAME + "(" +
                ACCOUNT_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                ACCOUNT_COLUMN_NAME + " TEXT NOT NULL, " + ACCOUNT_COLUMN_IMAGE + " BLOB, " + ACCOUNT_COLUMN_SELECTED + " INTEGER)");

        db.execSQL("CREATE TABLE " + ICATEGORY_TABLE_NAME + "(" +
                ICATEGORY_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                ICATEGORY_COLUMN_NAME + " TEXT NOT NULL, " + ICATEGORY_COLUMN_IMAGE + " BLOB, " + ICATEGORY_COLUMN_DESC + " TEXT, " +
                ICATEGORY_COLUMN_DEFVAL + " FLOAT NOT NULL)");

        db.execSQL("CREATE TABLE " + ECATEGORY_TABLE_NAME + "(" +
                ECATEGORY_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                ECATEGORY_COLUMN_NAME + " TEXT NOT NULL, " + ECATEGORY_COLUMN_IMAGE + " BLOB, " + ECATEGORY_COLUMN_DESC + " TEXT, " +
                ECATEGORY_COLUMN_DEFVAL + " FLOAT NOT NULL, " + ECATEGORY_COLUMN_CONSTANT + " INTEGER, " +
                ECATEGORY_COLUMN_VARIABLE + " INTEGER)");

        db.execSQL("CREATE TABLE " + INCOME_TABLE_NAME + "(" +
                INCOME_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                INCOME_COLUMN_AMOUNT + " FLOAT NOT NULL, " + INCOME_COLUMN_IMAGE + " BLOB, " + INCOME_COLUMN_CATEGORY + " INTEGER NOT NULL, " +
                INCOME_COLUMN_TITLE + " TEXT NOT NULL, " + INCOME_COLUMN_ACCOUNT + " INTEGER NOT NULL, " +
                INCOME_COLUMN_DATE + " LONG NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ICATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ECATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INCOME_TABLE_NAME);

        onCreate(db);
    }

    public boolean insertAccount(String name, byte[] image, int isSelected) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_IMAGE, image);
        contentValues.put(ACCOUNT_COLUMN_SELECTED, isSelected);
        db.insert(ACCOUNT_TABLE_NAME, null, contentValues);
        closeDB(db);
        return true;
    }

    public boolean updateAccount(int id, String name, int isSelected) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_SELECTED, isSelected);
        db.update(ACCOUNT_TABLE_NAME, contentValues, ACCOUNT_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        closeDB(db);
        return true;
    }

    public boolean updateAccountWithImage(int id, String name, byte[] image) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_IMAGE, image);
        db.update(ACCOUNT_TABLE_NAME, contentValues, ACCOUNT_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        closeDB(db);
        return true;
    }

    public Cursor getAccount(int id) {
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " +
                ACCOUNT_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public int getAccount(String name) {
        SQLiteDatabase db = readDB();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " +
                ACCOUNT_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        int id = res.getInt(res.getColumnIndex(ACCOUNT_COLUMN_ID));
        res.close();
        closeDB(db);
        return id;
    }

    public Cursor getAllAccounts() {
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME, null);
    }

    public int deleteAccount(int id) {
        SQLiteDatabase db = writeDB();
        return db.delete(ACCOUNT_TABLE_NAME,
                ACCOUNT_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    // Income Category
    public boolean insertCategoryI(String name, String description, byte[] image, float defValue) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICATEGORY_COLUMN_NAME, name);
        contentValues.put(ICATEGORY_COLUMN_DESC, description);
        contentValues.put(ICATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ICATEGORY_COLUMN_DEFVAL, defValue);
        db.insert(ICATEGORY_TABLE_NAME, null, contentValues);
        closeDB(db);
        return true;
    }

    public boolean updateCategoryIWithImage(int id, String name, String desc, byte[] image, float defValue) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICATEGORY_COLUMN_NAME, name);
        contentValues.put(ICATEGORY_COLUMN_DESC, desc);
        contentValues.put(ICATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ICATEGORY_COLUMN_DEFVAL, defValue);
        db.update(ICATEGORY_TABLE_NAME, contentValues, ICATEGORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        closeDB(db);
        return true;
    }

    public Cursor getCategoryI(int id) {
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + ICATEGORY_TABLE_NAME + " WHERE " +
                ICATEGORY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public int getCategoryI(String name) {
        SQLiteDatabase db = readDB();
        Cursor res = db.rawQuery("SELECT * FROM " + ICATEGORY_TABLE_NAME + " WHERE " +
                ICATEGORY_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        int id = res.getInt(res.getColumnIndex(ICATEGORY_COLUMN_ID));
        res.close();
        closeDB(db);
        return id;
    }

    public Cursor getAllCategoriesI() {
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + ICATEGORY_TABLE_NAME, null);
    }

    public int deleteCategoryI(int id) {
        SQLiteDatabase db = writeDB();
        return db.delete(ICATEGORY_TABLE_NAME,
                ICATEGORY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    // Expense Category
    public boolean insertCategoryE(String name, String description, byte[] image, float defValue, int constant, int variable) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ECATEGORY_COLUMN_NAME, name);
        contentValues.put(ECATEGORY_COLUMN_DESC, description);
        contentValues.put(ECATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ECATEGORY_COLUMN_DEFVAL, defValue);
        contentValues.put(ECATEGORY_COLUMN_CONSTANT, constant);
        contentValues.put(ECATEGORY_COLUMN_VARIABLE, variable);
        db.insert(ECATEGORY_TABLE_NAME, null, contentValues);
        closeDB(db);
        return true;
    }

    public boolean updateCategoryEWithImage(int id, String name, String desc, byte[] image, float defValue, int constant, int variable) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ECATEGORY_COLUMN_NAME, name);
        contentValues.put(ECATEGORY_COLUMN_DESC, desc);
        contentValues.put(ECATEGORY_COLUMN_IMAGE, image);
        contentValues.put(ECATEGORY_COLUMN_DEFVAL, defValue);
        contentValues.put(ECATEGORY_COLUMN_CONSTANT, constant);
        contentValues.put(ECATEGORY_COLUMN_VARIABLE, variable);
        db.update(ECATEGORY_TABLE_NAME, contentValues, ECATEGORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        closeDB(db);
        return true;
    }

    public Cursor getCategoryE(int id) {
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME + " WHERE " +
                ECATEGORY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public int getCategoryE(String name) {
        SQLiteDatabase db = readDB();
        Cursor res = db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME + " WHERE " +
                ECATEGORY_COLUMN_NAME + "=?", new String[]{name});
        res.moveToFirst();
        return res.getInt(res.getColumnIndex(ECATEGORY_COLUMN_ID));
    }

    public Cursor getAllCategoriesE() {
        SQLiteDatabase db = readDB();
        Cursor res = db.rawQuery("SELECT * FROM " + ECATEGORY_TABLE_NAME, null);
        return res;
    }

    public int deleteCategoryE(int id) {
        SQLiteDatabase db = writeDB();
        return db.delete(ECATEGORY_TABLE_NAME,
                ECATEGORY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    // Income Table
    public boolean insertIncome(float amount, String description, byte[] image, int account, int category, long date) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME_COLUMN_AMOUNT, amount);
        contentValues.put(INCOME_COLUMN_TITLE, description);
        contentValues.put(INCOME_COLUMN_IMAGE, image);
        contentValues.put(INCOME_COLUMN_ACCOUNT, account);
        contentValues.put(INCOME_COLUMN_CATEGORY, category);
        contentValues.put(INCOME_COLUMN_DATE, date);
        AUTO_ID = db.insert(INCOME_TABLE_NAME, null, contentValues);
        closeDB(writeDB());
        return true;
    }

    public boolean updateIncomeWithImage(int id, float amount, String description, byte[] image, int account, int category, long date) {
        SQLiteDatabase db = writeDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME_COLUMN_AMOUNT, amount);
        contentValues.put(INCOME_COLUMN_TITLE, description);
        contentValues.put(INCOME_COLUMN_IMAGE, image);
        contentValues.put(INCOME_COLUMN_ACCOUNT, account);
        contentValues.put(INCOME_COLUMN_CATEGORY, category);
        contentValues.put(INCOME_COLUMN_DATE, date);
        db.update(INCOME_TABLE_NAME, contentValues, INCOME_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        closeDB(db);
        return true;
    }

    public Cursor getIncome(int id) {
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + INCOME_TABLE_NAME + " WHERE " +
                INCOME_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public Cursor getAllIncomes(int month) {
        String date;
        if (dateUtili.getMonth() + month < 0) {
            date = "1-" + (dateUtili.getMonth() + month) + "-" + (dateUtili.getYear() - 1);
        } else {
            date = "1-" + (dateUtili.getMonth() + month) + "-" + (dateUtili.getYear());
        }
        SQLiteDatabase db = readDB();
        return db.rawQuery("SELECT * FROM " + INCOME_TABLE_NAME + " JOIN " + ACCOUNT_TABLE_NAME
                + " ON " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_ACCOUNT + " = " + ACCOUNT_TABLE_NAME + "." + ACCOUNT_COLUMN_ID + " JOIN " +
                ICATEGORY_TABLE_NAME + " ON " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_CATEGORY + " = " + ICATEGORY_TABLE_NAME + "." +
                ICATEGORY_COLUMN_ID + " WHERE " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_DATE + ">=" + dateUtili.StringtoLong(date) + ";", null);
    }

    public Cursor getAllIncomesBetween(int fromMonth, int toMonth) {
        String fromDate, toDate;
        if (dateUtili.getMonth() + fromMonth < 0) {
            fromDate = "1-" + (12 + dateUtili.getMonth() + fromMonth) + "-" + (dateUtili.getYear() - 1);
            toDate = "31-" + (dateUtili.getMonth()) + "-" + (dateUtili.getYear() - 1);
        } else {
            fromDate = "1-" + (dateUtili.getMonth() + fromMonth) + "-" + (dateUtili.getYear());
            toDate = "31-" + (dateUtili.getMonth()) + "-" + (dateUtili.getYear());
        }
        SQLiteDatabase db = readDB();
        Log.e(TAG,"SELECT * FROM " + INCOME_TABLE_NAME + " JOIN " + ACCOUNT_TABLE_NAME
                + " ON " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_ACCOUNT + " = " + ACCOUNT_TABLE_NAME + "." + ACCOUNT_COLUMN_ID + " JOIN " +
                ICATEGORY_TABLE_NAME + " ON " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_CATEGORY + " = " + ICATEGORY_TABLE_NAME + "." +
                ICATEGORY_COLUMN_ID + " WHERE " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_DATE + " BETWEEN " +
                dateUtili.StringtoLong(fromDate) + " AND " + dateUtili.StringtoLong(toDate) + ";");
        return db.rawQuery("SELECT * FROM " + INCOME_TABLE_NAME + " JOIN " + ACCOUNT_TABLE_NAME
                + " ON " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_ACCOUNT + " = " + ACCOUNT_TABLE_NAME + "." + ACCOUNT_COLUMN_ID + " JOIN " +
                ICATEGORY_TABLE_NAME + " ON " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_CATEGORY + " = " + ICATEGORY_TABLE_NAME + "." +
                ICATEGORY_COLUMN_ID + " WHERE " + INCOME_TABLE_NAME + "." + INCOME_COLUMN_DATE + " BETWEEN " +
                dateUtili.StringtoLong(fromDate) + " AND " + dateUtili.StringtoLong(toDate) + ";", null);

    }

    public Cursor getSumIncome(int month) {
        String fromDate, toDate;
        if (dateUtili.getMonth() + month < 0) {
            fromDate = "1-" + (12 + dateUtili.getMonth() + month) + "-" + (dateUtili.getYear() - 1);
            toDate = "31-" + (dateUtili.getMonth()) + "-" + (dateUtili.getYear() - 1);
        } else {
            fromDate = "1-" + (dateUtili.getMonth() + month) + "-" + (dateUtili.getYear());
            toDate = "31-" + (dateUtili.getMonth()) + "-" + (dateUtili.getYear());
        }
        SQLiteDatabase db = readDB();
        // SQL QUery = select  distinct strftime('%m-%Y',datetime(income_date/1000,'unixepoch')),sum(income_amount) from income group by
        // (select distinct strftime('%m',datetime(income_date/1000,'unixepoch')));;
        Log.e(TAG, "SELECT DISTINCT strftime('%m-%Y',datetime(" + INCOME_COLUMN_DATE + "/1000,'unixepoch')), SUM(" + INCOME_COLUMN_AMOUNT + ") FROM " + INCOME_TABLE_NAME +
                " WHERE " + INCOME_COLUMN_DATE + " BETWEEN " + dateUtili.StringtoLong(fromDate) + " AND " +
                dateUtili.StringtoLong(toDate) + " GROUP BY ( SELECT DISTINCT strftime('%m',datetime( " + INCOME_COLUMN_DATE + "/1000,'unixepoch')));");
        return db.rawQuery("SELECT DISTINCT strftime('%m-%Y',datetime(" + INCOME_COLUMN_DATE + "/1000,'unixepoch')), SUM(" + INCOME_COLUMN_AMOUNT + ") FROM " + INCOME_TABLE_NAME +
                " WHERE " + INCOME_COLUMN_DATE + " BETWEEN " + dateUtili.StringtoLong(fromDate) + " AND " +
                dateUtili.StringtoLong(toDate) + " GROUP BY ( SELECT DISTINCT strftime('%m',datetime( " + INCOME_COLUMN_DATE + "/1000,'unixepoch')));", null);
        //res.moveToFirst();
        //float amount = res.getFloat(0);
        //res.close();
        //closeDB(db);
        //return amount;
    }

    public int deleteIncome(int id) {
        SQLiteDatabase db = readDB();

        return db.delete(INCOME_TABLE_NAME,
                INCOME_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    //Expense Section
    public float getSumExpense(int thisMonth) {
        return 2000.0f;
    }

    private SQLiteDatabase readDB() {
        return this.getReadableDatabase();
    }

    private SQLiteDatabase writeDB() {
        return this.getWritableDatabase();
    }

    private void closeDB(SQLiteDatabase db) {
        db.close();
    }

}
