package com.nice.balafm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView search_close;
    private View search_button;
    private EditText search_text;
    private static final String TAG="SearchActivity";
    private SearchModel searchModel;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragList;
    private SearchResultFragment searchResultFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppKt.setStatusBarLightMode(this, true);
        setContentView(R.layout.active_search);
        searchModel=new SearchModel(this,"BalaFm.db",null,1);
        search_text=findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewPager.setCurrentItem(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        search_button=findViewById(R.id.search_search);
        search_close=findViewById(R.id.search_close);

        viewPager=(ViewPager)findViewById(R.id.search_fragment);
        searchResultFragment =new SearchResultFragment();
        SearchHelpFragment search_help=new SearchHelpFragment();
        fragList=new ArrayList<Fragment>();
        fragList.add(search_help);
        fragList.add(searchResultFragment);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),fragList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        SearchHelpFragment help=(SearchHelpFragment)fragList.get(0);
                        help.initHistoryBar();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        search_close.setOnClickListener(this);
        search_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_close:
                search_text.setText("");
                viewPager.setCurrentItem(0);
                break;
            case R.id.search_search:
                String searchStr=search_text.getText().toString();
                search(searchStr);
                addSearchHistory(searchStr);
        }
    }
    public void search(String text)
    {
        if(!search_text.getText().equals(text)) search_text.setText(text);
        searchResultFragment.search(text);
        viewPager.setCurrentItem(1);
    }

    public void addSearchHistory(String text) {
        searchModel.addRecord(text);
        searchModel.printAll();
        ArrayList<String> history = searchModel.getHistory();
        for (String h : history) {
            Log.d(TAG, h + " | ");
        }
    }

    public SearchResultFragment getSearchResultFragment() {
        return searchResultFragment;
    }
}

class ViewPagerAdapter extends FragmentPagerAdapter{
    private ArrayList<Fragment> pages;
    public ViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> list)
    {
        super(fm);
        pages=list;
    }
    public Fragment getItem(int position)
    {
        return pages.get(position);
    }
    public int getCount(){
        return pages.size();
    }
}