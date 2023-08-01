package com.ambabovpn.pro.view;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.ambabovpn.pro.R;

public class CenteredToolBar extends Toolbar {

    private TextView centeredTitleTextView;

    public CenteredToolBar(Context context) {
        super(context);
    }
    public CenteredToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public CenteredToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void setTitle(@StringRes int resId) {
        String s = getResources().getString(resId);
        setTitle(s);
    }
    @Override
    public void setTitle(CharSequence title) {
        getCenteredTitleTextView().setText(title);
    }
    @Override
    public void setTitleTextColor(int color) {
        getCenteredTitleTextView().setTextColor(color);
    }
    @Override
    public CharSequence getTitle() {
        return getCenteredTitleTextView().getText().toString();
    }
    public void setTypeface(Typeface font) {
        getCenteredTitleTextView().setTypeface(font);
    }
    private TextView getCenteredTitleTextView() {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = new TextView(getContext());
            //  centeredTitleTextView.setTypeface(...);
            centeredTitleTextView.setSingleLine();
            centeredTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            centeredTitleTextView.setGravity(Gravity.CENTER);
            centeredTitleTextView.setTextAppearance(getContext(),
                    R.style.Toolbar_TitleText);
            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            centeredTitleTextView.setLayoutParams(lp);
            addView(centeredTitleTextView);
        }
        return centeredTitleTextView;
    }
}




