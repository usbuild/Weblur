package com.lecoding.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
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

    View view = null;
    View.OnClickListener clickListener;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    boolean isChecked = false;

    @Override
    protected View onCreateView(ViewGroup parent) {

        if (view != null) return view;

        view = LayoutInflater.from(getContext()).inflate(
                R.layout.mycheckbox, parent, false);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setText(getTitle());
        checkBox.setChecked(isChecked);
        view.findViewById(R.id.imageView).setOnClickListener(clickListener);
        checkBox.setOnCheckedChangeListener(checkedChangeListener);
        return view;
    }

    public void setOnSetClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
