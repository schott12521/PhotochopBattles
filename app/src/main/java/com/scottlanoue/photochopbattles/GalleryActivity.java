package com.scottlanoue.photochopbattles;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.ImageView;

import com.scottlanoue.photochopbattles.RedditJson.Link;

import java.io.InputStream;

public class GalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        Link passedLink = (Link) getIntent().getSerializableExtra("com.scottlanoue.photochopbattles.RedditJson.Link");
        Log.d("this is the link", passedLink + " ");

        ImageView galleryImage = (ImageView) findViewById(R.id.galleryImage);
//        Glide.with(getApplicationContext()).load(passedLink.getUrl())
//                .placeholder(R.drawable.abc_spinner_mtrl_am_alpha)
//                .crossFade()
//                .into(galleryImage);
        new DownloadImagesTask(galleryImage).execute(passedLink.getUrl());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bitmapImage;

        public DownloadImagesTask(ImageView image) {
            bitmapImage = image;
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
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}
