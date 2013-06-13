package com.lecoding.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by usbuild on 13-6-13.
 */
public class CustomClickSpan extends ClickableSpan {

    Context context;
    Intent intent;

    public CustomClickSpan(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        context.startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
