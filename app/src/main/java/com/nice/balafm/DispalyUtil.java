package com.nice.balafm;

import android.content.Context;

/**
 * Created by 23721 on 2017/7/31.
 */

public class DispalyUtil {
    /**
     * px 转 dp
     */
    public static  float px2dip(Context context,float pxValue)
    {
        final float scale=context.getResources().getDisplayMetrics().density;
        return pxValue/scale;
    }

    /**
     * dp 转 px
     */

    public static float dip2px(Context context,float dipValue)
    {
        final float scale=context.getResources().getDisplayMetrics().density;
        return dipValue*scale;
    }
}
