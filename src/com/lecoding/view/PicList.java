package com.lecoding.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.loopj.android.image.SmartImageView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-19 下午3:51
 */
public class PicList extends LinearLayout {
    public PicList(Context context) {
        super(context);
    }

    public PicList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setImages(List<String> list) {
        this.removeAllViews();
        for (String s : list) {
            SmartImageView smartImageView = new SmartImageView(this.getContext());
            smartImageView.setImageUrl(s);
            smartImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.addView(smartImageView);
        }
    }
}
