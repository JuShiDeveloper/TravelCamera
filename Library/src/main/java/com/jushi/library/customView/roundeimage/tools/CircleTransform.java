package com.jushi.library.customView.roundeimage.tools;

/**
 * Describe:圆形
 * <p>
 * Created by zhigang wei
 * on 2018/5/11
 * <p>
 * Company :cpx
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 圆形的Transformation
 * Created by Raye on 2016/5/3.
 */
public class CircleTransform extends BitmapTransformation {

    private static final String ID = "com.base.library.view.roundeimage.CircleTransform";
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));


    public CircleTransform(Context context) {

    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());

        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

        Bitmap bitmap = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader =
                new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            // source isn't square, move viewport to center
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }

    @Override
    public boolean equals(Object o){
        return o instanceof CircleTransform;
    }

    @Override
    public int hashCode(){
        return ID.hashCode();
    }
}