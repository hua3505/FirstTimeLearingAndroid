package com.gmail.huashadow.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gmail.huashadow.myapplication.ticwear.R;


public class PhotoActivity extends Activity {
    public final static String SAVED_PATH = "com.gmail.huashadow.myapplication.SAVED_PATH";
    private String mPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mPhotoPath = getIntent().getStringExtra(MainActivity.EXTRA_PHOTO);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!mPhotoPath.equals("")) {
            setPic(mPhotoPath);
        }
    }

    private void setPic(String photoPath) {
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        int targetWidth = imageView.getWidth();
        int targetHeight = imageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;

        int scaleFactor = Math.max(photoWidth / targetWidth, photoHeight / targetHeight);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor > 1 ? scaleFactor : 1;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
}
