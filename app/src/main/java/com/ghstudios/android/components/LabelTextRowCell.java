package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.ghstudios.android.mhgendatabase.R;

/**
 * This is a full height, full width cell that displays an icon, label, and value. Used to generate
 * data rows in RecyclerView or inside XML layouts.
 */

public class LabelTextRowCell extends ConstraintLayout implements LabelValueComponent {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.generic_icon) ImageView imageView;
    @BindView(R.id.label_text) TextView labelView;
    @BindView(R.id.label_alt_text) TextView labelAltView;
    @BindView(R.id.value_text) TextView valueView;

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
        inflater.inflate(R.layout.cell_icon_label_text, this, true);

        ButterKnife.bind(this);

        imageView.setVisibility(View.GONE);
        setLabelText(labelText);
        setLabelAltText(labelAltText);
        setAltTextEnabled(altTextEnabled);
        setValueText(valueText);
    }

    @Override
    public void setLabelText(CharSequence labelText) {
        labelView.setText(labelText);
    }

    /**
     * Sets the alt title. This is usually the japanese name.
     * @param altTitleText
     */
    public void setLabelAltText(String altTitleText) {
        labelAltView.setText(altTitleText);
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
        valueView.setText(valueText);
    }

    /**
     * Runs some logic to see if alt text should be enabled.
     */
    private void updateAltTextVisibility() {
        CharSequence altText = labelAltView.getText();
        if (altEnabled && altText != null && altText.length() > 0) {
            labelAltView.setVisibility(VISIBLE);
        } else {
            labelAltView.setVisibility(GONE);
        }
    }

    @Override
    public CharSequence getLabelText() {
        return labelView.getText();
    }

    @Override
    public CharSequence getValueText() {
        return valueView.getText();
    }
}
