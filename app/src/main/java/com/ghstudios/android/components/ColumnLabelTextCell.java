package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColumnLabelTextCell extends ConstraintLayout implements LabelValueComponent {

    @BindView(R.id.label_text)
    TextView labelView;

    @BindView(R.id.value_text)
    TextView valueView;

    public ColumnLabelTextCell(Context context, String labelText, String valueText) {
        super(context);
        init(labelText, valueText);
    }

    public ColumnLabelTextCell(Context context) {
        super(context);
        init(null, null);
    }

    public ColumnLabelTextCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ColumnLabelTextCell);

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
        inflater.inflate(R.layout.cell_column_label_text, this, true);

        ButterKnife.bind(this);

        setLabelText(labelText);
        setValueText(valueText);
    }

    @Override
    public void setLabelText(CharSequence labelText) {
        labelView.setText(labelText);
        if (labelText == null || labelText.length() == 0) {
            labelView.setVisibility(View.GONE);
        } else {
            labelView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setValueText(CharSequence valueText) {
        valueView.setText(valueText);
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
