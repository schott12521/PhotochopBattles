package com.scottlanoue.photochopbattles.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.scottlanoue.photochopbattles.AsyncTasks.DownloadImagesTask;
import com.scottlanoue.photochopbattles.MainActivity;
import com.scottlanoue.photochopbattles.R;

public class GalleryViewPager extends PagerAdapter {

    private int size;
    private String[] urls;
    private Context mContext;

    public GalleryViewPager(int numItems, String[] urls, Context contextIn) {
        this.size = numItems;
        this.urls = urls;
        this.mContext = contextIn;
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
        View itemView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.gallery_layout, container, false);

        ImageView image = (ImageView) itemView.findViewById(R.id.galleryImage);
//        image.setImageResource(new DownloadImagesTask(itemView.findViewById(android.R.id.content), image).execute(urls[0]));
//        image.setImageBitmap(new DownloadImagesTask(itemView.findViewById(android.R.id.content), image).execute(urls[0]));

        // TODO Gotta actually finish the GalleryViewPager
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}
