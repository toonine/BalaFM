package com.nice.balafm;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.nice.balafm.model.ChannelModel;

import java.nio.channels.Channel;
import java.util.ArrayList;

public class OrderChannelActivity extends Activity {
    private ArrayList<ChannelModel> onLineChannels;
    private ArrayList<ChannelModel> onPrepareChannels;
    private ArrayList<ChannelModel> onSleepChannels;
    private RecyclerView onLineList;
    private RecyclerView onPrepareList;
    private RecyclerView onSleepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_channel);

        ArrayList<ChannelModel> testList=new ArrayList<ChannelModel>();
        for(int i=0;i<6;i++){
            ChannelModel c=new ChannelModel();
            c.id=111;
            c.anchor.id=222;
            c.icon="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1501918794524&di=21df804f539f5d8e22dd24885979094b&imgtype=0&src=http%3A%2F%2Fpic17.nipic.com%2F20111122%2F6759425_152002413138_2.jpg";
            c.anchor.user.icon="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502513661&di=b5e4908bebb6eac6c0e7b22f8509340c&imgtype=jpg&er=1&src=http%3A%2F%2Fwww.qqpk.cn%2FArticle%2FUploadFiles%2F201205%2F20120522121005622.jpg";
            c.listenorNum=1234;
            c.brief="雕刻机在叫滋滋滋滋";
            c.classify="#生活";
            testList.add(c);
        }
        onLineList=findViewById(R.id.order_channel_onLine);
        onPrepareList=findViewById(R.id.order_channel_onPrepare);
        onSleepList=findViewById(R.id.order_channel_onSleep);

        onLineList.setAdapter(new OrderChannelAdapter());
        onLineList.setLayoutManager(new GridLayoutManager(this,2){
            @Override
            public boolean canScrollHorizontally() { return false; }
            @Override
            public boolean canScrollVertically() { return false; }
        });

        onPrepareList.setAdapter(new OrderChannelAdapter());
        onPrepareList.setLayoutManager(new GridLayoutManager(this,2){
            @Override
            public boolean canScrollHorizontally() { return false; }
            @Override
            public boolean canScrollVertically() { return false; }
        });

        onSleepList.setAdapter(new OrderChannelAdapter());
        onSleepList.setLayoutManager(new GridLayoutManager(this,2){
            @Override
            public boolean canScrollHorizontally() { return false; }
            @Override
            public boolean canScrollVertically() { return false; }
        });

        ((OrderChannelAdapter)(onSleepList.getAdapter())).resetList(testList);
        ((OrderChannelAdapter)(onPrepareList.getAdapter())).resetList(testList);
        ((OrderChannelAdapter)(onLineList.getAdapter())).resetList(testList);

    }

}

class OrderChannelAdapter extends RecyclerView.Adapter<OrderChannelAdapter.ChannelCard>{
    public ArrayList<ChannelModel> channels;
    static class ChannelCard extends RecyclerView.ViewHolder{
        public ImageView channelIcon;
        public ImageView anchorIcon;
        public TextView brief;
        public TextView classify;
        public View menu;
        public TextView onLineListenor;
        public int aid;
        public int chid;
        public ChannelCard(View view){
            super(view);
            channelIcon=view.findViewById(R.id.order_channel_channel_icon);
            anchorIcon=view.findViewById(R.id.order_channel_anchor_icon);
            brief=view.findViewById(R.id.order_channel_brief);
            classify=view.findViewById(R.id.order_channel_classify);
            menu=view.findViewById(R.id.order_channel_menu);
            onLineListenor=view.findViewById(R.id.order_channel_onLineListenor);
        }
    }

    public OrderChannelAdapter(){
        channels=new ArrayList<ChannelModel>();
    }

    @Override
    public ChannelCard onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.area_order_channel_2,parent,false);
        return new ChannelCard(view);
    }

    @Override
    public void onBindViewHolder(ChannelCard holder, int position) {
        ChannelModel channel=channels.get(position);
        holder.aid=channel.anchor.id;
        holder.chid=channel.id;
        RequestManager glide=Glide.with(holder.anchorIcon.getContext());
        glide.load(channel.icon).into(holder.channelIcon);
        glide.load(channel.anchor.user.icon).into(holder.anchorIcon);
        holder.brief.setText(channel.brief);
        holder.classify.setText(channel.classify);
        holder.onLineListenor.setText(String.valueOf(channel.listenorNum));
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }
    public void resetList(ArrayList<ChannelModel> cs){
        channels=cs;
        notifyDataSetChanged();
    }
}
