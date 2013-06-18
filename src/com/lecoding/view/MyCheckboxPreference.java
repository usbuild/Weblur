package com.lecoding.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.lecoding.R;

/**
 * Created by usbuild on 13-6-18.
 */
public class MyCheckboxPreference extends Preference {
    public MyCheckboxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyCheckboxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCheckboxPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return View.inflate(getContext(), R.layout.mycheckbox, parent);
    }
}
