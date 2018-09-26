package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.ITintedIcon;
import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemRecipeCell extends LinearLayout {
    @BindView(R.id.title)
    SubHeaderCell titleView;

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
        setOrientation(VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.cell_item_recipe, this, true);

        ButterKnife.bind(this);

        setTitleText(title);
    }

    /**
     * Sets the title text. If the title is null or empty, the title is hidden.
     * @param title
     */
    public void setTitleText(String title) {
        titleView.setLabelText(title);
        if (title != null && !title.trim().isEmpty()) {
            titleView.setVisibility(VISIBLE);
        } else {
            titleView.setVisibility(GONE);
        }
    }

    public View addItem(ITintedIcon icon, String itemName, int qty, boolean key) {
        IconLabelTextCell cell = new IconLabelTextCell(getContext());
        cell.setLeftIcon(icon);
        cell.setLabelText(itemName);
        cell.setValueText(String.valueOf(qty));
        cell.setKeyVisibility(key);

        itemsView.addView(cell);

        return cell;
    }

    /**
     * Clears all components in this recipe
     */
    public void clearItems() {
        itemsView.removeAllViews();
    }
}
