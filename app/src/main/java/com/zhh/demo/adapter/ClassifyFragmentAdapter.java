package com.zhh.demo.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.zhh.demo.fragment.ClassifyFragment;

import java.util.List;

/**
 * Tip:
 *
 * @author zhh
 * @date 2019-12-01.
 */
public class ClassifyFragmentAdapter extends BaseFragmentAdapter<ClassifyFragment>{
    public ClassifyFragmentAdapter(FragmentManager fm, List<ClassifyFragment> fragments) {
        super(fm,fragments);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "test" + position;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if(object.getClass().getName().equals(ClassifyFragment.class.getName())){
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
