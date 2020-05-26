package com.agromall.agromall.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agromall.agromall.BuildConfig;
import com.agromall.agromall.constant.DbContract;
import com.agromall.agromall.model.Farm;
import com.agromall.agromall.model.Farmer;

import java.util.ArrayList;

/**
 * Created by Wisdom Emenike.
 * Date: 6/17/2017
 * Time: 12:34 AM
 */

@SuppressWarnings("ALL")
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = BuildConfig.VERSION_CODE;
    private static Cursor schoolCursor = null;

    private static final String CREATE_FARMERS_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.FARMERS_TABLE_NAME + "("
            + DbContract.FARMER_UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DbContract.FARMER_FIRSTNAME + " CHAR(100) NOT NULL,"
            + DbContract.FARMER_MIDDLENAME + " CHAR(100) NOT NULL,"
            + DbContract.FARMER_LASTNAME + " CHAR(100) NOT NULL,"
            + DbContract.FARMER_PHONE + " CHAR(30) NOT NULL,"
            + DbContract.FARMER_EMAIL + " CHAR(100) NOT NULL,"
            + DbContract.FARMER_PICTURE + " blob NOT NULL,"
            + DbContract.FARMER_GENDER + " INT NOT NULL,"
            + DbContract.FARMER_ADDRESS + " CHAR(500) NOT NULL);";

    private static final String CREATE_FARMS_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.FARMS_TABLE_NAME + "("
            + DbContract.FARM_UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DbContract.FARM_OWNER_ID + " INT NOT NULL,"
            + DbContract.FARM_NAME + " CHAR(100) NOT NULL,"
            + DbContract.FARM_LOCATION + " CHAR(100) NOT NULL,"
            + DbContract.FARM_COORDINATES + " CHAR(100) NOT NULL);";

    private static final String DROP_FARMERS_TABLE = "DROP TABLE IF EXISTS " + DbContract.FARMERS_TABLE_NAME;
    private static final String DROP_FARMS_TABLE = "DROP TABLE IF EXISTS " + DbContract.FARMS_TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    public void dropTables(SQLiteDatabase db) {
        db.execSQL(DROP_FARMERS_TABLE);
        db.execSQL(DROP_FARMS_TABLE);
    }

    public void createTables(SQLiteDatabase db) {
        db.execSQL(CREATE_FARMERS_TABLE);
        db.execSQL(CREATE_FARMS_TABLE);
    }

    /**
     * Store farmer details in SQLite Database
     * @param farmer
     * @return
     */
    public boolean saveFarmer(Farmer farmer) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FARMER_FIRSTNAME, farmer.getFirstName());
        contentValues.put(DbContract.FARMER_MIDDLENAME, farmer.getMiddleName());
        contentValues.put(DbContract.FARMER_LASTNAME, farmer.getLastName());
        contentValues.put(DbContract.FARMER_EMAIL, farmer.getEmail());
        contentValues.put(DbContract.FARMER_PHONE, farmer.getPhone());
        contentValues.put(DbContract.FARMER_PICTURE, farmer.getPicture());
        contentValues.put(DbContract.FARMER_GENDER, farmer.getGender());
        contentValues.put(DbContract.FARMER_ADDRESS, farmer.getAddress());

        SQLiteDatabase database = this.getReadableDatabase();
        boolean result = database.insert(DbContract.FARMERS_TABLE_NAME, null, contentValues) > 0;
        database.close();
        return result;
    }

    /**
     * Update farmer info in SQLite Database
     * @param farmer
     * @return
     */
    public boolean updateFarmer(Farmer farmer) {

        if (farmer.getFarmerID() <= 0) return false;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FARMER_FIRSTNAME, farmer.getFirstName());
        contentValues.put(DbContract.FARMER_MIDDLENAME, farmer.getMiddleName());
        contentValues.put(DbContract.FARMER_LASTNAME, farmer.getLastName());
        contentValues.put(DbContract.FARMER_EMAIL, farmer.getEmail());
        contentValues.put(DbContract.FARMER_PHONE, farmer.getPhone());
        contentValues.put(DbContract.FARMER_PICTURE, farmer.getPicture());
        contentValues.put(DbContract.FARMER_GENDER, farmer.getGender());
        contentValues.put(DbContract.FARMER_ADDRESS, farmer.getAddress());

        if (contentValues.size() <= 0) return false;

        String selection = DbContract.FARMER_UID + " = ? ";
        String[] args = {String.valueOf(farmer.getFarmerID())};
        int affectedRows = database.update(DbContract.FARMERS_TABLE_NAME, contentValues, selection, args);
        database.close();
        return affectedRows > 0;
    }

    /**
     * Get farmer info from SQLite Database
     * @param farmerID
     * @return
     */
    public Farmer getFarmer(int farmerID) {

        String[] projection = {
                DbContract.FARMER_UID,
                DbContract.FARMER_FIRSTNAME,
                DbContract.FARMER_MIDDLENAME,
                DbContract.FARMER_LASTNAME,
                DbContract.FARMER_EMAIL,
                DbContract.FARMER_PHONE,
                DbContract.FARMER_GENDER,
                DbContract.FARMER_PICTURE,
                DbContract.FARMER_ADDRESS
        };

        SQLiteDatabase database = this.getReadableDatabase();
        String selection = DbContract.FARMER_UID + " =? ";
        String[] args = {String.valueOf(farmerID)};
        Cursor cursor = (database.query(DbContract.FARMERS_TABLE_NAME, projection, selection, args,
                null, null, null));

        Farmer farmer = new Farmer();
        if (cursor != null && cursor.moveToFirst()) {
            farmer.setFarmerID(cursor.getInt(cursor.getColumnIndex(DbContract.FARMER_UID)));
            farmer.setFirstName(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_FIRSTNAME)));
            farmer.setMiddleName(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_MIDDLENAME)));
            farmer.setLastName(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_LASTNAME)));
            farmer.setEmail(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_EMAIL)));
            farmer.setPhone(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_PHONE)));
            farmer.setGender(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_GENDER)));
            farmer.setPicture(cursor.getBlob(cursor.getColumnIndex(DbContract.FARMER_PICTURE)));
            farmer.setAddress(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_ADDRESS)));
        }

        cursor.close();
        database.close();
        return farmer;
    }


    /**
     * Get all farmer's info from SQLite Database
     * @return
     */
    public ArrayList<Farmer> getFarmers() {
        return getFarmers(0);
    }

    /**
     * Get all farmer's info from SQLite Database
     * @param limit
     * @return
     */
    public ArrayList<Farmer> getFarmers(int limit) {

        String[] projection = {
                DbContract.FARMER_UID,
                DbContract.FARMER_FIRSTNAME,
                DbContract.FARMER_MIDDLENAME,
                DbContract.FARMER_LASTNAME,
                DbContract.FARMER_EMAIL,
                DbContract.FARMER_PHONE,
                DbContract.FARMER_GENDER,
                DbContract.FARMER_PICTURE,
                DbContract.FARMER_ADDRESS
        };

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;

        if (limit > 0) {

            cursor = (database.query(DbContract.FARMERS_TABLE_NAME, projection, null, null,
                    null, null, "upper(" + DbContract.FARMER_UID + ") DESC", String.valueOf(limit)));

        } else {

            cursor = (database.query(DbContract.FARMERS_TABLE_NAME, projection, null, null,
                    null, null, "upper(" + DbContract.FARMER_FIRSTNAME + ") ASC"));

        }

        ArrayList<Farmer> farmers = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Farmer farmer = new Farmer();
                farmer.setFarmerID(cursor.getInt(cursor.getColumnIndex(DbContract.FARMER_UID)));
                farmer.setFirstName(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_FIRSTNAME)));
                farmer.setMiddleName(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_MIDDLENAME)));
                farmer.setLastName(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_LASTNAME)));
                farmer.setEmail(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_EMAIL)));
                farmer.setPhone(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_PHONE)));
                farmer.setGender(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_GENDER)));
                farmer.setPicture(cursor.getBlob(cursor.getColumnIndex(DbContract.FARMER_PICTURE)));
                farmer.setAddress(cursor.getString(cursor.getColumnIndex(DbContract.FARMER_ADDRESS)));
                farmers.add(farmer);
            }
            cursor.close();
        }

        database.close();

        return farmers;
    }

    /**
     * Check if farmer email exist in SQLite Database
     * @param email
     * @return
     */
    public boolean farmerEmailExist(String email) {
        return farmerEmailExist(email, 0);
    }

    /**
     * Check if farmer email exist in SQLite Database
     * @param email
     * @param ignoreFarmerID
     * @return
     */
    public boolean farmerEmailExist(String email, int ignoreFarmerID) {
        SQLiteDatabase database = this.getReadableDatabase();
        int count = 0;
        Cursor cursor;

        if (ignoreFarmerID > 0) {

            cursor = database.rawQuery("SELECT * FROM " + DbContract.FARMERS_TABLE_NAME + " WHERE "
                    + DbContract.FARMER_EMAIL + " =? AND " + DbContract.FARMER_UID + " !=?", new String[]{email, String.valueOf(ignoreFarmerID)});
        } else {
            cursor = database.rawQuery("SELECT * FROM " + DbContract.FARMERS_TABLE_NAME + " WHERE "
                    + DbContract.FARMER_EMAIL + " =?", new String[]{email});
        }

        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
            if (count > 0) return true;
        }
        return false;
    }

    /**
     * Check if farmer phone exist in SQLite Database
     * @param phone
     * @return
     */
    public boolean farmerPhoneExist(String phone) {
        return farmerPhoneExist(phone, 0);
    }

    /**
     * Check if farmer phone exist in SQLite Database
     * @param phone
     * @param ignoreFarmerID
     * @return
     */
    public boolean farmerPhoneExist(String phone, int ignoreFarmerID) {

        SQLiteDatabase database = this.getReadableDatabase();
        int count = 0;
        Cursor cursor;

        if (ignoreFarmerID > 0) {

            cursor = database.rawQuery("SELECT * FROM " + DbContract.FARMERS_TABLE_NAME + " WHERE "
                    + DbContract.FARMER_PHONE + " =? AND " + DbContract.FARMER_UID + " !=?", new String[]{phone, String.valueOf(ignoreFarmerID)});
        } else {

            cursor = database.rawQuery("SELECT * FROM " + DbContract.FARMERS_TABLE_NAME + " WHERE "
                    + DbContract.FARMER_PHONE + " =?", new String[]{phone});
        }

        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
            if (count > 0) return true;
        }
        return false;
    }


    /**
     * Count farmers in SQLite Database
     * @return
     */
    public int countFarmers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContract.FARMERS_TABLE_NAME, null);
        return cursor.getCount();
    }


    /**
     * Delete farmer from SQLite Database
     * @param farmerID
     * @return
     */
    public boolean deleteFarmer(int farmerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete(DbContract.FARMERS_TABLE_NAME,
                DbContract.FARMER_UID + " = ?", new String[]{String.valueOf(farmerID)});
        db.close();
        return affectedRows > 0 ? (deleteFarmerFarms(farmerID) ? true : true) : false;
    }


    /**
     * Delete all farmer farms from SQLite Database
     * @param farmerID
     * @return
     */
    public boolean deleteFarmerFarms(int farmerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete(DbContract.FARMS_TABLE_NAME,
                DbContract.FARM_OWNER_ID + " = ?", new String[]{String.valueOf(farmerID)});
        db.close();
        return affectedRows > 0;
    }


    /**
     * Store farm details in SQLite Database
     * @param farmer
     * @return
     */
    public boolean saveFarm(Farm farm) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FARM_OWNER_ID, farm.getFarmOwnerID());
        contentValues.put(DbContract.FARM_NAME, farm.getName());
        contentValues.put(DbContract.FARM_LOCATION, farm.getLocation());
        contentValues.put(DbContract.FARM_COORDINATES, farm.getCoordinates());

        SQLiteDatabase database = this.getReadableDatabase();
        boolean result = database.insert(DbContract.FARMS_TABLE_NAME, null, contentValues) > 0;
        database.close();
        return result;
    }

    /**
     * Update farm info in SQLite Database
     * @param farm
     * @return
     */
    public boolean updateFarm(Farm farm) {

        if (farm.getFarmID() <= 0 || farm.getFarmOwnerID() <= 0) return false;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.FARM_OWNER_ID, farm.getFarmOwnerID());
        contentValues.put(DbContract.FARM_NAME, farm.getName());
        contentValues.put(DbContract.FARM_LOCATION, farm.getLocation());
        contentValues.put(DbContract.FARM_COORDINATES, farm.getCoordinates());

        if (contentValues.size() <= 0) return false;

        String selection = DbContract.FARM_UID + " = ? ";
        String[] args = {String.valueOf(farm.getFarmID())};
        int affectedRows = database.update(DbContract.FARMS_TABLE_NAME, contentValues, selection, args);
        database.close();
        return affectedRows > 0;
    }

    /**
     * Get farm info from SQLite Database
     * @param farmerID
     * @return
     */
    public Farm getFarm(int farmID, int farmerID) {

        String[] projection = {
                DbContract.FARM_UID,
                DbContract.FARM_OWNER_ID,
                DbContract.FARM_NAME,
                DbContract.FARM_LOCATION,
                DbContract.FARM_COORDINATES
        };

        SQLiteDatabase database = this.getReadableDatabase();
        String selection = DbContract.FARM_UID + " =? AND " + DbContract.FARM_OWNER_ID + " =? ";
        String[] args = {String.valueOf(farmID), String.valueOf(farmerID)};
        Cursor cursor = (database.query(DbContract.FARMS_TABLE_NAME, projection, selection, args,
                null, null, null));

        Farm farm = new Farm();
        if (cursor != null && cursor.moveToFirst()) {
            farm.setFarmID(cursor.getInt(cursor.getColumnIndex(DbContract.FARM_UID)));
            farm.setFarmOwnerID(cursor.getInt(cursor.getColumnIndex(DbContract.FARM_OWNER_ID)));
            farm.setName(cursor.getString(cursor.getColumnIndex(DbContract.FARM_NAME)));
            farm.setLocation(cursor.getString(cursor.getColumnIndex(DbContract.FARM_LOCATION)));
            farm.setCoordinates(cursor.getString(cursor.getColumnIndex(DbContract.FARM_COORDINATES)));
        }

        cursor.close();
        database.close();
        return farm;
    }

    /**
     * Get all farms owned by a farmer info from SQLite Database
     * @param limit
     * @return
     */
    public ArrayList<Farm> getFarms(int limit) {
        return getFarms(0, limit);
    }

    /**
     * Get all farms owned by a farmer info from SQLite Database
     * @param farmerID
     * @param limit
     * @return
     */
    public ArrayList<Farm> getFarms(int farmerID, int limit) {

        String[] projection = {
                DbContract.FARM_UID,
                DbContract.FARM_OWNER_ID,
                DbContract.FARM_NAME,
                DbContract.FARM_LOCATION,
                DbContract.FARM_COORDINATES
        };

        SQLiteDatabase database = this.getReadableDatabase();

        String selection = DbContract.FARM_OWNER_ID + " =? ";
        String[] args = {String.valueOf(farmerID)};
        Cursor cursor = (database.query(DbContract.FARMS_TABLE_NAME, projection, farmerID > 0 ? selection : null, farmerID > 0 ? args : null,
                null, null, "upper(" + (limit > 0 ? DbContract.FARM_UID : DbContract.FARM_NAME) +
                        (limit > 0 ? ") DESC" : ") ASC"), limit > 0 ? String.valueOf(limit) : null));

        ArrayList<Farm> farms = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Farm farm = new Farm();
                farm.setFarmID(cursor.getInt(cursor.getColumnIndex(DbContract.FARM_UID)));
                farm.setFarmOwnerID(cursor.getInt(cursor.getColumnIndex(DbContract.FARM_OWNER_ID)));
                farm.setName(cursor.getString(cursor.getColumnIndex(DbContract.FARM_NAME)));
                farm.setLocation(cursor.getString(cursor.getColumnIndex(DbContract.FARM_LOCATION)));
                farm.setCoordinates(cursor.getString(cursor.getColumnIndex(DbContract.FARM_COORDINATES)));
                farms.add(farm);
            }
            cursor.close();
        }

        database.close();

        return farms;
    }


    /**
     * Count farms in SQLite Database
     * @return
     */
    public int countFarms() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContract.FARMS_TABLE_NAME, null);
        return cursor.getCount();
    }


    /**
     * Delete farm from SQLite Database
     * @param farmID
     * @return
     */
    public boolean deleteFarm(int farmID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete(DbContract.FARMS_TABLE_NAME,
                DbContract.FARM_UID + " = ?", new String[]{String.valueOf(farmID)});
        db.close();
        return affectedRows > 0;
    }



    /**
     * Delete all data in SQLite Database when logging out
     */
    public void dropAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        dropTables(db);
        db.close();
    }

}
