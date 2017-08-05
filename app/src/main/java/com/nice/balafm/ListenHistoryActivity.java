package com.nice.balafm;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import okhttp3.Callback;
import okhttp3.Response;

public class ListenHistoryActivity extends Activity {
    private ArrayList<ChannelModel> channels;
    private RecyclerView list;
    private EditText searchString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_history);
        list=findViewById(R.id.listen_history_list);
        searchString=findViewById(R.id.listen_history_search);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(new ListenHistoryAdapter());

        searchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String word=editable.toString();
                search(word,0,13,true);
            }
        });

        search("",0,10,true);
    }

    public void search(String words,int offset,int limit,boolean reset){
        JSONObject param = new JSONObject();
        try {
            param.put("keyword",words);
            param.put("offset",offset);
            param.put("limit",limit);
            HttpUtilKt.postJsonRequest(this, GLobal.getHostAddress() + "/search/channel", param.toString()
                    , new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ListenHistoryActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ListenHistoryActivity.this,"服务器出错",Toast.LENGTH_SHORT);
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
                                        ListenHistoryActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(reset) {
                                                    ListenHistoryActivity.this.channels=clist;
                                                    ListenHistoryAdapter adapter=(ListenHistoryAdapter)list.getAdapter();
                                                    adapter.resetList(clist);
                                                    for(ChannelModel c:clist){
                                                        Log.d("LHA search","clist:"+c.id);
                                                    }
                                                    list.getAdapter().notifyItemRangeChanged(
                                                            0, clist.size());
                                                    Log.d("SearchResultChannelUI","list size:"+clist.size());
                                                }
                                            }
                                        });
                                    }else{
                                        ListenHistoryActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ListenHistoryActivity.this,"服务器结果不正常",Toast.LENGTH_SHORT);
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{
                                ListenHistoryActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ListenHistoryActivity.this,"不是要一个好JSON",Toast.LENGTH_SHORT);
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


class ListenHistoryAdapter extends RecyclerView.Adapter<ListenHistoryAdapter.ViewHolder>{
    private ArrayList<ChannelModel> channels;
    public ListenHistoryAdapter(){channels=new ArrayList<ChannelModel>();}
    public ListenHistoryAdapter(ArrayList<ChannelModel> cs){
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