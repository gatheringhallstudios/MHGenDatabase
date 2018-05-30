package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.ghstudios.android.mhgendatabase.R;

/**
 * This is a full height, full width cell that displays an icon, label, and value. Used to generate
 * data rows in RecyclerView or inside XML layouts.
 */

public class LabelTextCell extends ConstraintLayout {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.label_text)
    TextView labelView;

    @BindView(R.id.value_text)
    TextView valueView;

    public LabelTextCell(Context context, String labelText, String valueText) {
        super(context);
        init(labelText, valueText);
    }

    public LabelTextCell(Context context) {
        super(context);
        init("", "");
    }

    public LabelTextCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LabelTextCell);

        try {
            String labelText = attributes.getString(R.styleable.ColumnLabelTextCell_labelText);
            String valueText = attributes.getString(R.styleable.ColumnLabelTextCell_valueText);

            init(labelText, valueText);
        } finally {
            attributes.recycle();
        }
    }

    public void init(String labelText, String valueText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.cell_label_text, this, true);

        ButterKnife.bind(this);

        setLabelText(labelText);
        setValueText(valueText);
    }

    public void setLabelText(String labelText) {
        labelView.setText(labelText);
    }

    public void setValueText(String valueText) {
        valueView.setText(valueText);
    }
}
