package com.lecoding.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lecoding.R;
import com.lecoding.activity.AccountActivity;
import com.lecoding.data.Comment;
import com.loopj.android.image.SmartImageView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-25 下午3:03
 */
public class CommentAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        inflater = LayoutInflater.from(context);
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.comment_item, null);
            holder.username = (TextView) view.findViewById(R.id.cmt_username);
            holder.content = (TextView) view.findViewById(R.id.cmt_content);
            holder.profileImage = (SmartImageView) view.findViewById(R.id.cmt_profile_img);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Comment comment = comments.get(i);
        holder.username.setText(comment.getUser().getName());
        holder.content.setText(comment.getText());
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        holder.profileImage.setImageUrl(comment.getUser().getProfileImageUrl());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AccountActivity.class);
                intent.putExtra("uid", comment.getUser().getId());
                view.getContext().startActivity(intent);
            }
        };
        holder.profileImage.setOnClickListener(listener);
        holder.username.setOnClickListener(listener);
        return view;
    }

    private static class ViewHolder {
        public TextView username;
        public TextView content;
        public SmartImageView profileImage;
    }
}
