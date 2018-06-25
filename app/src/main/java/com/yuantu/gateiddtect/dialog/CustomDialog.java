package com.yuantu.gateiddtect.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

/**
 * Author:  Yxj
 * Time:    2018/6/22 下午2:33
 * -----------------------------------------
 * Description:
 */
public class CustomDialog extends AlertDialog {

    protected CustomDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    protected CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private void init(Context context) {

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

            }
        });


    }


    public static class Builder{

        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public CustomDialog create(){
            LayoutInflater.from(context).inflate(R.layout.exit_alert,null);
        }
    }

}
