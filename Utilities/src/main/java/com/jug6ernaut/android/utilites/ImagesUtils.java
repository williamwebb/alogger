package com.jug6ernaut.android.utilites;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;



public class ImagesUtils{
	
    public static Drawable grabImageFromUrl(String url) throws Exception {
    	return Drawable.createFromStream((InputStream)new URL(url).getContent(), "src");
    }
    
    public static Bitmap drawableToBitmap(Drawable d){
    	Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
    	return bitmap;
    }
	
}