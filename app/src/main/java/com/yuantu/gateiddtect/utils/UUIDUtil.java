package com.yuantu.gateiddtect.utils;

import java.util.UUID;

/**
 * Author:  Yxj
 * Time:    2018/6/19 下午4:16
 * -----------------------------------------
 * Description: 用于生成uuid，只取16位，因为faceid注册限制了16位
 */
public class UUIDUtil {

//    public static String generateUUID(){
//        String[] strs = UUID.randomUUID().toString().split("-");
//        return new StringBuilder().append(strs[0])
//                .append(strs[1])
//                .append(strs[2]).toString();
//    }

    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }
}
