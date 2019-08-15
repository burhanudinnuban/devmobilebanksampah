package com.demo.user.banksampah.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.demo.user.banksampah.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anthon on 9/9/2016.
 */

public class RestProcess {
    private HashMap<String,String> gbl_sysMsg;
    public String extractJson(String response, String activity_str, HashMap<String, String> sysMsg){
        String return_value="";
        String resp_card_number,resp_amount,resp_pts_earned,resp_tx_id,resp_var_message;
        String resp_date;
        JSONObject obj = null;
        HashMap<String,String> hash_respond = new HashMap<String,String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf_time =  new SimpleDateFormat("HH:mm:ss");
        String curr_date = sdf.format(new Date());
        String curr_time = sdf_time.format(new Date());
        gbl_sysMsg = sysMsg;
        try {
            obj = new JSONObject(response);
            //Log.d("My App", obj.toString());
            switch (activity_str){
                case "checkpoint":
                    resp_card_number = obj.getString("card_number");
                    String resp_full_name = obj.getString("full_name");
                    String resp_new_ic_no = obj.getString("new_ic_no");
                    String resp_member_status = obj.getString("member_status");
                    String resp_point_balance = obj.getString("point_balance");
                    String resp_var_result = obj.getString("var_result");
                    resp_var_message = obj.getString("var_message");
                    return_value = String.format(
                            translateSysMsg("FN_DATE")+":%s\n"+
                                    translateSysMsg("FN_TIME")+":%s\n"+
                                    translateSysMsg("FN_FULL_NAME")+":%s\n"+
                                    translateSysMsg("FN_CARD_NO")+":%s\n" +
                                    translateSysMsg("FN_PTS_BAL")+":%s\n" +
                                    translateSysMsg("FN_MBR_IC")+":%s\n" +
                                    translateSysMsg("FN_MBR_STATUS")+":%s\n"
                            ,curr_date,curr_time,resp_full_name, resp_card_number,resp_point_balance,resp_new_ic_no,resp_member_status);
                    break;
                case "issuepts":
                    //return_value = response;
                    resp_card_number = obj.getString("var_card_number");
                    resp_amount = obj.getString("var_amount");
                    resp_pts_earned = obj.getString("var_point_earned");
                    resp_tx_id = obj.getString("var_tx_id");
                    resp_var_message = obj.getString("var_result_message");
                    return_value = String.format(
                            translateSysMsg("FN_DATE")+":%s\n"+
                                    translateSysMsg("FN_TIME")+":%s\n"+
                                    translateSysMsg("FN_TX_ID")+":%s\n"+
                                    translateSysMsg("FN_CARD_NO")+":%s\n" +
                                    translateSysMsg("FN_AMT")+":%s\n" +
                                    translateSysMsg("FN_PTS_EARNED")+":%s\n"+
                                    translateSysMsg("FN_MESSAGE")+":%s\n"
                            ,curr_date,curr_time,resp_tx_id,resp_card_number,resp_amount,resp_pts_earned,translateSysMsg(resp_var_message)
                    );

                    break;
                case "downloadcode":
                    return_value = response;

                    break;
                case "downloadoutlet":
                    return_value = response;
                    break;
                case "downloaduser":
                    return_value = response;
                    break;

                case "downloadsysmsg":
                    return_value = response;

                    break;
                case "txlist":
                    return_value = response;

                    break;
                case "redeempts":
                    //return_value = response;

                    resp_card_number = obj.getString("var_card_number");
                    resp_amount = obj.getString("var_amount");
                    resp_pts_earned = obj.getString("var_point_balance");
                    resp_tx_id = obj.getString("var_tx_id");
                    resp_var_message = obj.getString("var_result_message");

                    return_value = String.format(
                            translateSysMsg("FN_DATE")+":%s\n"+
                                    translateSysMsg("FN_TIME")+":%s\n"+
                                    translateSysMsg("FN_TX_ID")+":%s\n"+
                                    translateSysMsg("FN_CARD_NO")+":%s\n" +
                                    translateSysMsg("FN_REDEEM_PTS")+":%s\n" +
                                    translateSysMsg("FN_PTS_BAL")+":%s\n"+
                                    translateSysMsg("FN_MESSAGE")+":%s\n"
                            ,curr_date,curr_time,
                            resp_tx_id,resp_card_number,
                            resp_amount,resp_pts_earned,
                            translateSysMsg(resp_var_message)
                    );


                    break;
            }



        } catch (JSONException e) {
            e.printStackTrace();
            return_value = "ERROR!";
        }

        return return_value;

    }
    public HashMap apiSetting(DBHelper mydb){
        HashMap<String, String> apiData = new HashMap<String, String>();


        Cursor rs = mydb.getData(1);
        if(rs.moveToFirst()) {
            apiData.put("str_ws_addr",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_WS_ADDR)));
            apiData.put("str_ws_user",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_WS_USER)));
            apiData.put("str_ws_pass",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_WS_PASS)));
            apiData.put("str_org_id",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_ORG_ID)));
            apiData.put("str_merchant_id",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_MERCHANT_ID)));
            apiData.put("str_outlet_id",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_OUTLET_ID)));
            apiData.put("str_terminal_id",rs.getString(rs.getColumnIndex(DBHelper.SETTINGS_COLUMN_TERMINAL_ID)));
            apiData.put("str_lang_code",rs.getString(rs.getColumnIndex("lang_code")));
        } else{
            apiData.put("str_ws_addr","https://dev-erpnext.pracicointiutama.id/api/");
            apiData.put("str_ws_user","");
            apiData.put("str_ws_pass","");
            apiData.put("str_org_id","");
            apiData.put("str_merchant_id","");
            apiData.put("str_outlet_id","");
            apiData.put("str_terminal_id","");
            apiData.put("str_lang_code","gbr");
        }


        return apiData;
    }

    private String translateSysMsg(String value){
        String return_str=null;
        return_str = value;

        for (Map.Entry<String,String> entry : gbl_sysMsg.entrySet()) {
            if(value.equals(entry.getKey())){
                return_str = entry.getValue();
                break;
            }
            // do stuff
        }
        return return_str;
    }
    public String translateSysMsg2(String value, HashMap<String,String> hash_gen){
        String return_str=null;
        return_str = value;

        for (Map.Entry<String,String> entry : hash_gen.entrySet()) {
            if(value.equals(entry.getKey())){
                return_str = entry.getValue();
                break;
            }
            // do stuff
        }
        return return_str;
    }

    public HashMap apiSettingLocal(){
        HashMap<String, String> apiData = new HashMap<String, String>();
/*            apiData.put("str_ws_addr","http://apps.aim-net.com.my:88/restserver");
            apiData.put("str_ws_user", "admin");
            apiData.put("str_ws_pass","1234");
            apiData.put("str_org_id","2228");*/
        //apiData.put("str_ws_addr","http://wsstagging.locard-gift.com");
        //apiData.put("str_ws_addr","https://ws.locard.co.id");
        //apiData.put("str_ws_addr","https://wsstagging.locard.co.id");
        apiData.put("str_ws_addr","https://dev-erpnext.pracicointiutama.id/api");

        apiData.put("str_ws_user", "adminwebapi");
        apiData.put("str_ws_pass","rmsweb4p1!");
        apiData.put("str_org_id","2231");
        return apiData;
    }

    public HashMap apiErecycle(){
        HashMap<String,String > apiErecyle = new HashMap<>();
        apiErecyle.put("str_url_main", "https://dev-lestari.multiinti.io");
        apiErecyle.put("str_url_address", "https://dev-lestari.multiinti.io/api/method/digitalwastev2.bsh_bankapi");

        //API Login
        apiErecyle.put("str_api_login", ".login");
        apiErecyle.put("str_api_change_password", ".change_pass");

        //API Register
        apiErecyle.put("str_api_check_phonenumber", ".check_phonenumber");
        apiErecyle.put("str_api_send_otp", ".send_otp");
        apiErecyle.put("str_api_register", ".register");

        //API Upload Images
        apiErecyle.put("str_api_upload_image", ".upload_image_token");

        //API Validate OTP
        apiErecyle.put("str_api_validate_otp", ".validate_otp");

        //Token Authorization
        apiErecyle.put("str_header", "Authorization");
        apiErecyle.put("str_token_value", "token a5f866b606775ce:9ce154a207a7716");

        //Volley Setting
        apiErecyle.put("str_json_obj", "json_obj_req");


        //List Member
        apiErecyle.put("str_api_list_member",".list_member_bank_sampah");
        apiErecyle.put("str_api_list_request_member",".get_list_request");
        apiErecyle.put("str_api_acc_request_member",".approve_member");
        apiErecyle.put("str_api_tolak_request_member",".reject_member");

        //Tambahankan Daftar Item
        apiErecyle.put("str_api_add_item",".add_item");
        apiErecyle.put("str_api_list_item",".get_lestari_item");
        apiErecyle.put("str_api_list_daftar",".get_item");
        apiErecyle.put("str_api_listItem_update",".update_item");
        apiErecyle.put("str_api_pencairan_saldo",".pencairan_saldo");
        apiErecyle.put("str_api_history_order_user",".get_history_order");
        apiErecyle.put("str_api_update_profile",".change_profile");
        return apiErecyle;
    }

    public ArrayList<HashMap<String, String>> getJsonData(String[] field_name, String resp_content) throws JSONException {
        ArrayList<HashMap<String, String>> arrayReturn = new ArrayList<HashMap<String, String>>();
        JSONObject obj_json = null;

        String var_key, var_value, var_result_flag, var_message;
        String var_result;
        int i, json_length, json_item_length;
        HashMap<String, String> map_gen;
        var_result_flag = "0";
        var_result = "";

        try {
            obj_json = new JSONObject(resp_content);
            var_result_flag = obj_json.get("var_result").toString();
            var_message = obj_json.get("var_message").toString();
            var_result = obj_json.get("data").toString();
            map_gen = new HashMap<String, String>();
            map_gen.put("var_result",var_result_flag);
            map_gen.put("var_message",var_message);
            arrayReturn.add(map_gen);
            //if (var_result_flag.equals("1")) {
                JSONArray result_array = new JSONArray(var_result);

                for (i = 0; i < result_array.length(); i++) {
                    JSONObject obj = result_array.getJSONObject(i);

                    map_gen = new HashMap<String, String>();
                    /*for (int j = 0; j < field_name.length; j++) {
                        map_gen.put(field_name[j], obj.getString(field_name[j]));
                    }*/
                    for(int j=0;j<obj.length();j++){
                        String json_key = obj.names().getString(j);
                        String json_value = obj.getString(json_key);
                        map_gen.put(json_key,json_value);
                    }

                    arrayReturn.add(map_gen);
                }
            //}
        } catch (JSONException e) {
            e.printStackTrace();
            //showDialogLocal(e.getMessage());
        }
        return arrayReturn;
    }

    public String hpImei(Activity activity){
        String identifier = null;
        Context context = (Context) activity;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier .length() == 0)
            identifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return identifier;
    }
}
