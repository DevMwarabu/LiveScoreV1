package com.laxco.livescorev1.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.laxco.livescorev1.Fragments.FixtureInformation.LiveInfo;
import com.laxco.livescorev1.Fragments.MainFrags.FragCal;
import com.laxco.livescorev1.Fragments.MainFrags.FragDashboard;
import com.laxco.livescorev1.Fragments.MainFrags.FragToday;
import com.laxco.livescorev1.Fragments.MainFrags.NextDate;
import com.laxco.livescorev1.Fragments.MainFrags.PrevDate;
import com.laxco.livescorev1.Fragments.FixtureInformation.LineUps;
import com.laxco.livescorev1.Fragments.FixtureInformation.Stats;

/**
 * Created by mwarabu on 3/29/2019.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    int type;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, int type) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        switch (type){
            case 0:
                switch (position) {
                    case 0:
                        Stats stats = new Stats();
                        return stats;
                    case 1:
                        LiveInfo liveInfo = new LiveInfo();
                        return liveInfo;
                    default:
                        LineUps lineUps = new LineUps();
                        return lineUps;
                }
            default:
                switch (position) {
                    case 0:
                        FragDashboard fragDashboard = new FragDashboard();
                        return fragDashboard;
                    case 1:
                        PrevDate prevDate = new PrevDate();
                        return prevDate;
                    case 2:
                        FragToday fragToday = new FragToday();
                        return fragToday;
                    case 3:
                        NextDate nextDate = new NextDate();
                        return nextDate;
                    default:
                        FragCal fragCal = new FragCal();
                        return fragCal;
                }

        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

