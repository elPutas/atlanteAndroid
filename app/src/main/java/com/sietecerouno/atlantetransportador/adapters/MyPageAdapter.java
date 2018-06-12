package com.sietecerouno.atlantetransportador.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.squareup.picasso.Picasso;

/**
 * Created by Gio on 10/11/17.
 */

public class MyPageAdapter extends PagerAdapter
{
    Context mContext;
    int resId = 0;
    String url;

    public MyPageAdapter(Context context ) {
        mContext = context;

    }


    public Object instantiateItem(ViewGroup collection, int position)
    {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_container_image, collection, false);
        ImageView img = (ImageView) layout.findViewById(R.id.img);

        collection.addView(layout);
        String _url ="";
        switch (position) {
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
        }
        _url=Manager.getInstance().arrPhotoThumb.get(position).toString();
        Picasso.with(mContext)
                .load(_url)
                .into(img);
        return layout;
    }

    @Override
    public int getCount() {
        return Manager.getInstance().arrPhotoThumb.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
