package com.lecoding.util;

import com.lecoding.data.PicDetail;
import com.lecoding.data.Status;
import com.lecoding.data.User;
import com.lecoding.data.Visible;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-13 上午10:12
 */
public class JSONParser {
    private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",
            Locale.US);

    public static Status parseStatus(JSONObject jsonObject) {
        Status status = new Status();

        try {
            status.setCreateAt(sdf.parse(jsonObject.getString("created_at")));
            status.setId(jsonObject.getLong("id"));
            status.setMid(jsonObject.getString("mid"));
            status.setIdstr(jsonObject.getString("idstr"));
            status.setText(jsonObject.getString("text"));
            status.setSource(jsonObject.getString("source"));
            status.setFavorited(jsonObject.getBoolean("favorited"));
            status.setTruncated(jsonObject.getBoolean("truncated"));
            status.setInReplyToStatusId(jsonObject.getString("in_reply_to_status_id"));
            status.setInReplyToUserId(jsonObject.getString("in_reply_to_user_id"));
            status.setInReplyToScreenName(jsonObject.getString("in_reply_to_screen_name"));
            status.setThumbnailPic(jsonObject.has("thumbnail_pic") ? jsonObject.getString("thumbnail_pic") : null);
            status.setBmiddlePic(jsonObject.has("bmiddle_pic") ? jsonObject.getString("bmiddle_pic") : null);
            status.setOriginalPic(jsonObject.has("original_pic") ? jsonObject.getString("original_pic") : null);
            status.setGeo(jsonObject.getString("geo"));
            status.setUser(parseUser(jsonObject.getJSONObject("user")));
            status.setRepostsCount(jsonObject.getInt("reposts_count"));
            status.setCommentsCount(jsonObject.getInt("comments_count"));
            status.setAttitudesCount(jsonObject.getInt("attitudes_count"));
            status.setMlevel(jsonObject.getInt("mlevel"));
            status.setVisible(parseVisible(jsonObject.getJSONObject("visible")));

            JSONArray array = jsonObject.getJSONArray("pic_urls");
            List<PicDetail> picDetails = new ArrayList<PicDetail>();
            for (int i = 0; i < array.length(); ++i) {
                picDetails.add(parsePic(array.getJSONObject(i)));
            }
            status.setPicDetails(picDetails);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static Visible parseVisible(JSONObject jsonObject) {
        Visible visible = new Visible();
        try {
            visible.setType(jsonObject.getInt("type"));
            visible.setListId(jsonObject.getLong("list_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return visible;
    }

    public static User parseUser(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setId(jsonObject.getLong("id"));
            user.setIdstr(jsonObject.getString("idstr"));
            user.setScreenName(jsonObject.getString("screen_name"));
            user.setName(jsonObject.getString("name"));
            user.setProvince(jsonObject.getString("province"));
            user.setCity(jsonObject.getString("city"));
            user.setLocation(jsonObject.getString("location"));
            user.setDescription(jsonObject.getString("description"));
            user.setUrl(jsonObject.getString("url"));
            user.setProfileImageUrl(jsonObject.getString("profile_image_url"));
            user.setProfileUrl(jsonObject.getString("profile_url"));
            user.setDomain(jsonObject.getString("domain"));
            user.setWeihao(jsonObject.getString("weihao"));
            user.setGender(jsonObject.getString("gender"));
            user.setFollowersCount(jsonObject.getLong("followers_count"));
            user.setFriendsCount(jsonObject.getLong("friends_count"));
            user.setStatusesCount(jsonObject.getLong("statuses_count"));
            user.setFavouritesCount(jsonObject.getLong("favourites_count"));
            user.setCreateAt(sdf.parse(jsonObject.getString("created_at")));
            user.setFollowing(jsonObject.getBoolean("following"));
            user.setAllowAllActMsg(jsonObject.getBoolean("allow_all_act_msg"));
            user.setGeoEnabled(jsonObject.getBoolean("geo_enabled"));
            user.setVerified(jsonObject.getBoolean("verified"));
            user.setVerifiedType(jsonObject.getInt("verified_type"));
            user.setRemark(jsonObject.getString("remark"));
            user.setAllowAllComment(jsonObject.getBoolean("allow_all_comment"));
            user.setAvatarLarge(jsonObject.getString("avatar_large"));
            user.setVerifiedReason(jsonObject.getString("verified_reason"));
            user.setFollowMe(jsonObject.getBoolean("follow_me"));
            user.setOnlineStatus(jsonObject.getInt("online_status"));
            user.setBiFollowersCount(jsonObject.getInt("bi_followers_count"));
            user.setLang(jsonObject.getString("lang"));
            user.setStar(jsonObject.getInt("star"));
            user.setMbtype(jsonObject.getInt("mbtype"));
            user.setMbrank(jsonObject.getInt("mbrank"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static PicDetail parsePic(JSONObject jsonObject) {
        PicDetail picDetail = new PicDetail();
        try {
            picDetail.setThumbnailPic(jsonObject.getString("thumbnail_pic"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return picDetail;
    }
}
