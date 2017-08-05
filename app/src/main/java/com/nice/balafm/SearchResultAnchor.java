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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nice.balafm.model.AnchorModel;
import com.nice.balafm.util.HttpUtilKt;
import com.nice.balafm.util.JsonUtilKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 23721 on 2017/8/1.
 */

public class SearchResultAnchor extends Fragment {
    private RecyclerView anchorListView;
    private ArrayList<AnchorModel> anchorList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_search_anchor,container,false);
        anchorListView=view.findViewById(R.id.search_anchor_list);
        anchorList =new ArrayList<AnchorModel>();
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        anchorListView.setLayoutManager(layoutManager);
        anchorListView.setAdapter(new AnchorResultAdapter(anchorList));
        return view;
    }
    public void search(String words,int offset,int limit,boolean reset){
        JSONObject param = new JSONObject();
        try {
            param.put("keyword",words);
            param.put("offset",offset);
            param.put("limit",limit);
            HttpUtilKt.postJsonRequest(getActivity(), GLobal.getHostAddress() + "/search/anchor", param.toString()
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
                            Log.d("SRA search","body is====>>>>>>>>"+body);
                            if(JsonUtilKt.isGoodJson(body)){
                                try {
                                    JSONObject obj=new JSONObject(body);
                                    if(obj.getInt("result")==0){
                                        JSONArray rows=obj.getJSONArray("rows");
                                        ArrayList<AnchorModel> alist=new ArrayList<AnchorModel>();
                                        for (int i=0;i<rows.length();i++){
                                            AnchorModel a=new AnchorModel();
                                            JSONObject r=(JSONObject)rows.get(i);
                                            a.id=r.getInt("aid");
                                            a.user.id=r.getInt("user");
                                            a.fans=r.getInt("fans");
                                            a.user.name=r.getString("name");
                                            a.user.icon=r.getString("icon");
                                            alist.add(a);
                                        }
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(reset) {
                                                    SearchResultAnchor.this.anchorList =alist;
                                                    AnchorResultAdapter adapter=(AnchorResultAdapter)(anchorListView.getAdapter());
                                                    adapter.resetList(alist);
                                                    anchorListView.getAdapter().notifyItemRangeChanged(
                                                            0, alist.size());
                                                    Log.d("SearchResultAnchorUI","list size:"+alist.size());
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


class AnchorResultAdapter extends RecyclerView.Adapter<AnchorResultAdapter.ViewHolder>{
    private ArrayList<AnchorModel> anchors;
    public AnchorResultAdapter(ArrayList<AnchorModel> anchorList)
    {
        anchors=anchorList;
    }
    static  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public CircleImageView icon;
        public TextView fans;
        public TextView sign;
        public int uid;
        public int aid;
        public ViewHolder(View view)
        {
            super(view);
            name=view.findViewById(R.id.search_anchor_name);
            icon=view.findViewById(R.id.search_anchor_icon);
            fans=view.findViewById(R.id.search_anchor_num);
            sign=view.findViewById(R.id.search_anchor_sign);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.bar_search_anchor,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AnchorModel anchor = anchors.get(position);
        holder.name.setText(anchor.user.name);
        holder.fans.setText(String.valueOf(anchor.fans));
        holder.sign.setText(anchor.user.sign);
        holder.uid=anchor.user.id;
        holder.aid=anchor.id;
        if(!anchor.user.icon.equals(""))Glide.with(holder.icon.getContext()).load(anchor.user.icon).into(holder.icon);
    }
    @Override
    public int getItemCount() {
        return anchors.size();
    }

    public void resetList(ArrayList<AnchorModel> as){
        anchors=as;
    }
}


