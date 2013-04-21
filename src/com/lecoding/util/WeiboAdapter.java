package com.lecoding.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lecoding.R;
import com.lecoding.data.Status;
import com.lecoding.view.PicList;
import com.lecoding.view.Retweet;
import com.loopj.android.image.SmartImageView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-13 下午2:30
 */
public class WeiboAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Status> statuses;

    public WeiboAdapter(Context context, List<Status> statuses) {
        inflater = LayoutInflater.from(context);
        this.statuses = statuses;
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int i) {
        return statuses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.weibo_item, null);

            holder.text = (TextView) view.findViewById(R.id.weibo_item_text);
            holder.profileImg = (SmartImageView) view.findViewById(R.id.profile_img);
            holder.username = (TextView) view.findViewById(R.id.weibo_item_user);
            holder.commentCount = (TextView) view.findViewById(R.id.weibo_item_comment_cnt);
            holder.repostCount = (TextView) view.findViewById(R.id.weibo_item_repost_cnt);
            holder.picList = (PicList) view.findViewById(R.id.piclist);
            holder.thumbnail = (SmartImageView) view.findViewById(R.id.thumbnail);
            holder.retweet = (Retweet) view.findViewById(R.id.retweet);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Status status = statuses.get(i);
        holder.text.setText(status.getText());
        holder.profileImg.setImageUrl(status.getUser().getProfileImageUrl());
        holder.username.setText(status.getUser().getName());
        holder.commentCount.setText("评论(" + status.getCommentsCount() + ")");
        holder.repostCount.setText("转发(" + status.getRepostsCount() + ")");
        if (status.getRetweetedStatus() != null) {
            holder.retweet.setData(status.getRetweetedStatus());
            holder.retweet.setVisibility(View.VISIBLE);
        } else {
            holder.thumbnail.setVisibility(View.GONE);
            holder.thumbnail.setImageUrl(status.getThumbnailPic());
        }
        /*
        List<PicDetail> picDetails = status.getPicDetails();
        if (picDetails.size() > 0) {
            List<String> urls = new ArrayList<String>();
            for (PicDetail picDetail : picDetails) {
                urls.add(picDetail.getThumbnailPic());
            }
            holder.picList.setImages(urls);

        }
        */
        return view;
    }

    public final class ViewHolder {
        public TextView text;
        public SmartImageView profileImg;
        public TextView username;
        public TextView commentCount;
        public TextView repostCount;
        public PicList picList;
        public SmartImageView thumbnail;
        public Retweet retweet;
    }
}
