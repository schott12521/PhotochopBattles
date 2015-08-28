package com.scottlanoue.photochopbattles;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;

import com.scottlanoue.photochopbattles.Adapters.GalleryViewPagerAdapter;
import com.scottlanoue.photochopbattles.AsyncTasks.DownloadImagesTask;
import com.scottlanoue.photochopbattles.RedditJson.Comment;
import com.scottlanoue.photochopbattles.RedditJson.CommentFetcher;
import com.scottlanoue.photochopbattles.RedditJson.Link;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private GalleryViewPagerAdapter mAdapter;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        Link passedLink = (Link) getIntent().getSerializableExtra("com.scottlanoue.photochopbattles.RedditJson.Link");
        Log.d("this is the link", passedLink + " ");

//        ImageView galleryImage = (ImageView) findViewById(R.id.galleryImage);
//        Glide.with(getApplicationContext()).load(passedLink.getUrl()).placeholder(R.drawable.abc_spinner_mtrl_am_alpha).crossFade().into(galleryImage);
//        new DownloadImagesTask(findViewById(android.R.id.content), galleryImage).execute(passedLink.getUrl());

        try {
            doWork(passedLink.getPermaLink() + ".json");
        } catch (Exception e) {
            Log.v("did we come here", passedLink.getPermaLink() + ".json");
        }
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

    public void doWork(String urlString) throws IOException {
        URL url = new URL(urlString);
        CommentFetcher fetch = new CommentFetcher();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(200);

        request.connect();
        List<Comment> comments = fetch.readJsonStream((InputStream) request.getContent());

        mAdapter = new GalleryViewPagerAdapter(comments.size(), getURLSfromCommments(comments), this.getApplicationContext());
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setAdapter(mAdapter);
    }

    public String[] getURLSfromCommments(List<Comment> comments) {

        String[] returnArray = new String[comments.size() - 1];
        for (int i = 0; i < comments.size() - 1; i++) {
            returnArray[i] = comments.get(i).getImageLink();
        }
        return  returnArray;

    }
}
