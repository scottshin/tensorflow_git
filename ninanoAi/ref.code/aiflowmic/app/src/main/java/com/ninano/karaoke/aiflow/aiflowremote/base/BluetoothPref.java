package com.ninano.karaoke.aiflow.aiflowremote.base;

import android.content.Context;
import android.content.SharedPreferences;

public class BluetoothPref {
    private Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    public static final String KEY_APP_NAME = "AI_FLOW_MIC_BLUETOOTH";
    public static final String KEY_PAIR_ADDRESS = "KEY_PAIR_ADDRESS";
    public static final String KEY_PAIR_NAME = "KEY_PAIR_NAME";
    public static final String KEY_PAIR_TYPE = "KEY_PAIR_TYPE";
    public static final String KEY_PAIR_UUIDS = "KEY_PAIR_UUIDS";
    public static final String KEY_PAIR_STATE = "KEY_PAIR_STATE";
    public static final String KEY_PAIR_BL_CLASS = "KEY_PAIR_BL_CLASS";




    public static final String KEY_PAIR_DEVICE_OBJECT = "KEY_PAIR_DEVICE_OBJECT";
    public BluetoothPref(Context context){
        this.mContext = context;
        this.pref = mContext.getApplicationContext().getSharedPreferences(KEY_APP_NAME, 0);
        this.editor = pref.edit();
    }

    public void setName(String value){
        editor.putString(KEY_PAIR_NAME, value);
        editor.commit();
    }

    public String getName(){
        return pref.getString(KEY_APP_NAME, "");
    }

    public void setAddress(String value){
        editor.putString(KEY_PAIR_ADDRESS, value);
        editor.commit();
    }

    public String getAddress(){
        return pref.getString(KEY_PAIR_ADDRESS, "");
    }

    public void setType(int value){
        editor.putInt(KEY_PAIR_TYPE, value);
        editor.commit();
    }

    public int getType(){
        return pref.getInt(KEY_PAIR_TYPE, -1);
    }


    public void setUUids(String value){
        editor.putString(KEY_PAIR_UUIDS, value);
        editor.commit();
    }

    public String getUUids(){
        return pref.getString(KEY_PAIR_UUIDS, "");
    }

    public void setState(int value){
        editor.putInt(KEY_PAIR_STATE, value);
        editor.commit();
    }

    public int getState(){
        return pref.getInt(KEY_PAIR_TYPE, -1);
    }

}
