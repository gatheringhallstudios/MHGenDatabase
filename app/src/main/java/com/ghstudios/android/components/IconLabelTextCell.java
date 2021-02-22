package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.ITintedIcon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.CellIconLabelTextBinding;

/**
 * This is a full height, full width cell that displays an icon, label, and value. Used to generate
 * data rows in RecyclerView or inside XML layouts.
 */

public class IconLabelTextCell extends FrameLayout {

    private final String TAG = getClass().getSimpleName();

    private CellIconLabelTextBinding binding;

    boolean altEnabled = false;

    public IconLabelTextCell (Context context, @DrawableRes int imgSrc, String labelText, String valueText) {
        super(context);
        Drawable drawable = ContextCompat.getDrawable(getContext(), imgSrc);
        init(drawable, labelText, null, false, valueText);
    }

    public IconLabelTextCell(Context context) {
        super(context);
        init(null, "", "", false,"");
    }

    public IconLabelTextCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.IconLabelTextCell);

        // Set values from attributes
        try {
            Drawable drawable= attributes.getDrawable(R.styleable.IconLabelTextCell_iconSrc);
            String labelText = attributes.getString(R.styleable.IconLabelTextCell_labelText);
            String labelAltText = attributes.getString(R.styleable.IconLabelTextCell_labelAltText);
            boolean altTextEnabled = attributes.getBoolean(R.styleable.IconLabelTextCell_altTextEnabled, false);
            String valueText = attributes.getString(R.styleable.IconLabelTextCell_valueText);

            init(drawable, labelText, labelAltText, altTextEnabled, valueText);
        } finally {
            // Typed arrays should be recycled after use
            attributes.recycle();
        }
    }

    public void init(Drawable drawable, String labelText, String labelAltText, boolean altTextEnabled, String valueText) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = CellIconLabelTextBinding.inflate(inflater, this, true);

        setLeftIconDrawable(drawable);
        setLabelText(labelText);
        setLabelAltText(labelAltText);
        setAltTextEnabled(altTextEnabled);
        setValueText(valueText);
    }

    /**
     * Set custom drawable for the left icon
     */
    public void setLeftIconDrawable(Drawable drawable) {
        binding.genericIcon.setImageDrawable(drawable);

        // Invalidate to trigger layout update
        invalidate();
    }

    public void setLeftIcon(ITintedIcon icon){
        AssetLoader.setIcon(binding.genericIcon,icon);
    }

    public void setLabelText(String labelText) {
        binding.labelText.setText(labelText);
    }

    public CharSequence getLabelText() {
        return binding.labelText.getText();
    }

    /**
     * Sets the alt title. This is usually the japanese name.
     * @param altTitleText
     */
    public void setLabelAltText(String altTitleText) {
        binding.labelAltText.setText(altTitleText);
        updateAltTextVisibility();
    }

    /**
     * Sets whether or not the alt text is enabled
     * @param enabled
     */
    public void setAltTextEnabled(boolean enabled) {
        altEnabled = enabled;
        updateAltTextVisibility();
    }

    public void setValueText(String valueText) {
        binding.valueText.setText(valueText);
    }

    public void setKeyVisibility(boolean show){
        if(show)
            binding.key.setVisibility(View.VISIBLE);
        else
            binding.key.setVisibility(View.GONE);
    }

    /**
     * Runs some logic to see if alt text should be enabled.
     */
    private void updateAltTextVisibility() {
        CharSequence altText = binding.labelAltText.getText();
        if (altEnabled && altText != null && altText.length() > 0) {
            binding.labelAltText.setVisibility(VISIBLE);
        } else {
            binding.labelAltText.setVisibility(GONE);
        }
    }
}
