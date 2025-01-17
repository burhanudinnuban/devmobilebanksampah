package com.demo.user.banksampah.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.demo.user.banksampah.Services.LoginActivity;

import java.util.HashMap;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;

    // shared pref mode
    protected int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "eRecyclePref";

    private static final String IS_LOGIN = "isLoggedIn";
    private static final String IS_PIN = "isPinInput";

    public static final String KEY_NO_HP = "pref_no_hp";
    public static final String KEY_NAMA = "pref_nama";
    public static final String KEY_POINT = "pref_point";
    public static final String KEY_LATLONG = "pref_latlong";
    public static final String KEY_ALAMAT = "pref_alamat";
    public static final String KEY_EMAIL = "pref_email";
    public static final String KEY_FOTO = "pref_foto";
    public static final String KEY_ID = "pref_id";
    public static final String KEY_PIN = "pref_pin";
    public static final String KEY_ROLE_USER = "pref_role_user";
    public static final String KEY_JAM_OPERASIONAL = "pref_operasional";
    public static final String KEY_BANK = "pref_bank";
    public static final String KEY_BANK_ACCOUNT = "pref_bank_account";
    public static final String KEY_NO_REKENING = "pref_rekening";
    public static final String KEY_NAMA_PEMILIK = "pref_pemilik";
    public static final String KEY_CABANG = "pref_cabang";
    public static final String KEY_TOKEN = "pref_token_firebase";
    public static final String KEY_SALDO = "pref_saldo_nasabah";
    public static final String KEY_NO_SK = "pref_no_sk";
    public static final String KEY_PENERBIT_SK = "pref_penerbit_sk";
    public static final String KEY_ID_NASABAH = "pref_id_nasabah";
    public static final String KEY_SALDO_BANK_SAMPAH = "pref_saldo_bank_sampah";
    public static final String KEY_UNIT_DEFAULT = "pref_bank_unit_default";
    //private static final String KEY_FRAGMENT = "pref_fragment";
    //private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    /*public void createFragmentSession(String fragment){
        editor.putString(KEY_FRAGMENT, fragment);
    }*/

    /*public HashMap<String, String> getFragmentDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_FRAGMENT, pref.getString(KEY_FRAGMENT, null));
        return user;
    }*/

    //Create Login Session
    public void createLoginSession(String no_hp, String nama, String latlong,
                                   String alamat, String email, String foto,
                                   String id, String role_user, String jam, String point, String unitDefault){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NO_HP, no_hp);
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_LATLONG, latlong);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FOTO, foto);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_ROLE_USER, role_user);
        editor.putString(KEY_JAM_OPERASIONAL, jam);
        editor.putString(KEY_SALDO_BANK_SAMPAH, point);
        editor.putString(KEY_UNIT_DEFAULT, unitDefault);
        editor.commit();
    }

    public void createTokenSession(String token){
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void lupaPassword(String phone, String idUser){
        editor.putString(KEY_NO_HP,phone );
        editor.putString(KEY_ID,idUser );
        editor.commit();
    }

    public void updateProfil(String email, String alamat, String jam, String foto, String noSK, String PenerbitSK){
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_JAM_OPERASIONAL,jam );
        editor.putString(KEY_FOTO,foto );
        editor.putString(KEY_NO_SK,noSK );
        editor.putString(KEY_PENERBIT_SK,PenerbitSK );
        editor.commit();
    }

    public void DetailMember(String saldo, String id_member){
        editor.putString(KEY_SALDO, saldo);
        editor.putString(KEY_ID_NASABAH, id_member);
        editor.commit();
    }

    public void checkBankAkun(String bank,String noRekening, String namaPemilik, String cabang, String bankAccount){
        editor.putString(KEY_BANK, bank);
        editor.putString(KEY_NO_REKENING, noRekening);
        editor.putString(KEY_NAMA_PEMILIK, namaPemilik);
        editor.putString(KEY_CABANG, cabang);
        editor.putString(KEY_BANK_ACCOUNT, bankAccount);
        editor.commit();
    }

    public void CheckPin(String no_telepon,String pin){
        editor.putBoolean(IS_PIN, true);
        editor.putString(KEY_NO_HP, no_telepon);
        editor.putString(KEY_PIN, pin);
        editor.commit();
    }


    //Stored Session Data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_NO_HP, pref.getString(KEY_NO_HP, null));
        user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
        user.put(KEY_POINT, pref.getString(KEY_POINT, null));
        user.put(KEY_LATLONG, pref.getString(KEY_LATLONG, null));
        user.put(KEY_ALAMAT, pref.getString(KEY_ALAMAT, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_FOTO, pref.getString(KEY_FOTO, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_ROLE_USER, pref.getString(KEY_ROLE_USER, null));
        user.put(KEY_JAM_OPERASIONAL, pref.getString(KEY_JAM_OPERASIONAL, null));
        user.put(KEY_BANK, pref.getString(KEY_BANK, null));
        user.put(KEY_BANK_ACCOUNT, pref.getString(KEY_BANK_ACCOUNT, null));
        user.put(KEY_CABANG, pref.getString(KEY_CABANG, null));
        user.put(KEY_NO_REKENING, pref.getString(KEY_NO_REKENING, null));
        user.put(KEY_NAMA_PEMILIK, pref.getString(KEY_NAMA_PEMILIK, null));
        user.put(KEY_PIN, pref.getString(KEY_PIN, null));
        user.put(KEY_SALDO, pref.getString(KEY_SALDO, null));
        user.put(KEY_NO_SK, pref.getString(KEY_NO_SK, null));
        user.put(KEY_PENERBIT_SK, pref.getString(KEY_PENERBIT_SK, null));
        user.put(KEY_ID_NASABAH, pref.getString(KEY_ID_NASABAH, null));
        user.put(KEY_SALDO_BANK_SAMPAH, pref.getString(KEY_SALDO_BANK_SAMPAH, null));
        user.put(KEY_UNIT_DEFAULT, pref.getString(KEY_UNIT_DEFAULT, null));
        return user;
    }

    public HashMap<String,String> getTokenDetails(){
        HashMap<String,String>token = new HashMap<>();
        token.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        return token;
    }

    //Do Logout Activity
    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent intent_logout = new Intent(_context, LoginActivity.class);
        intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(intent_logout);
    }

    //Checking for Login
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    //Checking for Pin
    public boolean isPinIn(){
        return pref.getBoolean(IS_PIN, false);
    }



    /*public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
*/
}
