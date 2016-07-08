package com.example.xyzreader.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Locale;

public class GlideLoaderListener<T, R> implements RequestListener<T, R> {

    Context ctx;
    int defaultImg;
    ImageView imgView;
    int mMutedColor;
    View mMetaBar;

    public GlideLoaderListener(Context c, int img, ImageView imgView, int mutedColor, View mBar)
    {
        this.ctx = c;
        this.defaultImg = img;
        this.imgView = imgView;
        this.mMutedColor = mutedColor;
        this.mMetaBar = mBar;
    }
    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        android.util.Log.d("GLIDE LoggingListener", String.format(Locale.ROOT,
                "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && imgView != null) {
            imgView.setImageResource(defaultImg);
            scheduleStartPostponedTransition(imgView, ((Activity) this.ctx));
        }
        else
        {
            Glide.with(ctx).load(defaultImg).fitCenter().into(target);
        }

        return true;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        android.util.Log.d("GLIDE LoggingListener", String.format(Locale.ROOT,
                "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));

        if(resource instanceof Bitmap && this.imgView != null)
        {
            Bitmap bitmap = (Bitmap) resource;
            Palette p = Palette.generate(bitmap);
            this.mMutedColor = p.getLightVibrantColor(this.mMutedColor);
            this.imgView.setImageBitmap(bitmap);
            this.mMetaBar.setBackgroundColor(this.mMutedColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.imgView != null) {
            scheduleStartPostponedTransition(this.imgView, ((Activity) this.ctx));
        }

        return false;
    }

    private void scheduleStartPostponedTransition(final View sharedElement, final Activity ctx) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        ctx.startPostponedEnterTransition();
                        return true;
                    }
                });
    }
}