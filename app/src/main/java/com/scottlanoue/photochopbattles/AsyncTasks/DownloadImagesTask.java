package com.scottlanoue.photochopbattles.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.scottlanoue.photochopbattles.R;

import java.io.InputStream;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

    ImageViewTouch bitmapImage;
    View view;
    Context mContext;

    public DownloadImagesTask(Context contextIn) {
//        bitmapImage = image;
//        view = viewIn;
        mContext = contextIn;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlsToShow = urls[0];
        Bitmap bitImage = null;
        try {
//            InputStream in = new java.net.URL(urlsToShow).openStream();
//            bitImage = BitmapFactory.decodeStream(in);
            bitImage = Ion.with(mContext)
                    .load(urlsToShow)
                    .asBitmap()
//                    .setCallback(new FutureCallback<Bitmap>() {
//                    @Override
//                    public void onCompleted(Exception e, Bitmap result) {
////                        image.setImageBitmap(result);
//                    }
//                })
                    .get();
        } catch (Exception e) {}
        return bitImage;
    }

    protected void onPostExecute(Bitmap result) {
        bitmapImage.setImageBitmap(result);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }
}