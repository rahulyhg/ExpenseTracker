package com.expensetracker.expensetracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Mitu on 09-Jun-15.
 */
public class ReportViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ReportViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    public ReportViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment tab = null;
        switch (position) {
            case 0:
                tab = new ReportFragmentTab1();
                //ReportFragmentTab1 tab1 = new ReportFragmentTab1();
                //return tab1;
                break;
            case 1:
                tab = new ReportFragmentTab2();
                //ReportFragmentTab2 tab2 = new ReportFragmentTab2();
                //return tab2;
                break;
            case 2:
                tab = new ReportFragmentTab3();
                //ReportFragmentTab3 tab3 = new ReportFragmentTab3();
                //return tab3;
                break;
        }
        return tab;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
