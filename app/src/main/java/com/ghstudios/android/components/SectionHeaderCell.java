package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.CellSectionHeaderBinding;


/**
 * This is a full height, full width cell that displays a section header. Used to generate
 * header rows in RecyclerView or inside XML layouts.
 */

public class SectionHeaderCell extends LinearLayout {

    private final String TAG = getClass().getSimpleName();

    private CellSectionHeaderBinding binding;

    public SectionHeaderCell(Context context, String labelText) {
        super(context);
        init(labelText);
    }

    public SectionHeaderCell(Context context) {
        super(context);
        init("");
    }

    public SectionHeaderCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SectionHeaderCell);

        // Set values from attributes
        String labelText;
        try {
            labelText = attributes.getString(R.styleable.SectionHeaderCell_labelText);
        } finally {
            // Typed arrays should be recycled after use
            attributes.recycle();
        }

        init(labelText);
    }

    public void init(String labelText) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = CellSectionHeaderBinding.inflate(inflater, this);

        setLabelText(labelText);
    }

    public void setLabelText(String labelText) {
        binding.labelText.setText(labelText);
    }

}
