package com.mapchat.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.mapchat.chat.activity.SendMsgWeb;
import com.mapchat.chat.adapter.ListViewAdapter;
import com.mapchat.chat.adapter.OneAdapter;
import com.mapchat.chat.adapter.TwoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by 10714 on 2018/4/1.
 */

public class WebFragment extends BaseFragment {


    private ListViewAdapter listAdapter;
    private OneAdapter oneAdapter;
    private TwoAdapter twoAdapter;
    private RecyclerView recyclerView;
    private List<AVObject> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.web_fragment, container, false);
        ButterKnife.bind(this, view);
        recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerView);
        //listAdapter = new ListViewAdapter(getActivity().getApplicationContext());
        oneAdapter = new OneAdapter(getActivity().getApplicationContext());
        twoAdapter = new TwoAdapter(getActivity().getApplicationContext());

        recyclerView.setHasFixedSize(true);
       // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        listAdapter = new ListViewAdapter(mList,getActivity().getApplicationContext());
       // recyclerView.setAdapter(listAdapter);

        FloatingActionButton myFab = (FloatingActionButton)view.findViewById(R.id.myfab);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("项目一"));
        tabLayout.addTab(tabLayout.newTab().setText("项目二"));
        tabLayout.addTab(tabLayout.newTab().setText("项目三"));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {//ListView效果
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(listAdapter);
                }
                if (tab.getPosition() == 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(oneAdapter);
                }
                if (tab.getPosition() == 2) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(twoAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAdapter);

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(),SendMsgWeb.class);
                startActivity(intent);
            }
        });


        return view;
        //return view;
    }

    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        headerLayout.showTitle("网页");
    }

    @Override
    public void onResume() {
        super.onResume();
        //AVAnalytics.onResume(this);
        initData();
    }

    private void initData() {
        mList.clear();
        AVQuery<AVObject> avQuery = new AVQuery<>("Product");
        avQuery.orderByDescending("createdAt");
        avQuery.include("owner");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mList.addAll(list);
                    Log.d(TAG, "done: "+mList);
                    listAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}

























