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

public class ImageAdapter extends BaseAdapter {

    private List<String> imageUrls;

    private Context mContext;

    public ImageAdapter(Context context, List<String> imageUrls) {

        this.mContext = context;
        this.imageUrls = imageUrls;
    }

    public int getCount() {
        return imageUrls.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        //convertView.setLayoutParams(new GridView.LayoutParams(params));

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(params));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(imageUrls.get(position))
                //.placeholder(R.drawable.weather)
                .centerCrop()
                .resize(400, 400)
                .into(imageView);

        return imageView;
    }

    public List<String> getUriList(){
        return imageUrls;
    }

}