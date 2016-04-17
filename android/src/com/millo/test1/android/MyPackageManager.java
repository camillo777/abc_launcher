package com.millo.test1.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.millo.test1.MyGdxGame;
import com.millo.test1.MyPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyPackageManager implements MyGdxGame.MyAndroidPackageManager {

    private final static String TAG = "MyPackageManager";

    private final Activity mActivity;
    //ApplicationAdapter mAppAdapter;
    //ArrayList<Drawable> apps;

    public MyPackageManager(Activity activity) { //, ApplicationAdapter aa) {
        mActivity = activity;
        //mAppAdapter = aa;
    }

    @Override
    public ArrayList<MyPackage> getIcons() {
        Log.d(TAG, "getIcons");
        ArrayList<MyPackage> apps = new ArrayList<MyPackage>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = mActivity.getPackageManager().queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            apps.add(
                    new MyPackage(
                            getIcon(ri.activityInfo.packageName),
                            ri.activityInfo.packageName,
                            ri.activityInfo.applicationInfo.loadLabel(mActivity.getPackageManager()).toString()
                            )
            );
//            apps.add(ri.activityInfo.loadIcon(mActivity.getPackageManager()));
        }

        return apps;
    }


    public Texture getIcon(String packageName) {
        Log.d(TAG, "getIcon");

        Texture tex = null;

        Bitmap bitmap = null;

        String cacheFileName = "icon-"+packageName;

        Log.d(TAG, "DIR:"+mActivity.getCacheDir()+" FILE:"+cacheFileName);
        File localFile = new File(mActivity.getCacheDir(), cacheFileName);
//        if (localFile.exists()) {
//            Log.d(TAG, "File exists");
//            // the file exists in the sdcard, just load it
//            try {
//                bitmap = BitmapFactory.decodeStream(new FileInputStream(localFile));
//            } catch (FileNotFoundException e1) {
//                Log.e(TAG, e1.getMessage());
//            }
//
//            // we have our bitmap from the sdcard !! Let's put it into our HashMap
//            //sIcons.put(key, myBitmap)
//        } else {
            Log.d(TAG, "File NOT exists");
            PackageManager pm = mActivity.getPackageManager();
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e1) {
                Log.e(TAG, e1.getMessage());
            }

            Drawable d = pi.applicationInfo.loadIcon(pm);
            bitmap = getResizedBitmap(((BitmapDrawable) d).getBitmap(), 64, 64);
            // use the slow method
            // prepare a file to the application cache dir.
            File cachedFile = new File(mActivity.getCacheDir(), cacheFileName);
            // save our bitmap as a compressed JPEG with the package name as filename
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(cachedFile));
            } catch (FileNotFoundException e1) {
                Log.e(TAG, e1.getMessage());
            }
//        }

//            Picasso.Builder builder = new Picasso.Builder(mActivity);
//            Picasso picasso = builder.build();
//            Bitmap bitmap = picasso.load("app-icon:"+pi.packageName).get();

        tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        bitmap.recycle();

        return tex;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Log.d(TAG, "getResizedBitmap");

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
// CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

// "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public void logd(String _TAG, String s){
        Log.d(_TAG, s);
    }
}