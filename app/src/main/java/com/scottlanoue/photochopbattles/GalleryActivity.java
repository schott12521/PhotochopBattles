package com.scottlanoue.photochopbattles;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.scottlanoue.photochopbattles.Adapters.GalleryViewPagerAdapter;
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
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);


        Toolbar galleryToolbar = (Toolbar) findViewById(R.id.gallery_toolbar);

        Link passedLink = (Link) getIntent().getSerializableExtra("com.scottlanoue.photochopbattles.RedditJson.Link");

        galleryToolbar.setTitle(passedLink.getTitle());
        setSupportActionBar(galleryToolbar);
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setStatusBarColor(R.color.primary700);
        }

//        ImageView galleryImage = (ImageView) findViewById(R.id.galleryImage);
//        Glide.with(getApplicationContext()).load(passedLink.getUrl()).placeholder(R.drawable.abc_spinner_mtrl_am_alpha).crossFade().into(galleryImage);
//        new DownloadImagesTask(findViewById(android.R.id.content), galleryImage).execute(passedLink.getUrl());

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        try {
            doWork(passedLink.getPermaLink() + ".json");
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.v("did we come here", passedLink.getPermaLink() + ".json");
        }

        /**
         * Null check that should probably be handled better
         */
        if (mPager != null) {
            mPager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeThisView();
                    Log.v("did we come here", "Hmmmm");
                }
            });
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    /**
     * For some reason, we are not matching up the correct item selected id's with the id's that we are looking for. hmmmmm.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clickOut) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // that hierarchy.
//            NavUtils.navigateUpFromSameTask(this);
            this.finish();
            Log.v("this should do it", "this should do it");
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

        mAdapter = new GalleryViewPagerAdapter(comments.size() - 1, getURLSfromCommments(comments), this.getApplicationContext(), comments, this);

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

    public void closeThisView() {
        this.finish();
    }
}
