package com.yuantu.gateiddtect.db;

import org.litepal.crud.LitePalSupport;

/**
 * Author:  Yxj
 * Time:    2018/6/20 上午9:36
 * -----------------------------------------
 * Description:
 */
public class DBDao<T extends LitePalSupport> {

    public boolean insert(T t){
        return t.save();
    }
}
