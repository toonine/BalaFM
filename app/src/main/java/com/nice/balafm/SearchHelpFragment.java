package com.nice.balafm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.nice.balafm.util.HttpUtilKt;
import com.nice.balafm.util.JsonUtilKt;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 23721 on 2017/7/30.
 */

public class SearchHelpFragment extends Fragment {
    private SearchModel searchModel;
    private RecyclerView historyList;
    private FlowLayout hotWordList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_search_help,container,false);
        hotWordList=view.findViewById(R.id.hot_word);
        hotWordList.setHorizontalSpace(7);
        hotWordList.setVerticalSpace(5);
        searchModel=new SearchModel(this.getActivity(),"BalaFm.db",null,1);
        historyList=view.findViewById(R.id.historyList);
        initHistoryBar();
        initHotWord();
        return view;
    }
    public void initHistoryBar()
    {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyList.setLayoutManager(layoutManager);
        historyList.setAdapter(new SearchHistoryAdapter(this,searchModel.getHistory()));
    }
    public void search(String string)
    {
        SearchActivity activity=(SearchActivity)getActivity();
        activity.search(string);
    }
    public void setHostWord(ArrayList<String> words)
    {
        LayoutInflater inflater=getLayoutInflater();
        for(String w:words)
        {
            View view=inflater.inflate(R.layout.hot_word_layout,hotWordList,false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    search(w);
                }
            });
            TextView tv=view.findViewById(R.id.text);
            tv.setText(w);
            hotWordList.addView(view);
        }
    }
    public ArrayList<String> getHistory()
    {
        return searchModel.getHistory();
    }
    public void initHotWord()
    {
        HttpUtilKt.postJsonRequest(getContext(), HttpUtilKt.getHOST_ADDRESS() + "/fuckyou", "{}", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseJson=response.body().string();
                final ArrayList result=new ArrayList<String>();
                if(JsonUtilKt.isGoodJson(responseJson)){
                    try {
                        JSONArray jsonArray=new JSONArray(responseJson);
                        for(int i=0;i<jsonArray.length();i++) {
                            result.add((String)(jsonArray.get(i)));
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setHostWord(result);
                            }
                        });
                    }catch (JSONException je) {
                        je.printStackTrace();
                    }
                } else
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }

        });
    }
    public void removeHistory(String record)
    {
        SearchHistoryAdapter adapter=(SearchHistoryAdapter)historyList.getAdapter();
        int position=adapter.getPosition(record);
        searchModel.removeRecord(record);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position,adapter.getItemCount());
    }
    public void removeHistory(int position)
    {
        SearchHistoryAdapter adapter=(SearchHistoryAdapter)historyList.getAdapter();
        searchModel.removeRecord(adapter.getValue(position));
        adapter.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(0,adapter.getItemCount());
    }
}
class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>
{
    public SearchHelpFragment fragment;
    private ArrayList<String> historyList;
    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView historyItem;
        public ViewHolder(View view)
        {
            super(view);
            historyItem=view.findViewById(R.id.title);

        }
    }
    public SearchHistoryAdapter(SearchHelpFragment _fragment, ArrayList<String> list){
        fragment=_fragment;
        historyList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_history_bar_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                fragment.search(historyList.get(position));

            }
        });
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                fragment.removeHistory(position);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String history=historyList.get(position);
        holder.historyItem.setText(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public int getPosition(String record)
    {
        return historyList.indexOf(record);
    }
    public String getValue(int position)
    {
        return historyList.get(position);
    }
    public void remove(int position)
    {
        historyList.remove(position);
    }
}
