package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.ITintedIcon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.CellTitleBarBinding;

public class TitleBarCell extends FrameLayout {

    private CellTitleBarBinding binding;

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
        binding = CellTitleBarBinding.inflate(inflater, this, true);

        setIconDrawable(drawable);
        setTitleText(titleText);
        setAltTitleText(titleAltText);
    }

    /**
     * Set custom drawable for the left icon
     */
    public void setIconDrawable(Drawable drawable) {
        binding.genericIcon.setImageDrawable(drawable);

        // Invalidate to trigger layout update
        invalidate();
    }

    /**
     * Set custom drawable for the left icon generated via the ITintedIcon interface.
     */
    public void setIcon(ITintedIcon icon){
        AssetLoader.setIcon(binding.genericIcon, icon);
    }

    public void setIconResource(@DrawableRes int resId) {
        binding.genericIcon.setImageResource(resId);

        // Invalidate to trigger layout update
        invalidate();
    }

    /**
     * Sets the main title of the title bar.
     * @param titleText
     */
    public void setTitleText(String titleText) {
        binding.titleText.setText(titleText);
    }

    /**
     * Sets the alt title. This is usually the japanese name.
     * @param altTitleText
     */
    public void setAltTitleText(String altTitleText) {
        binding.titleAltText.setText(altTitleText);
        maybeEnableAltText();
    }

    /**
     * Runs some logic to see if alt text should be enabled.
     */
    private void maybeEnableAltText() {
        CharSequence altText = binding.titleAltText.getText();
        if (altText != null && altText.length() > 0) {
            binding.titleAltText.setVisibility(VISIBLE);
        } else {
            binding.titleAltText.setVisibility(GONE);
        }
    }
}
