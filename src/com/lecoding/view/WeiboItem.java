package com.lecoding.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.lecoding.R;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-12 下午7:17
 */
public class WeiboItem extends LinearLayout {
    public WeiboItem(Context context) {
        super(context);
    }

    public WeiboItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weibo_item, this, true);
    }

}
