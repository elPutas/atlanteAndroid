package com.sietecerouno.atlantetransportador.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.sietecerouno.atlantetransportador.fragments.ProfileFragment;

import java.util.ArrayList;

/**
 * Created by gio on 5/9/16.
 */
public class ViewPagerAdaptor extends FragmentPagerAdapter
{
    ArrayList<Fragment> fragmentes = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();
    ArrayList<Integer> tabIcon = new ArrayList<>();

    Drawable myDrawable;
    Context context;

    private FragmentManager fragMan;
    private ArrayList<Fragment> fragments=new ArrayList<Fragment>();

    public void addFragmentes(Fragment fragments, String titles, int icon)
    {
        this.fragmentes.add(fragments);
        this.tabTitles.add(titles);
        this.tabIcon.add(icon);
    }

    public ViewPagerAdaptor(FragmentManager fm, Context _context)
    {
        super(fm);
        context = _context;
    }


    @Override
    public Fragment getItem(int position)
    {

        return fragmentes.get(position);
    }

    @Override
    public int getCount() {
        return fragmentes.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        //myDrawable.setBounds(0, 0, (int) (myDrawable.getIntrinsicWidth()), (int) (myDrawable.getIntrinsicHeight()));

        SpannableString sb = new SpannableString(" ");
        try {
            myDrawable = ResourcesCompat.getDrawable(context.getResources(), tabIcon.get(position), null);
            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth()*2, myDrawable.getIntrinsicHeight()*2);
            //myDrawable.setBounds(0, 0, (int) (myDrawable.getIntrinsicWidth()/2), (int) (myDrawable.getIntrinsicHeight()/2));
            ImageSpan imageSpan = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        catch (Exception e)
        {
            Log.i("GIO", String.valueOf(e));
        }
        return sb;
    }



    public void clearAll() //You can clear any specified page if you want...
    {

        fragMan.beginTransaction().remove(new ProfileFragment()).commit();

    }

    public void addList() //Add some new fragment...
    {

    }

}
