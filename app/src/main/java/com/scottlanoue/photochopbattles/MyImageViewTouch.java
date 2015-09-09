package com.scottlanoue.photochopbattles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class MyImageViewTouch extends ImageViewTouch {

    /**
     * This class comes from a GitHub post, I'm trying to make it so you can't scroll on the viewPager while
     * the image is zoomed in.
     * TODO fix this
     * @param context
     * @param attrs
     */
    public MyImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getScale() > 1f) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

}
