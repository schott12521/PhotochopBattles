package com.scottlanoue.photochopbattles;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockDialogInterface;
import android.transition.Explode;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.scottlanoue.photochopbattles.RedditJson.Link;
import com.scottlanoue.photochopbattles.RedditJson.LinkFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String psbURL = "http://www.reddit.com/r/photoshopbattles/.json?limit=50";

    /**
     * Used for the RecyclerView
     */
    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerAdapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;

    private int xClickPos = 0, yClickPos = 0;
    public Bitmap mainPhoto;

    /**
     * These objects are used for scroll detection
     */
    private boolean notLoading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.color.primary700, typedValue, true);
            Log.v("This color: ", getResources().getColor(R.color.primary700) + " ");
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), getResources().getColor(R.color.primary700));
            setTaskDescription(td);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        This creates the toolbar for the main activity
         */
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerLayout);
        recyclerAdapter = new RecyclerViewAdapter();

        /*
         TODO this will decide how many 'tiles' should be shown based on screen metrics
         */
        Log.v("Size: ", getResources().getDisplayMetrics() + "");
        if (getResources().getDisplayMetrics().densityDpi == 320) {
            gridLayoutManager = new GridLayoutManager(this, 3);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 2);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        new DownloadLinksTask(psbURL, true).execute();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipe.setProgressViewOffset(true, 0, 225);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.GONE);
//                recyclerAdapter = new RecyclerViewAdapter();
                new DownloadLinksTask(psbURL, true).execute();
                notLoading = false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            int mLastVisibleItem = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (notLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount - 3) {
                        notLoading = false;
                        if (recyclerAdapter.linksList.size() > 0) {
                            /*
                            TODO fix this from showing duplicates, consult help on Reddit
                             */
                            Log.v("plz ", psbURL + "&count" + (totalItemCount - 2) + "&after=t3_" + recyclerAdapter.linksList
                                    .get(totalItemCount - 1).getId());
                            new DownloadLinksTask(psbURL + "&count" + (totalItemCount - 2) + "&after=t3_" + recyclerAdapter.linksList
                                    .get(totalItemCount - 1).getId(), false).execute();
                        }
                    }
                }

                /**
                 * This is what should be handling the hiding of action bar.
                 */
//                if (!notLoading) {
//                    final int currentFirstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
//                    if (currentFirstVisibleItem > mLastVisibleItem) {
//                        MainActivity.this.getSupportActionBar().hide();
//                    } else if (currentFirstVisibleItem < mLastVisibleItem) {
//                        MainActivity.this.getActionBar().show();
//                    }
//
//                    this.mLastVisibleItem = currentFirstVisibleItem;
//                }
            }

        });

        final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        /** TODO BAD BAD NOT GOOD **/
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onTouchEvent(RecyclerView v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("clicked", " plz");
                }
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View view = rv.findChildViewUnder(e.getX(), e.getY());
                if (view != null && gestureDetector.onTouchEvent(e)) {
                    xClickPos = (int) e.getX();
                    yClickPos= (int) e.getY();
                    int itemPos = rv.getChildAdapterPosition(view);
                    Link item = recyclerAdapter.linksList.get(itemPos);

                    Intent galleryIntent = new Intent(view.getContext(), GalleryActivity.class);
                    galleryIntent.putExtra("com.scottlanoue.photochopbattles.RedditJson.Link", item);
                    galleryIntent.putExtra("X", xClickPos);
                    galleryIntent.putExtra("Y", yClickPos);
                    if (!item.getDomain().contains("self"))
                        Log.v("baby", " justin"); // Sometimes, the app crashes here
                    if (mainPhoto != null) {
                        galleryIntent.putExtra("Bitmap", mainPhoto);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setExitTransition(new Explode());
                        Log.v("Transition: ", rv.toString());
//                        startActivity(galleryIntent, ActivityOptions.makeSceneTransitionAnimation(getParent()).toBundle());
                        // this is crashing, keep working....
                    } else {
                        startActivity(galleryIntent);
                    }
                    startActivity(galleryIntent);
                    return true;
                }
                return false;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

//        if (id == R.id.refresh) {
//            new DownloadLinksTask(psbURL, true).execute();
//        }

//        if (id == R.id.reset) {
//            text.setText("");
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used for the initial load or refreshing to get back to the first page.
     * @param urlString link to the url to grab links from
     * @return A list containing all of the fetched links
     * @throws IOException
     */
    public List<Link> refresh(String urlString) throws IOException {
        recyclerAdapter.disposeData();
//        Log.d("lets see", recyclerAdapter.toString());
        recyclerView.setVisibility(View.GONE);

        URL url = new URL(urlString);
        LinkFetcher fetch = new LinkFetcher();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(200);

        request.connect();
        return fetch.readJsonStream((InputStream) request.getContent());
    }

    /**
     * This method is used for adding data onto the end of the grid.
     * @param urlString Link to the url to grab more links from
     * @return A list containing the new links that were freshly pulled from the url
     * @throws IOException
     */
    public List<Link> neverEnding(String urlString) throws IOException {
        URL url = new URL(urlString);
        LinkFetcher fetch = new LinkFetcher();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(200);

        request.connect();

        notLoading = true;

        return fetch.readJsonStream((InputStream) request.getContent());
    }

    private class DownloadLinksTask extends AsyncTask<String, String, String> {
        List<Link> links = null;
        String url;
        boolean firstRefresh;

        public DownloadLinksTask(String url, boolean firstRefresh) {
            this.url = url;
            this.firstRefresh = firstRefresh;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                if (firstRefresh) {
                    links = refresh(url);
                } else {
                    links = neverEnding(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            if (swipe.isRefreshing()) {
                swipe.setRefreshing(false);
            }
            recyclerAdapter.addData(links);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            notLoading = true;
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.LinkViewHolder> {

        private List<Link> linksList = new ArrayList<>();

        @Override
        public LinkViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
            return new LinkViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final LinkViewHolder linkHolder, int i) {
            final Link link = linksList.get(i);

            /**
             * This if statement gets rid of the PsBattle before a post
             */
            if (link.getTitle().startsWith("PsBattle: ") || link.getTitle().startsWith("PSBattle: ")) {
//                link.refactorTitle(link.getTitle().substring(link.getTitle().indexOf(": ") + 2));
                linkHolder.title.setText(link.getTitle().substring(link.getTitle().indexOf(": ") + 2));
            } else {
                linkHolder.title.setText(link.getTitle());
            }

            linkHolder.additionalInfo.setText(link.getScore() + "");
//            linkHolder.numPictures.setText(link.getNumComments()); This line would display the num of comments, but I have to rethink this

            /*
            If the link is a selfpost, we don't have a picture to load!
             */
            if (link.getDomain().equals("self.photoshopbattles")) {
                linkHolder.photo.setImageResource(R.drawable.text_post);
                linkHolder.photo.setImageAlpha(128);
                linkHolder.title.setBackgroundColor(Color.BLACK);
                linkHolder.title.getBackground().setAlpha(128);
            } else {
                linkHolder.photo.setImageAlpha(255);
                Glide.with(getApplicationContext()).load(link.getUrl())
                        .centerCrop()
                        .fitCenter()
                        .crossFade()
                        .thumbnail(0.2f)
                        .into(linkHolder.photo);
                if (linkHolder.photo != null && linkHolder.photo.getDrawable() != null && ((GlideBitmapDrawable) linkHolder.photo.getDrawable()).getBitmap() != null)
                    // I don't think this works
                    mainPhoto = ((GlideBitmapDrawable) linkHolder.photo.getDrawable()).getBitmap();
                /*
                This code generates the title's background color using Palette and Glide but is acting very wonky...
                 */
//                Glide.with(getApplicationContext())
//                        .load(link.getUrl())
//                        .asBitmap()
//                        .into(new BitmapImageViewTarget(linkHolder.photo) {
//                            @Override
//                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                                super.onResourceReady(bitmap, anim);
//                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//                                    @Override
//                                    public void onGenerated(Palette palette) {
//                                        if (palette.getLightVibrantSwatch() != null) {
//                                            linkHolder.title.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
//                                            linkHolder.title.getBackground().setAlpha(128);
//                                        }
//                                        else {
//                                            linkHolder.title.setBackgroundColor(Color.BLACK);
//                                            linkHolder.title.getBackground().setAlpha(80);
//                                        }
//                                    }
//                                });
//                            }
//                        });
            }
        }

        @Override
        public int getItemCount() {
            return linksList.size();
        }

        public void disposeData() {
            linksList = new ArrayList<>();
            this.notifyDataSetChanged();
        }

        public void addData(List<Link> photos) {
            Set<Link> linksSet = new HashSet<>(linksList);

            for (Link link : photos) {
                if (!linksSet.contains(link)) {
//                    linksList.add(linksList.size(), link);
                    linksList.add(link);
                }
            }

            this.notifyDataSetChanged();
        }

        public String toString() {
            String returnString = "Links: ";
            for (Link link : linksList) {
                returnString = returnString + " " + link;
            }
            return returnString;
        }

        public class LinkViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView additionalInfo;
            ImageView photo;
            FrameLayout tile;

            LinkViewHolder(View itemView) {
                super(itemView);
                photo = (ImageView) itemView.findViewById(R.id.photo);
                additionalInfo = (TextView) itemView.findViewById(R.id.additionalInfo);
                title = (TextView) itemView.findViewById(R.id.title);
                tile = (FrameLayout) itemView.findViewById(R.id.tile);
            }

//  Deprecated onClick
//            @Override
//            public void onClick(View view) {
//                int itemPos = recyclerView.getChildLayoutPosition(view);
//                Link item = linksList.get(itemPos);
//
//                Intent galleryIntent = new Intent(view.getContext(), GalleryActivity.class);
//                galleryIntent.putExtra("com.scottlanoue.photochopbattles.RedditJson.Link", item);
//                if (!item.getDomain().contains("self"))
//                    Log.v("baby", " justin"); // Sometimes, the app crashes here
//                // We have to explore alternative options for sending this Bitmap
//                    try {
//                        galleryIntent.putExtra("BitmapImage", ((GlideBitmapDrawable) photo.getDrawable()).getBitmap());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                startActivity(galleryIntent);
//            }
        }
    }

    public class RecyclerViewOnItemTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerViewOnItemTouchListener(Context context, RecyclerView recyclerView, ClickListener clickListener) {
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    xClickPos = (int) e.getX();
                    yClickPos = (int) e.getY();

                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }
            });
            this.clickListener = clickListener;
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//            gestureDetector.onTouchEvent(e);
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.v("hmmmm ", " here");
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener {

        public void onClick(View view, int position);

//        public void onLongClick(View view, int position);
//        Don't think I neeed this yet...
    }
}