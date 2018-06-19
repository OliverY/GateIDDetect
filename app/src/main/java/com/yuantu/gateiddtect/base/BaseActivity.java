package com.yuantu.gateiddtect.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.yuantu.gateiddtect.R;

import butterknife.ButterKnife;

/**
 * Author:  Yxj
 * Time:    2018/6/19 下午3:10
 * -----------------------------------------
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dealIntent(getIntent());
        setContentView(getContentView());
        ButterKnife.bind(this);
        initView();
        initData();

    }

    public abstract int getContentView();

    protected void dealIntent(Intent intent){

    }

    protected void initView(){

    }

    protected void initData(){

    }

    public void showDialog(String title, String message, String btn1, DialogCallback btn1Click,String btn2,DialogCallback btn2Click){
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
                builder.withDialogColor(getResources().getColor(R.color.green_45b97c))
                .withTitle(title)
                .withDuration(300)
                .withEffect(Effectstype.Fadein)
                .withMessage(message)
                .withButton1Text(btn1)
                .setButton1Click(v->{
                    btn1Click.onClick();
                    builder.dismiss();
                })
                .withButton2Text(btn2)
                .setButton2Click(v->{
                    btn2Click.onClick();
                    builder.dismiss();
                });
        builder.show();
    }

    public interface DialogCallback{
        void onClick();
    }

}
