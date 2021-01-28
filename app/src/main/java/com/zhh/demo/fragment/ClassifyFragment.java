package com.zhh.demo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhh.demo.ClassifyActivity;
import com.zhh.demo.R;
import com.zhh.demo.adapter.ListAdapter;
import com.zhh.demo.iface.ScrollableContainer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Tip:
 *
 * @author zhh
 * @date 2019-12-01.
 */
public class ClassifyFragment extends Fragment implements ScrollableContainer {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private ClassifyActivity mActivity;
    private ListAdapter adapter;
    private int index;
    private GridLayoutManager gridLayoutManager;
    private static final String NAME = "NAME";

    public static ClassifyFragment newInstance(String name){
        ClassifyFragment fragment = new ClassifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NAME, name);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            this.mActivity = (ClassifyActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            this.mActivity = (ClassifyActivity) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nested_scroll,null);
        ButterKnife.bind(this,view);
        initView(getArguments().getString(NAME));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {

    }

    private void initView(String name) {
        gridLayoutManager = new GridLayoutManager(mActivity,1);
        recyclerview.setLayoutManager(gridLayoutManager);
        adapter = new ListAdapter(mActivity,mActivity.getListData(name, (index+1)*30));
        recyclerview.setAdapter(adapter);
    }

    public void setIndex(int index){
        this.index = index;
    }

    @Override
    public View getScrollableView() {
        return recyclerview;
    }
}
