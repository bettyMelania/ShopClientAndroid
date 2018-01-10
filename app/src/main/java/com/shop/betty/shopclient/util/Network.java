package com.shop.betty.shopclient.util;

/**
 * Created by Betty on 1/10/2018.
 */


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Network {

    public static boolean isNetworkConnected(Context _context){
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}