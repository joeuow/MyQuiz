package com.example.myquiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by joewei on 2016-10-16.
 */

public class GeneralDialog {
    Toast toast;
    ProgressDialog progressDialog;

    GeneralDialog (Context context, boolean condition, String prg_string, String rst_suc_string, String rst_fail_string) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(prg_string);

        if(condition)
            toast = Toast.makeText(context, rst_suc_string, Toast.LENGTH_SHORT);
        else
            toast = Toast.makeText(context, rst_fail_string, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
    }

    public void DisplayProgress() {
        //progressDialog.setDismissMessage(msg);
        progressDialog.show();

        new android.os.Handler().postDelayed(
               new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        toast.show();
                    }
                }, 1000);
    }
}
