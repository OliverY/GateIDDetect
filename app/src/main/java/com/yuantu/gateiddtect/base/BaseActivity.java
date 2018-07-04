package com.yuantu.gateiddtect.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(getContentView());
        ButterKnife.bind(this);
        initView();
        initData();

    }

    protected void dealIntent(Intent intent){

    }

    public abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();

}
