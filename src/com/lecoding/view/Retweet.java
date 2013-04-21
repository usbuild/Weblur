package com.lecoding.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.loopj.android.image.SmartImageView;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-21 上午9:25
 */
public class Retweet extends LinearLayout {
    public Retweet(Context context) {
        super(context);
    }

    public Retweet(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.retweet, this, true);
    }

    public Retweet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(Status status) {
        TextView username = (TextView) findViewById(R.id.retweet_user);
        TextView text = (TextView) findViewById(R.id.retweet_text);
        SmartImageView thumbnail = (SmartImageView) findViewById(R.id.retweet_thumbnail);
        TextView cmtCount = (TextView) findViewById(R.id.retweet_comment_cnt);
        TextView rpCount = (TextView) findViewById(R.id.retweet_repost_cnt);
        username.setText(status.getUser().getName());
        text.setText(status.getText());
        if (status.getThumbnailPic() != null) {
            thumbnail.setImageUrl(status.getThumbnailPic());
        }
        cmtCount.setText("评论(" + String.valueOf(status.getCommentsCount()) + ")");
        rpCount.setText("转发(" + String.valueOf(status.getRepostsCount()) + ")");
    }
}
