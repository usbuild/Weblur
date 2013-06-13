package com.lecoding.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.*;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.lecoding.R;
import com.lecoding.activity.AccountActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usbuild on 13-6-13.
 */
public class WeiboText extends TextView {
    static Map<Pattern, Integer> faceMap = null;


    public WeiboText(Context context) {
        super(context);
    }

    public WeiboText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (faceMap == null) {
            faceMap = new HashMap<Pattern, Integer>();
            for (String str : getResources().getStringArray(R.array.faces)) {
                String[] kv = str.split("\\|");
                faceMap.put(Pattern.compile("\\[" + kv[0] + "\\]"),
                        getResources().getIdentifier(kv[1], "drawable", getContext().getPackageName()));

            }
        }
    }

    public WeiboText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        // dealing with at
        if (!TextUtils.isEmpty(text)) {

            String origin = text.toString();
            origin = origin.replaceAll("(http://[-\\w\\./]+)", "<a href=\"$1\">$1</a>")
                    .replaceAll("(@[-\\w]+)", "<a href=\"$1\">$1</a>")
                    .replaceAll("(#[^#]+#)", "<a href=\"$1\">$1</a>")
            ;
            Spanned spanned = Html.fromHtml(origin);


            SpannableStringBuilder ssb = new SpannableStringBuilder(spanned);

            for (Map.Entry<Pattern, Integer> entry : faceMap.entrySet()) {
                Matcher matcher = entry.getKey().matcher(ssb);
                while (matcher.find()) {
                    boolean set = true;
                    for (ImageSpan span : ssb.getSpans(matcher.start(),
                            matcher.end(), ImageSpan.class))
                        if (ssb.getSpanStart(span) >= matcher.start()
                                && ssb.getSpanEnd(span) <= matcher.end())
                            ssb.removeSpan(span);
                        else {
                            set = false;
                            break;
                        }
                    if (set) {
                        ssb.setSpan(new ImageSpan(getContext(), entry.getValue()),
                                matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }


            URLSpan[] spans = ssb.getSpans(0, ssb.length(), URLSpan.class);
            for (URLSpan span : spans) {
                int start = ssb.getSpanStart(span);
                int end = ssb.getSpanEnd(span);
                int flags = ssb.getSpanFlags(span);

                final String url = span.getURL();

                ClickableSpan sp = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {

                        if (url.charAt(0) == '@') {
                            Intent intent = new Intent(getContext(), AccountActivity.class);
                            intent.putExtra("name", url.substring(1));
                            getContext().startActivity(intent);
                        } else if (url.charAt(0) == '#') {

                        } else {
                            getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.lightblue));
                        ds.setUnderlineText(false);
                    }
                };
                ssb.removeSpan(span);
                ssb.setSpan(sp, start, end, flags);
            }
            super.setText(ssb, type);

        } else {
            super.setText(text, type);
        }
    }
}
