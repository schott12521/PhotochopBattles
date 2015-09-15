package com.scottlanoue.photochopbattles.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlanoue.photochopbattles.AsyncTasks.DownloadImagesTask;
import com.scottlanoue.photochopbattles.GalleryActivity;
import com.scottlanoue.photochopbattles.R;
import com.scottlanoue.photochopbattles.RedditJson.Comment;

import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GalleryViewPagerAdapter extends PagerAdapter {

    private int size;
    private String[] urls;
    private Context mContext;
    private List<Comment> commentsList;
    private GalleryActivity galleryActivity;

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

        // TODO get this layout to inflate
        TextView caption = (TextView) itemView.findViewById(R.id.galleryCaption);
        final ImageViewTouch image = (ImageViewTouch) itemView.findViewById(R.id.galleryImage);
        caption.setText(commentsList.get(position).getBody());

        /*
        This determines if the code needs to add an image extenstion to the link!

        NOTE: the first check makes sure that the program is not trying to add an image extension to a comment with an "Error 001"
         */
        if (!urls[position].contains("Error 001") &&!urls[position].substring(urls[position].lastIndexOf("/"), urls[position].length()).contains(".")) {
            urls[position] = urls[position] + ".png";
        }

        // TODO mess more with ion and animations
        new DownloadImagesTask(itemView, image, mContext).execute(urls[position]);
//        Ion.with(mContext)
//                .load(urls[position])
//                .withBitmap()
//                .animateGif(AnimateGifMode.ANIMATE)
//                .asBitmap()
//                .setCallback(new FutureCallback<Bitmap>() {
//                    @Override
//                    public void onCompleted(Exception e, Bitmap result) {
//                        image.setImageBitmap(result);
//                    }
//                });
//        Ion.with(mContext)
//                .load(urls[position])
//                .withBitmap()
//                .placeholder(R.drawable.fab_background)
//                .animateIn(Animation.ZORDER_NORMAL)
//                .intoImageView(image);
//                .into(image);

        /**
         * If I allow the image to adjust the view bounds, the image is no longer scrollable...
         */
//        image.setAdjustViewBounds(true);
        itemView.findViewById(R.id.progressBar).setVisibility(View.GONE);

        itemView.setOnClickListener(new View.OnClickListener() {
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
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    /**
     * I dont think this is needed...
     */
//    @Override
//    public void onPageSelected(int position) {
//        Log.v("hmmm ", "ok");
//    }
}
