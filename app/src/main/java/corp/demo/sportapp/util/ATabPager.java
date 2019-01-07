/*
 * Created by omrobbie.
 * Copyright (c) 2018. All rights reserved.
 * Last modified 11/11/17 3:14 PM.
 */

package corp.demo.sportapp.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import corp.demo.sportapp.feature.FavoriteFragment;
import corp.demo.sportapp.feature.NowPlayingFragment;
import corp.demo.sportapp.feature.UpcomingFragment;

/**
 * Created by omrobbie on 09/10/2017.
 */

public class ATabPager extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 3;

    public ATabPager(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NowPlayingFragment();

            case 1:
                return new UpcomingFragment();

            case 2:
                return new FavoriteFragment();

            default:
                return null;
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
