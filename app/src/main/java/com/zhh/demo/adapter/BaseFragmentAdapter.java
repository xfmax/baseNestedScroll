package com.zhh.demo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Tip:
 *
 * @author zhh
 * @date 2019-12-01.
 */
public abstract class BaseFragmentAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    protected List<T> fragments;

    public BaseFragmentAdapter(FragmentManager fm, List<T> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments != null && position < fragments.size() ? fragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }
}
