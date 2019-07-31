package com.demo.user.banksampah;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.user.banksampah.Activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by anthon on 3/3/2017.
 */
public class DialogProcess {
    public static final String MSG_CONNECTION_PROBLEM = "Koneksi internet terganggu!Coba sebentar lagi.";
    public static final String MSG_DATA_GET_FAILED = "Gagal mendapatkan data! Coba sekali lagi.";
    public static final String MSG_REQ_SUCCESS = "Sukses.";
    public static final String MSG_REQ_FAILED = "Gagal.";
    public static final String TITLE_OFFER = "Offer";
    public static final int SECTION_OFFER = 1;
    public ImageLoader imageLoader;
    HashMap<String, String> map_dlg = new HashMap<String, String>();
    private WebView wvPromoTnc;

    public void showDialogLocal(final Activity activity, final HashMap<String, String> map_gen){

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        // Inflate the popup_layout.xml
        LayoutInflater li = LayoutInflater.from(activity.getApplicationContext());
        View viewOk = li.inflate(R.layout.dlg_ok, null);
        viewOk.setBackgroundResource(android.R.color.transparent);
        TextView tvOkTitle = (TextView) viewOk.findViewById(R.id.tvOkTitle);
        TextView tvOkMsg = (TextView) viewOk.findViewById(R.id.tvOkMsg);
        Button btnClose = (Button) viewOk.findViewById(R.id.btnClose);
        tvOkTitle.setText(map_gen.get("title"));
        tvOkMsg.setText(map_gen.get("msg"));
        alertDialog.setView(viewOk);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();

    }

    public void showDialogLocalSuccess(final Activity activity, final HashMap<String, String> map_gen){

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        // Inflate the popup_layout.xml
        LayoutInflater li = LayoutInflater.from(activity.getApplicationContext());
        View viewOk = li.inflate(R.layout.dlg_ok, null);
        viewOk.setBackgroundResource(android.R.color.transparent);
        TextView tvOkTitle = (TextView) viewOk.findViewById(R.id.tvOkTitle);
        TextView tvOkMsg = (TextView) viewOk.findViewById(R.id.tvOkMsg);
        Button btnClose = (Button) viewOk.findViewById(R.id.btnClose);
        tvOkTitle.setText(map_gen.get("title"));
        tvOkMsg.setText(map_gen.get("msg"));
        alertDialog.setView(viewOk);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent merch_intent = new Intent(activity, MainActivity.class);
                activity.startActivity(merch_intent);
                alertDialog.dismiss();
                activity.finish();


            }
        });
        alertDialog.show();

    }

    public void showDialogCoupon(final Activity activity, final HashMap<String, String> map_gen, Bitmap promoImg){

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        // Inflate the popup_layout.xml
        LayoutInflater li = LayoutInflater.from(activity.getApplicationContext());
        View viewOk = li.inflate(R.layout.dlg_coupon, null);

        TextView tvDlgCTitle = (TextView) viewOk.findViewById(R.id.tvDlgCTitle);
        tvDlgCTitle.setText(map_gen.get("title"));


        ImageView ivDlgCProduct = (ImageView) viewOk.findViewById(R.id.ivDlgCProduct);
        /*imageLoader = new ImageLoader(viewOk.getContext());
        imageLoader.DisplayImage(map_gen.get("display_pic1"),ivDlgCProduct);*/
        ivDlgCProduct.setImageBitmap(promoImg);

        TextView tvDlgCMerchantName = (TextView) viewOk.findViewById(R.id.tvDlgCMerchantName);
        tvDlgCMerchantName.setText(map_gen.get("merchant_name"));

        TextView tvDlgCBandaraName = (TextView) viewOk.findViewById(R.id.tvDlgCBandaraName);
        tvDlgCBandaraName.setText(map_gen.get("address1"));

        TextView tvDlgCProductName = (TextView) viewOk.findViewById(R.id.tvDlgCProductName);
        tvDlgCProductName.setText(map_gen.get("product_name"));

        TextView tvDlgCOutletName = (TextView) viewOk.findViewById(R.id.tvDlgCOutletName);
        tvDlgCOutletName.setText(map_gen.get("outlet_name"));

        TextView tvDlgMemberName = (TextView) viewOk.findViewById(R.id.tvDlgMemberName);
        tvDlgMemberName.setText("MEMBER NAME: " + map_gen.get("full_name"));

        TextView tvDlgVcardMember = (TextView) viewOk.findViewById(R.id.tvDlgVcardMember);
        tvDlgVcardMember.setText("MEMBER CARD: " + map_gen.get("card_number"));

        TextView tvDlgCouponNo = (TextView) viewOk.findViewById(R.id.tvDlgCouponNo);
        tvDlgCouponNo.setText(map_gen.get("coupon_code"));

        TextView tvRef = (TextView) viewOk.findViewById(R.id.tvRef);
        tvRef.setText(map_gen.get("sys_reference_no"));


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        TextView tvDlgTgl = (TextView) viewOk.findViewById(R.id.tvDlgTgl);
        tvDlgTgl.setText(formattedDate);


        Button btnClose = (Button) viewOk.findViewById(R.id.btnDlgCClose);


        alertDialog.setView(viewOk);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent merch_intent = new Intent(activity, VoucherTxHistoryActivity.class);
                activity.startActivity(merch_intent);
                alertDialog.dismiss();
                activity.finish();*/

            }
        });
        alertDialog.show();

    }

    /*2017-08-08 fix on getting the string in strings.xml*/
    public String systemMsgConverter(Context context, String sysMsg){
        String localMsg;
        Activity activity = (Activity) context;
        int resId = context.getResources().getIdentifier(sysMsg,"string",activity.getPackageName());

        if(resId==0)
            //localMsg = "UNKNOWN!";
            localMsg = context.getResources().getString(resId);
        else
            localMsg = context.getResources().getString(resId);

        return localMsg;
    }

}
