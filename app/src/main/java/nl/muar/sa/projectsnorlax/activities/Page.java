package nl.muar.sa.projectsnorlax.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import nl.muar.sa.projectsnorlax.fragments.MenuFragment;

public class Page extends FragmentStatePagerAdapter
{
    public static final int NUM_OF_PAGES = 5;

    public Page(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return new MenuFragment();
    }

    @Override
    public int getCount()
    {
        return NUM_OF_PAGES;
    }
}