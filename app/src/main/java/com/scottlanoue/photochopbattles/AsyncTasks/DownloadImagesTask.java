package com.scottlanoue.photochopbattles.AsyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.scottlanoue.photochopbattles.R;

import java.io.InputStream;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

    ImageViewTouch bitmapImage;
    View view;

    public DownloadImagesTask(View viewIn, ImageViewTouch image) {
        bitmapImage = image;
        view = viewIn;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlsToShow = urls[0];
        Bitmap bitImage = null;
        try {
            InputStream in = new java.net.URL(urlsToShow).openStream();
            bitImage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {}
        return bitImage;
    }

    protected void onPostExecute(Bitmap result) {
        bitmapImage.setImageBitmap(result);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }
}