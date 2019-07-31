package com.demo.user.banksampah;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anthon on 9/6/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RMS.db";
    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_COLUMN_WS_ADDR = "ws_addr";
    public static final String SETTINGS_COLUMN_WS_USER = "ws_user";
    public static final String SETTINGS_COLUMN_WS_PASS = "ws_pass";
    public static final String SETTINGS_COLUMN_ORG_ID = "org_id";
    public static final String SETTINGS_COLUMN_MERCHANT_ID = "merchant_id";
    public static final String SETTINGS_COLUMN_OUTLET_ID = "outlet_id";
    public static final String SETTINGS_COLUMN_TERMINAL_ID = "terminal_id";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        /*db.execSQL(
                "create table settings " +
                        "(id integer primary key, ws_addr text,ws_user text,ws_pass text, org_id text,merchant_id text,outlet_id text,terminal_id text,country_code text)"
        );

        db.execSQL(
                "create table tx_code " +
                        "(id integer primary key, code_name text,code_value text, code_type text)"
        );*/



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(db);
    }

    public boolean insertSettings  (HashMap<String,String> hash_gen)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] field_name = {"ws_addr", "ws_user", "ws_pass", "org_id", "merchant_id", "outlet_id", "terminal_id", "lang_code"};
        for (int k = 0; k < field_name.length; k++) {
            contentValues.put(field_name[k], hash_gen.get(field_name[k]));
        }
        db.insert("settings", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from settings where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SETTINGS_TABLE_NAME);
        return numRows;
    }
    public boolean updateSetting(HashMap<String, String> hash_gen){
        int total_rows = numberOfRows();
        if(total_rows == 0){
            insertSettings(hash_gen);
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            String[] field_name = {"ws_addr", "ws_user", "ws_pass", "org_id", "merchant_id", "outlet_id", "terminal_id", "lang_code"};
            for (int k = 0; k < field_name.length; k++) {
                contentValues.put(field_name[k], hash_gen.get(field_name[k]));
            }
            db.update("settings", contentValues, "id = ? ", new String[]{Integer.toString(1)});
        }
        return true;


    }


    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("settings",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllCotacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from settings", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(SETTINGS_COLUMN_WS_ADDR)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean insertCode  (String code_name, String code_value, String code_type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code_name", code_name);
        contentValues.put("code_value", code_value);
        contentValues.put("code_type", code_type);
        db.insert("tx_code", null, contentValues);
        return true;
    }

    public int deleteCode ()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("tx_code",null,null);
    }

//    public ArrayList<HashMap<String, String>> getCode(int var_type)
    public HashMap<String,String> getCode(int var_type)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<>();
        HashMap<String, String> txcode;
        txcode = new HashMap<String, String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql_str = "select * from tx_code where code_type ='" + var_type + "'";

        Cursor res = db.rawQuery(sql_str, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){

            //txcode.put("code_name",res.getString(res.getColumnIndex("code_name")));
            //txcode.put("code_value",res.getString(res.getColumnIndex("code_value")));
            txcode.put(res.getString(res.getColumnIndex("code_value")),res.getString(res.getColumnIndex("code_name")));

            array_list.add(txcode);
            res.moveToNext();
        }
        //return array_list;
        return txcode;
    }

    public int deleteOutlet ()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("outlets",null,null);
    }

    public boolean insertOutlet  (Map<String,String> map_outlet)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] field_name = {"org_id","merchant_id","terminal_id","outlet_name","outlet_code","outlet_type","address1","address2","address3","city","postal_code","state","country","contact_person","work_phone","cell_phone","fax_phone","email","support_no"};
        for(int k=0;k<field_name.length;k++){
            contentValues.put(field_name[k], map_outlet.get(field_name[k]));
        }
        db.insert("outlets", null, contentValues);
        return true;
    }

    public HashMap<String,String> getOutletInfo()
    {

        HashMap<String, String> hash_gen;
        hash_gen = new HashMap<String, String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] field_name = {"terminal_id","outlet_name","address1","address2","address3","city","postal_code","state","country","contact_person","work_phone","cell_phone","fax_phone","support_no"};
        String sql_str = "select * from outlets ";

        Cursor res = db.rawQuery(sql_str, null);
        if(res.moveToFirst()){
            for(int k=0;k<field_name.length;k++){
                hash_gen.put(field_name[k], res.getString(res.getColumnIndex(field_name[k])));
            }
        }else{
            for(int k=0;k<field_name.length;k++){
                hash_gen.put(field_name[k], "");
            }

        }

        //return array_list;
        return hash_gen;
    }

    public int deleteSysMsg ()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("ws_message",null,null);
    }

    public boolean insertSysMsg  (Map<String,String> map_outlet)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] field_name = {"id","short_message","gbr","idn","chn","other01","other02","other03"};
        for(int k=0;k<field_name.length;k++){
            contentValues.put(field_name[k], map_outlet.get(field_name[k]));
        }
        db.insert("ws_message", null, contentValues);
        return true;
    }
    public HashMap<String,String> getSysMsg(String var_country_id)
    {

        HashMap<String, String> sysMsg = new HashMap<String, String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql_str = "select short_message, " + var_country_id +  " from ws_message";

        Cursor res = db.rawQuery(sql_str, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){

            //txcode.put("code_name",res.getString(res.getColumnIndex("code_name")));
            //txcode.put("code_value",res.getString(res.getColumnIndex("code_value")));
            sysMsg.put(res.getString(res.getColumnIndex("short_message")),res.getString(res.getColumnIndex(var_country_id)));

            res.moveToNext();
        }
        //return array_list;
        return sysMsg;
    }
    public int deleteUser ()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("pos_user",null,null);
    }

    public boolean userAuthentication(String user_id, String user_password)
    {
        boolean result=false;
        SQLiteDatabase db = this.getReadableDatabase();

        String sql_str = String.format("select * from pos_user where user_id ='%s'"+
                " and user_password = '%s'",user_id,MD5(user_password));

        Cursor res = db.rawQuery(sql_str, null);
        if(res.moveToFirst()){
            result = true;
        }
        //return array_list;
        return result;
    }



    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public int getTotalRowsSysMsg(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "ws_message");
        return numRows;
    }


    public boolean insertUser  (Map<String,String> map_gen)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] field_name = {"user_id","user_password","user_name"};
        for(int k=0;k<field_name.length;k++){
            contentValues.put(field_name[k], map_gen.get(field_name[k]));
        }
        db.insert("pos_user", null, contentValues);
        return true;
    }



    public void resetTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS settings");
        }catch (Throwable T){
            T.printStackTrace();
        }

        try {
            db.execSQL("DROP TABLE IF EXISTS ws_message");
        }catch (Throwable T){
            T.printStackTrace();
        }

        try {
            db.execSQL("DROP TABLE IF EXISTS tx_code");
        }catch (Throwable T){
            T.printStackTrace();
        }
        try {
            db.execSQL("DROP TABLE IF EXISTS outlets");
        }catch (Throwable T){
            T.printStackTrace();
        }




    }

}
