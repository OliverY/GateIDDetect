package com.yuantu.gateiddtect.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Author:  Yxj
 * Time:    2018/7/5 下午8:40
 * -----------------------------------------
 * Description:
 */
public class GlideCircleTransform extends BitmapTransformation {

    public GlideCircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool,toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Logger.e("source.getWidth()::"+source.getWidth()+"  source.getHeight()::"+source.getHeight());
        int size = Math.min(source.getWidth(),source.getHeight());
        int x = (source.getWidth()-size)/2;
        int y = (source.getHeight()-size)/2;

        Logger.e("x="+x+" y="+y);

        Bitmap squared = Bitmap.createBitmap(source,x,y,size,size);
        Bitmap result = pool.get(size,size,Bitmap.Config.ARGB_8888);
        if(result==null){
            Logger.e("result == null");
            result = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(squared,BitmapShader.TileMode.CLAMP,BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size/2f;
        canvas.drawCircle(r,r,r,paint);
        return result;
    }


    @Override
    public String getId() {
        return getClass().getName();
    }
}
