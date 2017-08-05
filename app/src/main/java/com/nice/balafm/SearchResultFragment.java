package com.nice.balafm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.nice.balafm.model.AnchorModel;
import com.nice.balafm.model.ChannelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 23721 on 2017/7/31.
 */

public class SearchResultFragment extends Fragment{
    private FragmentManager fragmentManager;
    private TabLayout searchResultTab;
    private ViewPager searchResultViews;
    private SearchResultAnchor searchResultAnchor;
    private SearchResultChannel searchResultChannel;
    private SearchResultClassify searchResultClassify;
    private ArrayList<Fragment> fragList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_search_result,container,false);
        searchResultTab=view.findViewById(R.id.search_result_tab);
        searchResultViews=view.findViewById(R.id.search_result_view);
        searchResultChannel=new SearchResultChannel();
        searchResultAnchor=new SearchResultAnchor();
        searchResultClassify=new SearchResultClassify();
        fragList=new ArrayList<Fragment>();
        init();
        return view;
    }
    public void init(){
        fragList.add(searchResultAnchor);
        fragList.add(searchResultChannel);
        fragList.add(searchResultClassify);
        searchResultTab.setupWithViewPager(searchResultViews);;
        searchResultViews.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            private ArrayList<Fragment> frags=fragList;
            private String[] title={
                "主播","频道","分类"
            };
            @Override
            public Fragment getItem(int position) {
                return frags.get(position);
            }

            @Override
            public int getCount() {
                return frags.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return title[position];
            }
        });
    }

    public void search(String words){
        searchResultAnchor.search(words,0,10,true);
        searchResultChannel.search(words,0,10,true);
    }

    public SearchResultAnchor getSearchResultAnchor() {
        return searchResultAnchor;
    }

    public SearchResultChannel getSearchResultChannel() {
        return searchResultChannel;
    }

    public SearchResultClassify getSearchResultClassify() {
        return searchResultClassify;
    }
}




