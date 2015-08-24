package com.scottlanoue.photochopbattles;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scottlanoue.photochopbattles.RedditJson.Link;
import com.scottlanoue.photochopbattles.RedditJson.RedditFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerLayout);
        recycleAdapter = new RecyclerViewAdapter();
        final RecyclerView.LayoutManager llm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(recycleAdapter);


        new DownloadLinksTask().execute();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                recyclerView.setVisibility(View.GONE);
                recycleAdapter = new RecyclerViewAdapter();
                new DownloadLinksTask().execute();
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

        if (id == R.id.refresh) {
            new DownloadLinksTask().execute();
        }

//        if (id == R.id.reset) {
//            text.setText("");
//        }

        return super.onOptionsItemSelected(item);
    }

    public List<Link> refresh() throws IOException {
        recycleAdapter.disposeData();
        Log.d("lets see", recycleAdapter.toString());
//        recyclerView.setVisibility(View.GONE);

        URL url = new URL("http://www.reddit.com/r/photoshopbattles.json");
        RedditFetcher fetch = new RedditFetcher();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(200);

        request.connect();
        List<Link> linkList = fetch.readJsonStream((InputStream) request.getContent());
        Snackbar.make(findViewById(R.id.swipeRefresh), "We did it!", Snackbar.LENGTH_SHORT).show();

//        for (Link link : linkList) {
//            text.append(link.toString());
//            text.append("\n\n");
//        }
//        swipe.setRefreshing(false);
        return linkList;
    }

    private class DownloadLinksTask extends AsyncTask<String, Void, String> {
        List<Link> links = null;

        @Override
        protected String doInBackground(String... urls) {
            try {
               links = refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            swipe.setRefreshing(false);
            recycleAdapter.addData(links);
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

            linkHolder.title.setText(link.getTitle());
//            linkHolder.score.setText(link.getScore() + " ");
//            linkHolder.permaLink.setText(link.getPermaLink());

            if (link.getDomain().equals("self.photoshopbattles")) {
                linkHolder.photo.setImageResource(R.drawable.text_post);
                linkHolder.photo.setImageAlpha(128);
//                Glide.with(getApplicationContext()).load(link.getUrl()).into(linkHolder.photo);
            } else {
                linkHolder.photo.setImageAlpha(255);
                Glide.with(getApplicationContext()).load(link.getUrl())
                        .centerCrop()
//                        .placeholder(R.drawable.progress_indeterminate_horizontal)
                        .crossFade()
                        .into(linkHolder.photo);
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

        public class LinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            TextView score;
            ImageView photo;

            LinkViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                photo = (ImageView) itemView.findViewById(R.id.photo);
                title = (TextView) itemView.findViewById(R.id.title);
            }

            @Override
            public void onClick(View view) {
                int itemPos = recyclerView.getChildLayoutPosition(view);
                Link item = linksList.get(itemPos);

                Intent galleryIntent = new Intent(view.getContext(), GalleryActivity.class);
                galleryIntent.putExtra("com.scottlanoue.photochopbattles.RedditJson.Link", item);
                startActivity(galleryIntent);
            }
        }
    }
}