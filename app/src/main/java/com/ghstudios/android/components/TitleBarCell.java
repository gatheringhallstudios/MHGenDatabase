package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TitleBarCell extends FrameLayout {

    @BindView(R.id.generic_icon)
    ImageView imageView;

    @BindView(R.id.label_text)
    TextView labelView;

    public TitleBarCell(Context context, @DrawableRes int imgSrc, String labelText) {
        super(context);
        Drawable drawable = ContextCompat.getDrawable(getContext(), imgSrc);
        init(drawable, labelText);
    }

    public TitleBarCell(Context context) {
        super(context);
        init(null, "");
    }

    public TitleBarCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TitleBarCell);

        // Set values from attributes
        Drawable drawable;
        String labelText;
        try {
            drawable = attributes.getDrawable(R.styleable.TitleBarCell_iconSrc);
            labelText = attributes.getString(R.styleable.TitleBarCell_labelText);
        } finally {
            // Typed arrays should be recycled after use
            attributes.recycle();
        }

        init(drawable, labelText);
    }

    public void init(Drawable drawable, String labelText) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cell_title_bar, this, true);

        ButterKnife.bind(this);

        setIconDrawable(drawable);
        setName(labelText);
    }

    /**
     * Set custom drawable for the left icon
     */
    public void setIconDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);

        // Invalidate to trigger layout update
        invalidate();
    }

    public void setName(String labelText) {
        labelView.setText(labelText);
    }
}
