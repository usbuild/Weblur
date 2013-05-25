package com.lecoding.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lecoding.R;
import com.lecoding.data.User;
import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;

import java.util.List;

/**
 * Created by usbuild on 13-5-24.
 */
public class UserAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<User> users;
    private Context context;

    public UserAdapter(Context context, List<User> user) {
        inflater = LayoutInflater.from(context);
        this.users = user;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
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
            view = inflater.inflate(R.layout.userlist_item, null);

            holder.profileImage = (SmartImageView) view.findViewById(R.id.profile_img);
            holder.screenName = (TextView) view.findViewById(R.id.screen_name);
            holder.gender = (TextView) view.findViewById(R.id.gender);
            holder.location = (TextView) view.findViewById(R.id.location);
            holder.weiboCnt = (TextView) view.findViewById(R.id.weibo_cnt);
            holder.followCnt = (TextView) view.findViewById(R.id.follow_cnt);
            holder.fansCnt = (TextView) view.findViewById(R.id.fans_cnt);
            holder.description = (TextView) view.findViewById(R.id.description);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final User user = this.users.get(i);
        holder.profileImage.setImageUrl(user.getProfileImageUrl());
        holder.screenName.setText(user.getScreenName());
        if (user.getGender().equals("m")) {
            holder.gender.setText("男");
        } else if (user.getGender().equals("f")) {
            holder.gender.setText("女");
        } else {
            holder.gender.setText("未知");
        }
        holder.location.setText(user.getLocation());
        holder.weiboCnt.setText(user.getStatusesCount() + "微博");
        holder.followCnt.setText(user.getFriendsCount() + "关注");
        holder.fansCnt.setText(user.getFollowersCount() + "粉丝");
        holder.description.setText("");
        return view;
    }

    public final class ViewHolder {
        public SmartImageView profileImage;
        public TextView screenName;
        public TextView gender;
        public TextView location;
        public TextView weiboCnt;
        public TextView fansCnt;
        public TextView followCnt;
        public TextView description;


    }
}
