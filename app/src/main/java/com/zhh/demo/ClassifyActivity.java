package com.zhh.demo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhh.demo.adapter.ClassifyFragmentAdapter;
import com.zhh.demo.fragment.ClassifyFragment;
import com.zhh.demo.iface.AppBarLayoutObserved;
import com.zhh.demo.view.MFCoordinatorLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Tip:
 *
 * @author sufeng
 * @date 2019-12-01.
 */
public class ClassifyActivity extends AppCompatActivity implements AppBarLayoutObserved {
    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_EXPANDED = 1;
    public static final int STATUS_COLLAPSED = 2;

    @BindView(R.id.appbarlayout)
    AppBarLayout appbarlayout;
    @BindView(R.id.sticky_header)
    RelativeLayout llHeader;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_icon)
    TextView tvIcon;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.smartTabLayout)
    SmartTabLayout smartTabLayout;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.coordinatorLayout)
    MFCoordinatorLayout coordinatorLayout;
    @BindView(R.id.buttonBack)
    Button button;
    private int headHeight, iconSize,backIcon;
    private int maxHeadTopHeight,minHeadTopHeight;
    private int mStatus = STATUS_EXPANDED;//默认展开状态
    private List<ClassifyFragment> fragments;
    private ClassifyFragmentAdapter fragmentAdapter;
    private List<String> tabs;
    private int lastVerticalOffset = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_classify);
        ButterKnife.bind(this);
        preData();
        initView();
        initData();
        initListener();
    }

    private void preData(){
        tabs = new ArrayList<>();
        tabs.add("水果");
        tabs.add("蔬菜");
        tabs.add("生鲜");
        tabs.add("超市");
        fragments = new ArrayList<>();
        for(int i = 0; i < tabs.size(); i++){
            ClassifyFragment fragment = ClassifyFragment.newInstance(tabs.get(i));
            fragment.setIndex(i);
            fragments.add(fragment);
        }
    }

    private void initView() {
        fragmentAdapter = new ClassifyFragmentAdapter(getSupportFragmentManager(),fragments);
        viewpager.setAdapter(fragmentAdapter);
        coordinatorLayout.setCurrentScrollableContainer(fragments.get(0));
        smartTabLayout.setCustomTabView(new MyTabProvider());
        smartTabLayout.setViewPager(viewpager);
    }

    private void initData(){
        headHeight = (int) this.getResources().getDimension(R.dimen.headHeight);
        minHeadTopHeight = (int) this.getResources().getDimension(R.dimen.minHeadTopHeight);
        maxHeadTopHeight = (int) this.getResources().getDimension(R.dimen.maxHeadTopHeight);
        iconSize = (int) this.getResources().getDimension(R.dimen.imageIcon);
        backIcon = (int) this.getResources().getDimension(R.dimen.backIcon);
    }

    private void initListener(){
        appbarlayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //verticalOffset 向上滑动值为负，初始值为0即展开状态
                //剩下未滑出屏幕的高度
                int h = headHeight + verticalOffset ;
                if(verticalOffset == 0){
                    mStatus = STATUS_EXPANDED;
                }else if(h == minHeadTopHeight){
                    mStatus = STATUS_COLLAPSED;
                }else{
                    mStatus = STATUS_DEFAULT;
                }

                if(lastVerticalOffset != verticalOffset){
                    if(h <= maxHeadTopHeight){
                        if(h >= minHeadTopHeight){
                            int mMaxOffSet = maxHeadTopHeight - minHeadTopHeight;
                            int mOffset = h - minHeadTopHeight;
                            float mRate = (1.0f * mOffset) / mMaxOffSet;
                            //Icon新高度
                            int iconHeight = (int) (iconSize * mRate);
                            ivIcon.setAlpha(1 * mRate);
                            ivIcon.getLayoutParams().height = iconHeight;
                            int backHeight = (int) (backIcon * mRate);
                            button.setAlpha(1 * mRate);
                            button.getLayoutParams().height = backHeight;
                        }
                        llHeader.requestLayout();
                    }else{
                        button.setAlpha(1.0f);
                        ivIcon.setAlpha(1.0f);
                        ivIcon.getLayoutParams().height = iconSize;
                        llHeader.requestLayout();
                    }
                    lastVerticalOffset = verticalOffset;
                }
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                coordinatorLayout.setCurrentScrollableContainer(fragments.get(position));
            }
        });
        coordinatorLayout.setAppBarLayoutObserved(this);
        coordinatorLayout.setNestedScrollView(nestedScrollView);
    }

    public List<String> getListData(String name, int count){
        List<String> listData = new ArrayList<>();
        for(int i=0;i<count;i++){
            listData.add(name + "Item - "+i);
        }
        return listData;
    }


    @Override
    public int getAppBarLayoutStatus() {
        return mStatus;
    }

    private class MyTabProvider implements SmartTabLayout.TabProvider{
        private LayoutInflater inflater;

        public MyTabProvider(){
            inflater = LayoutInflater.from(ClassifyActivity.this);
        }

        @Override
        public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
            View view = inflater.inflate(R.layout.category_tab,container,false);
            TextView textView = view.findViewById(R.id.tvTabText);
            textView.setText(tabs.get(position));
            return view;
        }
    }
}
