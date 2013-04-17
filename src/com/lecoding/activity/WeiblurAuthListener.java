package com.lecoding.activity;

import android.os.Bundle;
import android.widget.Toast;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-10 下午9:12
 */
public class WeiblurAuthListener implements WeiboAuthListener {
    @Override
    public void onComplete(Bundle bundle) {
        String token = bundle.getString("access_token");
        String expiresIn = bundle.getString("expires_in");
        BaseActivity.token = new Oauth2AccessToken(token, expiresIn);
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Toast.makeText(BaseActivity.activity, "Auth cancel",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(WeiboDialogError weiboDialogError) {
        Toast.makeText(BaseActivity.activity,
                "Auth error : " + weiboDialogError.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(BaseActivity.activity, "Auth cancel",
                Toast.LENGTH_LONG).show();
    }
}
