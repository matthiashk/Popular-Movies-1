package com.mycompany.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The ImageAdapter used for the GridView.
 */

public class ImageAdapter extends BaseAdapter {

    private List<String> mImageUrls;

    private Context mContext;

    public ImageAdapter(Context context, List<String> imageUrls) {
        this.mContext = context;
        this.mImageUrls = imageUrls;
    }

    public int getCount() {
        return mImageUrls.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(params));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mImageUrls.get(position))
                .centerCrop()
                .resize(600, 900)
                .into(imageView);

        return imageView;
    }
}