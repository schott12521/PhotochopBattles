package com.scottlanoue.photochopbattles.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.scottlanoue.photochopbattles.AsyncTasks.DownloadImagesTask;
import com.scottlanoue.photochopbattles.MainActivity;
import com.scottlanoue.photochopbattles.R;

import org.w3c.dom.Text;

public class GalleryViewPagerAdapter extends PagerAdapter {

    private int size;
    private String[] urls;
    private Context mContext;

    public GalleryViewPagerAdapter(int numItems, String[] urls, Context contextIn) {
        this.size = numItems;
        this.urls = urls;
        this.mContext = contextIn;
        for (String url : urls) {
            Log.v("URLS: ", url);
        }
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        View itemView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(R.layout.gallery_layout, container, false);
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.gallery_layout, container, false);

        // TODO get this layout to inflate
//        ImageView image = (ImageView) itemView.findViewById(R.id.galleryImage);
        TextView text = (TextView) itemView.findViewById(R.id.sampleHelp);
        text.setText(urls[position] + " help");
        Snackbar.make(itemView, position + " ", Snackbar.LENGTH_SHORT).show();
//        image.setImageResource(new DownloadImagesTask(itemView.findViewById(android.R.id.content), image).execute(urls[0]));
//        image.setImageBitmap(new DownloadImagesTask(itemView.findViewById(android.R.id.content), image).execute(urls[0]));

        // TODO Gotta actually finish the GalleryViewPagerAdapter
        container.addView(itemView);
        return container;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ViewPager) object);
    }

}
