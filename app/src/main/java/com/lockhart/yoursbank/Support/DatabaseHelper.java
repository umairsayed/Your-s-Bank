package com.lockhart.yoursbank.Support;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String employee_login = "employee_table";
    private final String customer_login = "customer_table";
    private final String transaction = "transaction_table";

    public DatabaseHelper(Context context) {
        super(context, "YourBank.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + employee_login + " (ACCOUNT_NO varchar unique primary key not null, NAME varchar not null, ADDRESS varchar not null, EMAIL varchar not null, PHONE varchar not null, PASSWORD varchar not null)");
        db.execSQL("create table " + customer_login + " (ACCOUNT_NO varchar unique primary key not null, NAME varchar not null, ADDRESS varchar not null, EMAIL varchar not null, PHONE varchar not null, PASSWORD varchar not null,  BALANCE varchar not null)");
        db.execSQL("create table " + transaction + " (TIMESTAMP long not null, FROM_ACC varchar not null, TO_ACC varchar not null, TRANSACTION_TYPE varchar not null, AMOUNT varchar not null, COMMENTS varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + employee_login);
        db.execSQL("drop table if exists " + customer_login);
        db.execSQL("drop table if exists " + transaction);
    }

    private void updateBalance(String ini, String end, String amnt, String type) {
        SQLiteDatabase database = this.getWritableDatabase();

        if (type.equals("debit")) {
            if (ini.length() == 10) {
                String bal = getBalance(ini);
                ContentValues contentValues = new ContentValues();
                long val = Long.parseLong(bal) - Long.parseLong(amnt);
                contentValues.put("BALANCE", String.valueOf(val));
                database.update(customer_login, contentValues, "ACCOUNT_NO = " + ini, null);
                if (end.length() == 10) {
                    bal = getBalance(end);
                    contentValues = new ContentValues();
                    val = Long.parseLong(bal) + Long.parseLong(amnt);
                    contentValues.put("BALANCE", String.valueOf(val));
                    database.update(customer_login, contentValues, "ACCOUNT_NO = " + end, null);
                }
            } else if (ini.length() == 8) {
                if (end.length() == 10) {
                    String bal = getBalance(end);
                    ContentValues contentValues = new ContentValues();
                    long val = Long.parseLong(bal) - Long.parseLong(amnt);
                    contentValues.put("BALANCE", String.valueOf(val));
                    database.update(customer_login, contentValues, "ACCOUNT_NO = " + end, null);
                }
            }
        } else if (type.equals("credit")) {
            if (ini.length() == 8) {
                if (end.length() == 10) {
                    String bal = getBalance(end);
                    ContentValues contentValues = new ContentValues();
                    long val = Long.parseLong(bal) + Long.parseLong(amnt);
                    contentValues.put("BALANCE", String.valueOf(val));
                    database.update(customer_login, contentValues, "ACCOUNT_NO = " + end, null);
                }
            }
        }
    }

    public boolean doTransaction(String frm_acc, String to_acc, String type, String amount, long timestamp, String cmnt) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("TIMESTAMP", timestamp);
            contentValues.put("FROM_ACC", frm_acc);
            contentValues.put("TO_ACC", to_acc);
            contentValues.put("TRANSACTION_TYPE", type);
            contentValues.put("AMOUNT", amount);
            contentValues.put("COMMENTS", cmnt);

            database.insert(transaction, null, contentValues);

            updateBalance(frm_acc, to_acc, amount, type);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("Recycle")
    public boolean checkEmployee(String acc_no) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor;
        String query = "select * from " + employee_login + " where ACCOUNT_NO = \"" + acc_no + "\"";
        cursor = database.rawQuery(query, null);
        return cursor.getCount() > 0;
    }

    @SuppressLint("Recycle")
    public boolean checkCustomer(String acc_no) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor;
        String query = "select * from " + customer_login + " where ACCOUNT_NO = \"" + acc_no + "\"";
        cursor = database.rawQuery(query, null);
        return cursor.getCount() <= 0;
    }

    public void addEmployee(String acc_no, String name, String addr, String email, String phone, String password) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("ACCOUNT_NO", acc_no);
        contentValues.put("NAME", name);
        contentValues.put("ADDRESS", addr);
        contentValues.put("EMAIL", email);
        contentValues.put("PHONE", phone);
        contentValues.put("PASSWORD", password);

        database.insert(employee_login, null, contentValues);
    }

    public void addCustomer(String acc_no, String name, String addr, String email, String phone, String password, String balance) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("ACCOUNT_NO", acc_no);
        contentValues.put("NAME", name);
        contentValues.put("ADDRESS", addr);
        contentValues.put("EMAIL", email);
        contentValues.put("PHONE", phone);
        contentValues.put("PASSWORD", password);
        contentValues.put("BALANCE", balance);

        database.insert(customer_login, null, contentValues);
    }

    public JSONObject getEmpAccount(String acc_no, String pwd_ha) {
        SQLiteDatabase database = this.getWritableDatabase();
        JSONObject object = new JSONObject();

        //check for account and return json if found
        Cursor cursor;
        String query = "select * from " + employee_login + " where ACCOUNT_NO = \"" + acc_no + "\"";
        try {
            object.put("status", "initiated");
            cursor = database.rawQuery(query, null);
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                if (cursor.getString(cursor.getColumnIndex("PASSWORD")).equals(pwd_ha)) {
                    object = getJSONObject(cursor);
                    object.put("status", "success");
                } else {
                    object.put("status", "invalid_password");
                }
            } else if (cursor.getCount() > 1) {
                object.put("status", "Account count is " + cursor.getCount());
            } else {
                object.put("status", "no_account");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    public JSONObject getCustAccount(String acc_no, String pwd_ha) {
        SQLiteDatabase database = this.getWritableDatabase();
        JSONObject object = new JSONObject();

        //check for account and return json if found
        Cursor cursor;
        String query = "select * from " + customer_login + " where ACCOUNT_NO = \"" + acc_no + "\"";
        try {
            object.put("status", "initiated");
            cursor = database.rawQuery(query, null);
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                if (cursor.getString(cursor.getColumnIndex("PASSWORD")).equals(pwd_ha)) {
                    object = getJSONObject(cursor);
                    object.put("status", "success");
                } else {
                    object.put("status", "invalid_password");
                }
            } else if (cursor.getCount() > 1) {
                object.put("status", "Account count is " + cursor.getCount());
            } else {
                object.put("status", "no_account");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    private JSONObject getJSONObject(Cursor cursor) {
        JSONObject object = new JSONObject();

        try {
            int column_count = cursor.getColumnCount();
            for (int i = 0; i < column_count; i++) {
                if (cursor.getColumnName(i) != null) {
                    if (cursor.getString(i) != null) {
                        object.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    public String getBalance(String acc_no) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor;
        String query = "select BALANCE from " + customer_login + " where ACCOUNT_NO = \"" + acc_no + "\"";
        cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("BALANCE"));
        } else {
            cursor.close();
            return null;
        }
    }

    public ArrayList<String> getAllAccountNo() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor;
        String query = "select ACCOUNT_NO from " + customer_login;
        cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            ArrayList<String> arrayList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrayList.add(cursor.getString(cursor.getColumnIndex("ACCOUNT_NO")));
                cursor.moveToNext();
            }
            cursor.close();
            return arrayList;
        } else {
            cursor.close();
            return null;
        }
    }

    public ArrayList<JSONObject> getCustTransactions(String acc) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor;
        String query = "select * from " + transaction + " where FROM_ACC = " + acc;
        cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            ArrayList<JSONObject> arrayList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrayList.add(getJSONObject(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return arrayList;
        } else {
            cursor.close();
            return null;
        }
    }

    public ArrayList<JSONObject> getAllTransactions() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor;
        String query = "select * from " + transaction + " ORDER BY TIMESTAMP DESC";
        cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            ArrayList<JSONObject> arrayList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrayList.add(getJSONObject(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return arrayList;
        } else {
            cursor.close();
            return null;
        }
    }
}