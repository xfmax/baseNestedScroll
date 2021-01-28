package com.zhh.demo.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.zhh.demo.R;
import com.zhh.demo.Utils.Util;
import com.zhh.demo.iface.AppBarLayoutObserved;
import com.zhh.demo.iface.ScrollableContainer;

/**
 * Tip:
 *
 * @author zhh
 * @date 2019-12-01.
 */
public class MFCoordinatorLayout extends CoordinatorLayout{
    private final String TAG = "MFCoordinatorLayout";
    private AppBarLayoutObserved observed;
    private NestedScrollView nestedScrollView;
    private ScrollableContainer currentScrollableContainer;
    private boolean isTouchPointInBannerView;
    private boolean is = false;

    public MFCoordinatorLayout(Context context) {
        super(context);
        init();
    }

    public MFCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MFCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){ }

    public void setAppBarLayoutObserved(AppBarLayoutObserved observed){
        this.observed = observed;
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer){
        this.currentScrollableContainer = scrollableContainer;
    }

    public void setNestedScrollView(NestedScrollView nestedScrollView){
        this.nestedScrollView = nestedScrollView;
    }

    private int mLastX,mLastY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //内部拦截法
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                if(refreshView!=null){
//                    refreshView.disallowInterceptTouchEvent(true);
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if(Math.abs(deltaY) > Math.abs(deltaX)){
                    //判断触摸点是否落在banner上
                    bannerView = getBannerView();
                    if(bannerView != null){
                        isTouchPointInBannerView = Util.calcViewScreenLocation(bannerView).contains(ev.getRawX(),ev.getRawY());
                    }else{
                        isTouchPointInBannerView = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouchPointInBannerView = false;
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        Log.d(TAG,"target: " + target + ", dy: "+ dy + ", consumed: " + consumed[1]);
        //必须放在前面调用，后面对父容器消耗的dy进行处理，来解决与子元素的滑动冲突
        super.onNestedPreScroll(target, dx, dy, consumed, type);
        if(consumed[1] == 0 && !isTouchPointInBannerView){
            //AppbarLayout折叠或下滑时，consumed[1]=0,并且触摸点不在Banner上
            nsvMaxOffsetY = getNestedScrollViewMaxOffset();
            if(nsvMaxOffsetY > 0 && nestedScrollView != null){
                //NestedScrollView存在最大滑出屏幕的偏移量时，需要对dy消耗进行处理
                if(dy > 0){
                    //上滑
                    if(nsvMaxOffsetY == nestedScrollView.getScrollY()){
                        //banner隐藏时，不消耗dy，交给列表，列表滑动dy
                        consumed[1] = 0;
                    }else{
                        //banner可见
                        //触摸点在RecyclerView上时，设置父容器消耗dy,不让列表滑动，同时滚动NestedScrollView
                        consumed[1] = dy;
                        nestedScrollViewScrollBy(0, dy);
                    }
                }else{
                    //下滑
                    if(nestedScrollView.getScrollY() == nsvMaxOffsetY){
                        //banner隐藏
                        if(isTop()){
                            //列表第一个item可见，设置父容器消耗dy,不让列表滑动，同时滚动NestedScrollView
                            consumed[1] = dy;
                            nestedScrollViewScrollBy(0, dy);
                        }else{
                            //列表第一个item不可见，父容器不消耗dy，交给RecyclerView消耗dy
                            consumed[1] = 0;
                        }
                    }else if(nestedScrollView.getScrollY() > 0){
                        //banner可见未完全展开
                        //触摸点在RecyclerView上时，设置父容器消耗dy,不让列表滑动，同时滚动NestedScrollView
                        consumed[1] = dy;
                        nestedScrollViewScrollBy(0, dy);
                    }
                }
            }
        }
    }

    private void nestedScrollViewScrollBy(int x, int y){
        if(nestedScrollView!=null){
            int mScrollY = nestedScrollView.getScrollY();
            int desY = y + mScrollY;
            desY = desY>=nsvMaxOffsetY?nsvMaxOffsetY:(desY<=0?0:desY);
            nestedScrollView.scrollTo(x,desY);
        }
    }

    /**
     * 允许NestedScrollView划出屏幕的最大距离
     */
    private int nsvMaxOffsetY;
    private int getNestedScrollViewMaxOffset(){
        if(nestedScrollView !=null && nestedScrollView.getChildCount()>0 && nsvMaxOffsetY == 0){
            View child = nestedScrollView.getChildAt(0);
            if(child instanceof VerticalLinearLayout){
                nsvMaxOffsetY = ((VerticalLinearLayout) child).getMaxOffsetY();
            }
        }
        return nsvMaxOffsetY;
    }

    public boolean isTop(){
        View scrollableView = getScrollableView();
        if(scrollableView == null){
            return true;
        }
        if(scrollableView instanceof RecyclerView){
            return isRecyclerViewTop((RecyclerView) scrollableView);
        }

        return true;
    }

    private View getScrollableView(){
        if(currentScrollableContainer == null){
            return null;
        }
        return currentScrollableContainer.getScrollableView();
    }

    private boolean isRecyclerViewTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition <= 1 && childAt.getTop() == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private LinearLayout bannerView;

    private LinearLayout getBannerView(){
        if(bannerView == null && nestedScrollView!=null && nestedScrollView.getChildCount()>0){
            View child = nestedScrollView.getChildAt(0);
            if(child instanceof VerticalLinearLayout){
                VerticalLinearLayout verticalLinearLayout = (VerticalLinearLayout) child;
                if(verticalLinearLayout.getChildCount()>0 && verticalLinearLayout.getChildAt(0) instanceof LinearLayout){
                    bannerView =  (LinearLayout) verticalLinearLayout.getChildAt(0);
                }
            }
        }
        return bannerView;
    }
}
