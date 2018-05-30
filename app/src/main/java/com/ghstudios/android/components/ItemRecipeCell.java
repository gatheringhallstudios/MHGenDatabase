package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemRecipeCell extends ConstraintLayout {
    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.list)
    LinearLayout itemsView;

    public ItemRecipeCell(Context ctx) {
        super(ctx);
        init("");
    }

    public ItemRecipeCell(Context ctx, String title) {
        super(ctx);
        init(title);
    }

    public ItemRecipeCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ItemRecipeCell);

        String titleText;

        try {
            titleText = attributes.getString(R.styleable.ItemRecipeCell_titleText);
        } finally {
            attributes.recycle();
        }

        init(titleText);
    }

    public void init(String title) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.cell_item_recipe, this, true);

        ButterKnife.bind(this);

        setTitleText(title);
    }

    public void setTitleText(String title) {
        titleView.setText(title);
    }
}
