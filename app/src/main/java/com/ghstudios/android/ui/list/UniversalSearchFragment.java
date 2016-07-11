package com.ghstudios.android.ui.list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.Monster;
import com.ghstudios.android.data.classes.Quest;
import com.ghstudios.android.data.database.MultiObjectCursor;
import com.ghstudios.android.loader.UniversalSearchCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.ArmorClickListener;
import com.ghstudios.android.ui.ClickListeners.DecorationClickListener;
import com.ghstudios.android.ui.ClickListeners.ItemClickListener;
import com.ghstudios.android.ui.ClickListeners.MaterialClickListener;
import com.ghstudios.android.ui.ClickListeners.MonsterClickListener;
import com.ghstudios.android.ui.ClickListeners.PalicoWeaponClickListener;
import com.ghstudios.android.ui.ClickListeners.QuestClickListener;
import com.ghstudios.android.ui.ClickListeners.WeaponClickListener;

import java.io.IOException;
import java.util.HashMap;

public class UniversalSearchFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private interface ResultHandler<T> {
        String getImage(T obj);
        String getName(T obj);
        String getType(T obj);
        View.OnClickListener createListener(T obj);
    }

    private HashMap<Class, ResultHandler> mHandlers = new HashMap<>();

    private String mSearchTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandlers.put(Monster.class, new ResultHandler<Monster>() {
            @Override
            public String getImage(Monster obj) {
                return "icons_monster/" + obj.getFileLocation();
            }

            @Override
            public String getName(Monster obj) {
                return obj.getName();
            }

            @Override
            public String getType(Monster obj) {
                return "Monster";
            }

            @Override
            public View.OnClickListener createListener(Monster obj) {
                return new MonsterClickListener(getActivity(), obj.getId());
            }
        });

        mHandlers.put(Quest.class, new ResultHandler<Quest>() {
            @Override
            public String getImage(Quest obj) {
                // todo: Change color if capture/slay (requires db + more icons)
                return "icons_items/Quest-Icon-White.png";
            }

            @Override
            public String getName(Quest obj) {
                return obj.getName();
            }

            @Override
            public String getType(Quest obj) {
                return "Quest";
            }

            @Override
            public View.OnClickListener createListener(Quest obj) {
                return new QuestClickListener(getActivity(), obj.getId());
            }
        });

        mHandlers.put(Item.class, new ResultHandler<Item>() {
            @Override
            public String getImage(Item item) {
                return item.getItemImage();
            }

            @Override
            public String getName(Item obj) {
                return obj.getName();
            }

            @Override
            public String getType(Item obj) {
                return obj.getType();
            }

            @Override
            public View.OnClickListener createListener(Item obj) {
                switch(obj.getType()){
                    case "Weapon":
                        return new WeaponClickListener(getActivity(), obj.getId());
                    case "Armor":
                        return new ArmorClickListener(getActivity(), obj.getId());
                    case "Decoration":
                        return new DecorationClickListener(getActivity(), obj.getId());
                    case "Materials":
                        return new MaterialClickListener(getActivity(),obj.getId());
                    case "Palico Weapon":
                        return new PalicoWeaponClickListener(getActivity(),obj.getId());
                    default:
                        return new ItemClickListener(getActivity(), obj.getId());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    public void performSearch(String searchTerm) {
        mSearchTerm = searchTerm;
        if (mSearchTerm != null) {
            mSearchTerm = mSearchTerm.trim();
        }

        if (!mSearchTerm.equals("")) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            clearSearch();
        }
    }

    private void clearSearch() {
        setListAdapter(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new UniversalSearchCursorLoader(getActivity(), mSearchTerm);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        UniversalSearchCursorAdapter adapter = new UniversalSearchCursorAdapter(
                getActivity(), mHandlers, (MultiObjectCursor) cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    private static class UniversalSearchCursorAdapter extends CursorAdapter {
        private HashMap<Class, ResultHandler> mHandlers;
        public UniversalSearchCursorAdapter(Context context,
                                            HashMap<Class, ResultHandler> handlers,
                                            MultiObjectCursor cursor) {
            super(context, cursor, 0);
            mHandlers = handlers;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            return inflater.inflate(R.layout.fragment_searchresult_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Object result = ((MultiObjectCursor)cursor).getObject();
            Class originalClass = result.getClass();

            if (!mHandlers.containsKey(originalClass)) {
                // Not expected, so marked as a runtime exception
                throw new RuntimeException(
                        "Could not find handler for class " + originalClass.getName());
            }

            ResultHandler handler = mHandlers.get(originalClass);

            ImageView imageView = (ImageView) view.findViewById(R.id.result_image);
            TextView nameView = (TextView) view.findViewById(R.id.result_name);
            TextView typeView = (TextView) view.findViewById(R.id.result_type);

            String imagePath = handler.getImage(result);
            if (imagePath != null) {
                Drawable itemImage = null;

                try {
                    itemImage = Drawable.createFromStream(
                            context.getAssets().open(imagePath), null);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                imageView.setImageDrawable(itemImage);
            }

            nameView.setText(handler.getName(result));
            typeView.setText(handler.getType(result));
            view.setOnClickListener(handler.createListener(result));
        }
    }
}
