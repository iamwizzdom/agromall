package com.agromall.agromall.constant;

/**
 * Created by Wisdom Emenike.
 * Date: 6/21/2017
 * Time: 12:33 AM
 */

@SuppressWarnings("ALL")
public class DbContract {
    /**
     * db name variables
     */
    public static final String DATABASE_NAME = "agromallDB";

    /**
     * farmer db variables
     */
    public static final String FARMERS_TABLE_NAME = "farmers";
    public static final String FARMER_UID = "_id";
    public static final String FARMER_FIRSTNAME = "firstName";
    public static final String FARMER_MIDDLENAME = "middleName";
    public static final String FARMER_LASTNAME = "lastName";
    public static final String FARMER_EMAIL = "email";
    public static final String FARMER_PHONE = "phone";
    public static final String FARMER_PICTURE = "picture";
    public static final String FARMER_GENDER = "gender";
    public static final String FARMER_ADDRESS = "address";

    /**
     * farm db variables
     */
    public static final String FARMS_TABLE_NAME = "farms";
    public static final String FARM_UID = "_id";
    public static final String FARM_OWNER_ID = "farmer_id";
    public static final String FARM_NAME = "name";
    public static final String FARM_LOCATION = "location";
    public static final String FARM_COORDINATES = "coordinates";

}
