package com.example.tvguide.MovieProfile;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class FragmentAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;
    String media_type;

    public FragmentAdapter(Context context, FragmentManager fm, int totalTabs, String media_type) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
        if(media_type.equals("movie"))
            this.totalTabs = 2;
        this.media_type = media_type;
    }

    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Overview();
            case 1:
                if(media_type.equals("tv"))
                    return new Track();
                else
                    return new Posts();
            case 2:
                return new Posts();


            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }

}