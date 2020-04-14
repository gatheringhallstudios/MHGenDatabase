package com.ghstudios.android;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

import java.util.List;

public abstract class SectionArrayAdapter<T> extends ArrayAdapter<T> {
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COUNT = 2;

    private final int mHeaderRes;
    private final LayoutInflater mLayoutInflater;
    private SparseArray<String> mSectionsIndexer;
    private List<T> mItems;

    public SectionArrayAdapter(Context context, List<T> itemList){
        this(context,itemList, R.layout.listview_generic_header);
    }

    public SectionArrayAdapter(Context context, List<T> itemList, int headerLayout) {
        super(context,headerLayout,itemList);
        mSectionsIndexer = new SparseArray<>();
        mHeaderRes = headerLayout;
        mLayoutInflater = LayoutInflater.from(context);
        mItems = itemList;
        calculateSectionHeaders();
    }

    //Override to set header names
    public abstract String getGroupName(T item);

    //Override these to create/bind views
    public abstract void bindView(View view, Context context, T item);
    public abstract View newView(Context context, T item, ViewGroup parent);

    private void calculateSectionHeaders() {
        int i = 0;

        String previous = "";
        int count = 0;

        mSectionsIndexer.clear();
        for (T item:mItems)
        {
            String group = getGroupName(item);
            if (!previous.equals(group)) {
                mSectionsIndexer.put(i + count, group);
                previous = group;
                count++;
            }

            i++;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (viewType == TYPE_NORMAL) {
            if(convertView == null){
                convertView = newView(getContext(),getItem(position),parent);
            }
            bindView(convertView,getContext(),getItem(position));
            return convertView;
        } else {
            if (convertView == null) {
                convertView = newHeaderView(parent);
            }

            bindHeaderView(convertView,position);
            return convertView;
        }
    }

    private View newHeaderView(ViewGroup parent){
        SectionArrayAdapter.ViewHolder holder = new SectionArrayAdapter.ViewHolder();
        View v = mLayoutInflater.inflate(mHeaderRes, parent, false);
        holder.textView = (TextView) v;
        v.setTag(holder);
        return v;
    }

    private void bindHeaderView(View view, int position){
        SectionArrayAdapter.ViewHolder holder = (SectionArrayAdapter.ViewHolder) view.getTag();
        TextView sectionText = holder.textView;
        final String group = mSectionsIndexer.get(position);
        sectionText.setText(group);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return super.getCount() + mSectionsIndexer.size();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_NORMAL;
    }

    private int getPositionForSection(int section) {
        if (mSectionsIndexer.get(section, null) != null) {
            return section + 1;
        }
        return section;
    }

    private int getSectionForPosition(int position) {
        int offset = 0;

        for (int i = 0; i < mSectionsIndexer.size(); i++) {
            int key = mSectionsIndexer.keyAt(i);

            if (position > key) {
                offset++;
            } else {
                break;
            }
        }

        return position - offset;
    }

    @Override
    public T getItem(int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            return super.getItem(getSectionForPosition(position));
        }
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            return super.getItemId(getSectionForPosition(position));
        }
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getPositionForSection(position)) {
            return TYPE_NORMAL;
        }
        return TYPE_HEADER;
    }

    public static class ViewHolder {
        TextView textView;
    }

}
