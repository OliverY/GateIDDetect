package com.yuantu.gateiddtect.ui.detect;

import com.yuantu.gateiddtect.ui.BaseView;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午9:47
 * -----------------------------------------
 * Description:
 */
public interface DetectView extends BaseView {

    void showDetectDialog(String name, float percent, String imgUrl);
}
