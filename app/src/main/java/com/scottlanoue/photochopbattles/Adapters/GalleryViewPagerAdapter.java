package com.scottlanoue.photochopbattles.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.scottlanoue.photochopbattles.AsyncTasks.DownloadImagesTask;
import com.scottlanoue.photochopbattles.GalleryActivity;
import com.scottlanoue.photochopbattles.R;
import com.scottlanoue.photochopbattles.RedditJson.Comment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public class GalleryViewPagerAdapter extends PagerAdapter {

    private int size;
    private String[] urls;
    private Context mContext;
    private List<Comment> commentsList;
    private GalleryActivity galleryActivity;

    public GalleryViewPagerAdapter() {

    }

    public GalleryViewPagerAdapter(int numItems, String[] urls, Context contextIn, List<Comment> commentsList, GalleryActivity galleryActivity) {
        this.size = numItems;
        this.urls = urls;
        this.mContext = contextIn;
        this.commentsList = commentsList;
        this.galleryActivity = galleryActivity;
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
                .inflate(R.layout.gallery_item, container, false);

        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) itemView.findViewById(R.id.sliding_layout);
        TextView caption = (TextView) itemView.findViewById(R.id.parent_comment);
        final SubsamplingScaleImageView image = (SubsamplingScaleImageView) itemView.findViewById(R.id.galleryImage);

        slidingUpPanelLayout.setAnchorPoint(0.5f);
        caption.setText(commentsList.get(position).getBody());

        /*
        This determines if the code needs to add an image extenstion to the link!

        NOTE: the first check makes sure that the program is not trying to add an image extension to a comment with an "Error 001"
         */
        if (!urls[position].contains("Error 001") && !urls[position].substring(urls[position].lastIndexOf("/"), urls[position].length()).contains(".")) {
            urls[position] = urls[position] + ".png";
        }

        // TODO mess more with ion and animations
//        new DownloadImagesTask(itemView, image, mContext).execute(urls[position]);
        Ion.with(mContext)
                .load(urls[position])
                .withBitmap()
//                .animateGif(AnimateGifMode.ANIMATE)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null)
                            image.setImage(ImageSource.bitmap(result));
                    }
                });

//        View.findViewById(R.id.progressBar).setVisibility(View.GONE);

        /**
         * This allows me to click on the image to kill the gallery activity and return to the main list!
         */
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryActivity.closeThisView();
                Log.v("did we come here", "Hmmmm");
            }
        });

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((SlidingUpPanelLayout) object);
    }
}
