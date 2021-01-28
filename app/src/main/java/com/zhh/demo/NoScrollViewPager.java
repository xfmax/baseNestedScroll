package com.zhh.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Tip:
 *
 * @author zhh
 * @date 2019-12-01.
 */
public class NoScrollViewPager extends ViewPager {
    private boolean scrollable = false; // false 禁止ViewPager左右滑动

    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scrollable;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scrollable;
    }
}