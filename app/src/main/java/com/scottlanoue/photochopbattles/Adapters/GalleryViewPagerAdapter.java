package com.scottlanoue.photochopbattles.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.scottlanoue.photochopbattles.AsyncTasks.DownloadImagesTask;
import com.scottlanoue.photochopbattles.MainActivity;
import com.scottlanoue.photochopbattles.R;
import com.scottlanoue.photochopbattles.RedditJson.Comment;

import org.w3c.dom.Text;

import java.lang.annotation.Target;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GalleryViewPagerAdapter extends PagerAdapter {

    private int size;
    private String[] urls;
    private Context mContext;
    private List<Comment> commentsList;

    public GalleryViewPagerAdapter(int numItems, String[] urls, Context contextIn, List<Comment> commentsList) {
        this.size = numItems;
        this.urls = urls;
        this.mContext = contextIn;
        this.commentsList = commentsList;
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
         */
        if (!urls[position].substring(urls[position].lastIndexOf("/"), urls[position].length()).contains(".")) {
            urls[position] = urls[position] + ".png";
        }

        // TODO mess more with ion and animations
        Ion.with(mContext)
                .load(urls[position])
                .withBitmap()
                .animateGif(AnimateGifMode.ANIMATE)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        image.setImageBitmap(result);
                    }
                });
//        Ion.with(mContext)
//                .load(urls[position])
//                .withBitmap()
//                .placeholder(R.drawable.fab_background)
//                .animateIn(Animation.ZORDER_NORMAL)
//                .intoImageView(image);
//        new DownloadImagesTask(itemView, image, mContext).execute(urls[position]);
//                .into(image);
//        image.setAdjustViewBounds(true);
        itemView.findViewById(R.id.progressBar).setVisibility(View.GONE);

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
