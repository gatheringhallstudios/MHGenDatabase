package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.CellIconLabelTextBinding;

/**
 * This is a full height, full width cell that displays an icon, label, and value. Used to generate
 * data rows in RecyclerView or inside XML layouts.
 */

public class LabelTextRowCell extends ConstraintLayout implements LabelValueComponent {

    private final String TAG = getClass().getSimpleName();

    private CellIconLabelTextBinding binding;

    boolean altEnabled = false;

    public LabelTextRowCell(Context context, String labelText, String valueText) {
        super(context);
        init(labelText, "", false, valueText);
    }

    public LabelTextRowCell(Context context) {
        super(context);
        init("", "", false, "");
    }

    public LabelTextRowCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LabelTextRowCell);

        try {
            String labelText = attributes.getString(R.styleable.LabelTextRowCell_labelText);
            String labelAltText = attributes.getString(R.styleable.LabelTextRowCell_labelAltText);
            boolean altTextEnabled = attributes.getBoolean(R.styleable.LabelTextRowCell_altTextEnabled, false);
            String valueText = attributes.getString(R.styleable.LabelTextRowCell_valueText);

            init(labelText, labelAltText, altTextEnabled, valueText);
        } finally {
            attributes.recycle();
        }
    }

    public void init(String labelText, String labelAltText, boolean altTextEnabled, String valueText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = CellIconLabelTextBinding.inflate(inflater, this, true);

        binding.genericIcon.setVisibility(View.GONE);
        setLabelText(labelText);
        setLabelAltText(labelAltText);
        setAltTextEnabled(altTextEnabled);
        setValueText(valueText);
    }

    @Override
    public void setLabelText(CharSequence labelText) {
        binding.labelText.setText(labelText);
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

    @Override
    public void setValueText(CharSequence valueText) {
        binding.valueText.setText(valueText);
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

    @Override
    public CharSequence getLabelText() {
        return binding.labelText.getText();
    }

    @Override
    public CharSequence getValueText() {
        return binding.valueText.getText();
    }
}
