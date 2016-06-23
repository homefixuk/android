package com.samdroid.image;

import android.graphics.Bitmap;

import com.samdroid.game.view.Graphics.ImageFormat;

public class AndroidImage implements Image {
    Bitmap bitmap;
    ImageFormat format;
    
    public AndroidImage(Bitmap bitmap, ImageFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }
    
    public Bitmap getBitmap() {
    	return bitmap;
    }

    public ImageFormat getImageFormat() {
    	return format;
    }

    @Override
    public int getWidth() {
        return bitmap != null ? bitmap.getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return bitmap != null ? bitmap.getHeight() : 0;
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        if (bitmap != null) bitmap.recycle();
    }      
}
