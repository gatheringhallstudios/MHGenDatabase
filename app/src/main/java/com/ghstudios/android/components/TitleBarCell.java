package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TitleBarCell extends FrameLayout {

    @BindView(R.id.generic_icon)
    ImageView imageView;

    @BindView(R.id.title_text)
    TextView titleView;

    @BindView(R.id.title_alt_text)
    TextView titleAltView;

    public TitleBarCell(Context context) {
        super(context);
        init(null, "", "", false);
    }

    public TitleBarCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TitleBarCell);

        // Set values from attributes
        try {
            Drawable drawable = attributes.getDrawable(R.styleable.TitleBarCell_iconSrc);
            String titleText = attributes.getString(R.styleable.TitleBarCell_titleText);
            String titleAltText = attributes.getString(R.styleable.TitleBarCell_titleAltText);
            boolean altTitleEnabled = attributes.getBoolean(R.styleable.TitleBarCell_altTitleEnabled, false);

            init(drawable, titleText, titleAltText, altTitleEnabled);
        } finally {
            // Typed arrays should be recycled after use
            attributes.recycle();
        }
    }

    public void init(Drawable drawable, String titleText, String titleAltText, boolean altTitleEnabled) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cell_title_bar, this, true);

        ButterKnife.bind(this);

        setIconDrawable(drawable);
        setTitleText(titleText);
        setAltTitleText(titleAltText);
        setAltTitleEnabled(altTitleEnabled);
    }

    /**
     * Set custom drawable for the left icon
     */
    public void setIconDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);

        // Invalidate to trigger layout update
        invalidate();
    }

    public void setTitleText(String titleText) {
        titleView.setText(titleText);
    }

    public void setAltTitleText(String altTitleText) {
        titleAltView.setText(altTitleText);
    }

    public void setAltTitleEnabled(boolean enabled) {
        if (enabled) {
            titleAltView.setVisibility(VISIBLE);
        } else {
            titleAltView.setVisibility(GONE);
        }
    }
}
