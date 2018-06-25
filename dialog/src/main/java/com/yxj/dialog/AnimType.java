package com.yxj.dialog;

import com.yxj.dialog.anim.BaseAnim;
import com.yxj.dialog.anim.FadeIn;
import com.yxj.dialog.anim.Fall;
import com.yxj.dialog.anim.FlipH;
import com.yxj.dialog.anim.FlipV;
import com.yxj.dialog.anim.NewsPaper;
import com.yxj.dialog.anim.RotateBottom;
import com.yxj.dialog.anim.RotateLeft;
import com.yxj.dialog.anim.Shake;
import com.yxj.dialog.anim.SideFall;
import com.yxj.dialog.anim.SlideBottom;
import com.yxj.dialog.anim.SlideLeft;
import com.yxj.dialog.anim.SlideRight;
import com.yxj.dialog.anim.SlideTop;
import com.yxj.dialog.anim.Slit;

/**
 * Author:  Yxj
 * Time:    2018/6/25 下午5:30
 * -----------------------------------------
 * Description:
 */
public enum AnimType {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    Slidebottom(SlideBottom.class),
    Slideright(SlideRight.class),
    FalL(Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    Rotatebottom(RotateBottom.class),
    Rotateleft(RotateLeft.class),
    SLit(Slit.class),
    SHake(Shake.class),
    Sidefill(SideFall.class);

    private Class<? extends BaseAnim> clazz;

    AnimType(Class<? extends BaseAnim> clazz) {
        this.clazz = clazz;
    }

    public BaseAnim get() {
        BaseAnim anim = null;
        try {
            anim = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return anim;
    }
}
