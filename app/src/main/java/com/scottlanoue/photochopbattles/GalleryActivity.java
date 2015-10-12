package com.scottlanoue.photochopbattles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private Link link;
    private int xStartPos, yStartPos;

    public Bitmap mainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            View galleryView = findViewById(R.id.gallery_frame_layout);
//            galleryView.setVisibility(View.GONE);
//            Animator anim = ViewAnimationUtils.createCircularReveal(galleryView,
//                    (int) getIntent().getSerializableExtra("X"), (int) getIntent().getSerializableExtra("Y"), 0,
//                    Math.max(galleryView.getWidth(), galleryView.getHeight()));
            getWindow().setEnterTransition(new Explode());
        }


        xStartPos = (int) getIntent().getSerializableExtra("X");
        yStartPos = (int) getIntent().getSerializableExtra("Y");

        Toolbar galleryToolbar = (Toolbar) findViewById(R.id.gallery_toolbar);

        Link passedLink = (Link) getIntent().getSerializableExtra("com.scottlanoue.photochopbattles.RedditJson.Link");
        link = passedLink;
        mainImage = getIntent().getParcelableExtra("Bitmap");

        galleryToolbar.setTitle(passedLink.getTitle());
        setSupportActionBar(galleryToolbar);

//        ImageView galleryImage = (ImageView) findViewById(R.id.galleryImage);
//        Glide.with(getApplicationContext()).load(passedLink.getUrl()).placeholder(R.drawable.abc_spinner_mtrl_am_alpha).crossFade().into(galleryImage);
//        new DownloadImagesTask(findViewById(android.R.id.content), galleryImage).execute(passedLink.getUrl());

        progressBar = (ProgressBar) findViewById(R.id.gallery_progress_bar);

        try {
            doWork(passedLink.getPermaLink() + ".json");
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.v("did we come here", " not good");
            e.printStackTrace();
        }

        /**
         * Null check that should probably be handled better
         */
        if (mPager != null) {
            mPager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeThisView();
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
            closeThisView();
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
        /*
        This is a bad hack/workaround to get the first image to show as a 'comment' even though it's the linked image...ew lol
         */
        comments.add(0, new Comment(null, link.getUrl(), "Main Image: ", 0, null));

//        mAdapter.addView();
        mAdapter = new GalleryViewPagerAdapter(comments.size() - 1, getURLSfromCommments(comments), this.getApplicationContext(), comments, this, mainImage);
//        mAdapter = new GalleryViewPagerAdapter(comments.size() - 1, getURLSfromCommments(comments), this.getApplicationContext(), comments, this);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setAdapter(mAdapter);

        /**
         * TODO Show the progress bar while loading!
         */
        mPager.setVisibility(View.VISIBLE);
    }

    public String[] getURLSfromCommments(List<Comment> comments) {

        String[] returnArray = new String[comments.size() - 1];
        for (int i = 0; i < comments.size() - 1; i++) {
            returnArray[i] = comments.get(i).getImageLink();
        }
        return  returnArray;

    }

    public void closeThisView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final View myView = findViewById(R.id.gallery_frame_layout);
                int cx = myView.getWidth() / 2;
                int cy = myView.getHeight() / 2;
                int initialRadius = myView.getWidth();
                final Animator anim = ViewAnimationUtils.createCircularReveal(myView, xStartPos, yStartPos, initialRadius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        myView.setVisibility(View.GONE);
                        finish();
                    }
                });
                anim.start();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }
    }

    public void finish() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            super.finishAfterTransition();
        else
            super.finish();
        overridePendingTransition(0, 0);
    }

    public Link getLink() {
        return link;
    }
}
