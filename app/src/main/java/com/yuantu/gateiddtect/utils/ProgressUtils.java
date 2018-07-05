package com.yuantu.gateiddtect.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.yuantu.gateiddtect.R;

/**
 * Author:  Yxj
 * Time:    2018/7/4 下午10:12
 * -----------------------------------------
 * Description:
 */
public class ProgressUtils {

    public static ProgressDialog show(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setMessage("数据加载中...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void hide(ProgressDialog progressDialog){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
