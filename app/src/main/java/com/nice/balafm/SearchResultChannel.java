package com.nice.balafm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nice.balafm.model.ChannelModel;
import com.nice.balafm.util.HttpUtilKt;
import com.nice.balafm.util.JsonUtilKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 23721 on 2017/8/1.
 */

public class SearchResultChannel extends Fragment {
    private RecyclerView list;
    private ArrayList<ChannelModel> channels;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_search_channel,container,false);
        list=view.findViewById(R.id.search_channel_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        Log.d("SRC onCreateView","list_layout:"+list.getLayoutManager().toString());
        channels=new ArrayList<ChannelModel>();
        list.setAdapter(new ChannelResutlAdapter(channels));
        return view;
    }
    public void search(String words,int offset,int limit,boolean reset){
        JSONObject param = new JSONObject();
        try {
            param.put("keyword",words);
            param.put("offset",offset);
            param.put("limit",limit);
            HttpUtilKt.postJsonRequest(getActivity(), GLobal.getHostAddress() + "/search/channel", param.toString()
                    , new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"服务器出错",Toast.LENGTH_SHORT);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body=response.body().string();
                            Log.d("SearchResultChannel","body is====>>>>>>>>"+body);
                            if(JsonUtilKt.isGoodJson(body)){
                                try {
                                    JSONObject obj=new JSONObject(body);
                                    if(obj.getInt("result")==0){
                                        JSONArray rows=obj.getJSONArray("rows");
                                        ArrayList<ChannelModel> clist=new ArrayList<ChannelModel>();
                                        for (int i=0;i<rows.length();i++){
                                            ChannelModel c=new ChannelModel();
                                            JSONObject r=(JSONObject)rows.get(i);
                                            c.id=r.getInt("chid");
                                            c.anchor.user.name=r.getString("anchorName");
                                            c.classify=r.getString("class");
                                            c.brief=r.getString("brief");
                                            c.icon=r.getString("icon");
                                            c.listenorNum=r.getInt("onlineListener");
                                            clist.add(c);
                                        }
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(reset) {
                                                    SearchResultChannel.this.channels=clist;
                                                    ChannelResutlAdapter adapter=(ChannelResutlAdapter)list.getAdapter();
                                                    adapter.resetList(clist);
                                                    list.getAdapter().notifyItemRangeChanged(
                                                            0, clist.size());
                                                    Log.d("SearchResultChannelUI","list size:"+clist.size());
                                                }
                                            }
                                        });
                                    }else{
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"服务器结果不正常",Toast.LENGTH_SHORT);
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),"不是要一个好JSON",Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

class ChannelResutlAdapter extends RecyclerView.Adapter<ChannelResutlAdapter.ViewHolder>{
    private ArrayList<ChannelModel> channels;
    public ChannelResutlAdapter(ArrayList<ChannelModel> cs){
        channels=cs;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView brief;
        ImageView icon;
        TextView num;
        TextView comment;
        TextView classify;
        public ViewHolder(View view){
            super((view));
            brief=view.findViewById(R.id.search_channel_name);
            icon=view.findViewById(R.id.search_channel_icon);
            num=view.findViewById(R.id.search_channel_onlineListener);
            comment=view.findViewById(R.id.search_channel_comment);
            classify=view.findViewById(R.id.search_channel_classify);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.bar_search_channel,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChannelModel channel=channels.get(position);
        holder.brief.setText(channel.brief);
        holder.classify.setText(channel.classify);
        holder.comment.setText(channel.startTime);
        holder.num.setText(String.valueOf(channel.listenorNum));
        Glide.with(holder.icon.getContext()).load(channel.icon).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }
    public void resetList(ArrayList<ChannelModel> cs){
        channels=cs;
    }
    public void add(ChannelModel channelModel,int pos){
        channels.add(pos,channelModel);
    }
    public void add(ArrayList<ChannelModel> list,int pos){
        channels.addAll(pos,list);
    }
}
