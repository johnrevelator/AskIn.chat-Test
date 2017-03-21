package com.unated.askincht_beta.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.MyShopResponse;


public class SharedStore {

    private static SharedStore mSharedStore;

    public SharedStore() {

    }

    public static SharedStore getInstance() {
        if (mSharedStore == null) {
            mSharedStore = new SharedStore();
            return mSharedStore;
        } else {
            return mSharedStore;
        }
    }

    private SharedPreferences getAppPreferences() {
        SharedPreferences sharedPreferences = AppMain.getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public void setMyShop(MyShopResponse.Data.MyShop shop) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_SHOP, new Gson().toJson(shop).toString());
        editor.apply();
    }
    public void setWas(boolean was) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putBoolean("was",was);
        editor.apply();
    }
    public void setShop(boolean isShop) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putBoolean("is_shop",isShop);
        editor.apply();
    }
    public void setMyLat(double lat) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.LATITUDE, String.valueOf(lat));
        editor.apply();
    }
    public void setPhone(String phone) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PHONE,phone);
        editor.apply();
    }
    public void setToken(String token) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.TOKEN,token);
        editor.apply();
    }
    public void setMyLng(double lng) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.LONGITUDE, String.valueOf(lng));
        editor.apply();
    }
    public MyShopResponse.Data.MyShop getMyShop() {
        return new Gson().fromJson(getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_SHOP, ""), MyShopResponse.Data.MyShop.class);
    }

    public void clearMyRequests() {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putInt(Constants.PREFERENCES.PREFERENCES_MY_NOTIFICATIONS, 0);
        editor.apply();
    }

    public void clearUserRequests() {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putInt(Constants.PREFERENCES.PREFERENCES_NOTIFICATIONS, 0);
        editor.apply();
    }

    public void setNewMyRequests(int count) {
        count += getAppPreferences().getInt(Constants.PREFERENCES.PREFERENCES_MY_NOTIFICATIONS, 0);
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putInt(Constants.PREFERENCES.PREFERENCES_MY_NOTIFICATIONS, count);
        editor.apply();
    }

    public int getMyRequestsCount() {
        return getAppPreferences().getInt(Constants.PREFERENCES.PREFERENCES_MY_NOTIFICATIONS, 0);
    }

    public String  getMyLat() {
        return getAppPreferences().getString(Constants.PREFERENCES.LATITUDE,"");
    }
    public String  getPhone() {
        return getAppPreferences().getString(Constants.PREFERENCES.PHONE,"");
    }
    public String  getMyLng() {
        return getAppPreferences().getString(Constants.PREFERENCES.LONGITUDE,"");
    }
    public void setNewUserRequests(int count) {
        count += getAppPreferences().getInt(Constants.PREFERENCES.PREFERENCES_NOTIFICATIONS, 0);
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putInt(Constants.PREFERENCES.PREFERENCES_NOTIFICATIONS, count);
        editor.apply();
    }

    public int getUserRequestsCount() {
        return getAppPreferences().getInt(Constants.PREFERENCES.PREFERENCES_NOTIFICATIONS, 0);
    }

    public void setDeviceAuth(boolean state) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putBoolean(Constants.PREFERENCES.PREFERENCES_DEVICE_AUTH, state);
        editor.apply();
    }

    public boolean isDeviceAuth() {
        return getAppPreferences().getBoolean(Constants.PREFERENCES.PREFERENCES_DEVICE_AUTH, true);
    }

    public void setUUID(String UUID) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_DEVICE_ID, UUID);
        editor.apply();
    }
    public void setBalance(String balance) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.BALANCE, balance);
        editor.apply();
    }

    public String getUUID() {
        return getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_DEVICE_ID, "");
    } public String getBalance() {
        return getAppPreferences().getString(Constants.PREFERENCES.BALANCE, "");
    }

    public void setSID(String sid) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_SID, sid);
        editor.apply();
    }

    public String getSID() {
        return getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_SID, "");
    }
    public String getToken() {
        return getAppPreferences().getString(Constants.PREFERENCES.TOKEN, "");
    }

    public void setUserId(String uId) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_UID, uId);
        editor.apply();
    }

    public String getUserId() {
        return getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_UID, "0");
    }
    public boolean getWas() {
        return getAppPreferences().getBoolean("was", false);
    }

    public boolean getShop() {
        return getAppPreferences().getBoolean("is_shop", false);
    }
    public void setShopId(String uId) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_SPID, uId);
        editor.apply();
    }

    public String getShopId() {
        return getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_SPID, "0");
    }

    public void setLastMessageId(String id) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_LAST_MSG_ID, id);
        editor.apply();
    }

    public String getLastMessageId() {
        return getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_LAST_MSG_ID, "0");
    }

    public void setLastRequestId(String id) {
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(Constants.PREFERENCES.PREFERENCES_LAST_REQ_ID, id);
        editor.apply();
    }

    public String getLastRequestId() {
        return getAppPreferences().getString(Constants.PREFERENCES.PREFERENCES_LAST_REQ_ID, "0");
    }
}
