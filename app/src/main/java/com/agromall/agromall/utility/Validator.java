package com.agromall.agromall.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.text.TextUtils;
import android.util.Patterns;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;

/**
 * Created by Wisdom Emenike.
 * Date: 8/3/2018
 * Time: 12:34 AM
 */

public class Validator {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        int phoneLength = phone.length();
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches() && phoneLength >= 7 && phoneLength <= 15;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = null;
        if (connectivityManager != null) networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return (networkCapabilities != null && networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET));
    }

}