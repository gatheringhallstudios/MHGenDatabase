package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.ITintedIcon;
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
        init(null, "", "");
    }

    public TitleBarCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TitleBarCell);

        // Set values from attributes
        try {
            Drawable drawable = attributes.getDrawable(R.styleable.TitleBarCell_iconSrc);
            String titleText = attributes.getString(R.styleable.TitleBarCell_titleText);
            String titleAltText = attributes.getString(R.styleable.TitleBarCell_titleAltText);

            init(drawable, titleText, titleAltText);
        } finally {
            // Typed arrays should be recycled after use
            attributes.recycle();
        }
    }

    public void init(Drawable drawable, String titleText, String titleAltText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.cell_title_bar, this, true);

        ButterKnife.bind(this);

        setIconDrawable(drawable);
        setTitleText(titleText);
        setAltTitleText(titleAltText);
    }

    /**
     * Set custom drawable for the left icon
     */
    public void setIconDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);

        // Invalidate to trigger layout update
        invalidate();
    }

    /**
     * Set custom drawable for the left icon generated via the ITintedIcon interface.
     */
    public void setIcon(ITintedIcon icon){
        AssetLoader.setIcon(imageView, icon);
    }

    public void setIconResource(@DrawableRes int resId) {
        imageView.setImageResource(resId);

        // Invalidate to trigger layout update
        invalidate();
    }

    /**
     * Sets the main title of the title bar.
     * @param titleText
     */
    public void setTitleText(String titleText) {
        titleView.setText(titleText);
    }

    /**
     * Sets the alt title. This is usually the japanese name.
     * @param altTitleText
     */
    public void setAltTitleText(String altTitleText) {
        titleAltView.setText(altTitleText);
        maybeEnableAltText();
    }

    /**
     * Runs some logic to see if alt text should be enabled.
     */
    private void maybeEnableAltText() {
        CharSequence altText = titleAltView.getText();
        if (altText != null && altText.length() > 0) {
            titleAltView.setVisibility(VISIBLE);
        } else {
            titleAltView.setVisibility(GONE);
        }
    }
}
