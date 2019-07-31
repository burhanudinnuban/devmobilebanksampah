package com.demo.user.banksampah.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.user.banksampah.R;

public class CustomProgress {

    public static CustomProgress customProgress = null;
    private Dialog myDialog;
    protected ProgressBar myProgressBar;

    public static CustomProgress getInstance(){
        if (customProgress == null){
            customProgress = new CustomProgress();
        }
        return customProgress;
    }

    public void showProgress(Context context, String message, boolean cancelable){
        myDialog = new Dialog(context);

        //No Title for Dialog
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.custom_progress_dialog);

        //myProgressBar = (ProgressBar)myDialog.findViewById(R.id.progress_bar);
        TextView progressText = myDialog.findViewById(R.id.progress_text);
        //progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        //myProgressBar.setIndeterminate(cancelable);
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(cancelable);
        myDialog.show();

        //Bisa Diganti..

    }

    public void hideProgress(){
        if (myDialog!=null){
            myDialog.dismiss();
            myDialog = null;
        }
    }
}
