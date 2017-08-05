package com.nice.balafm.model;

/**
 * Created by 23721 on 2017/7/31.
 */

public class ChannelModel
{
    public int id;
    public String brief;
    public String classify;
    public String icon;
    public int listenorNum;
    public AnchorModel anchor=new AnchorModel();
    public String startTime;
}
