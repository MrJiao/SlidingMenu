package com.jackson.commonutillib;

import android.util.Log;

/**
 * Created by jackson on 2017/4/9.
 */

public class L {
    private static final String TAG="JYB";
    private static final StringBuilder sb = new StringBuilder();
    public static void e(Object... msg){
        sb.setLength(0);
        for (Object s :
                msg) {
            sb.append(String.valueOf(s));
            sb.append(" ");
        }
        Log.e(TAG,sb.toString());
    }
}
