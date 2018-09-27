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
import com.ghstudios.android.data.classes.SkillTree;
import com.ghstudios.android.data.database.MultiObjectCursor;
import com.ghstudios.android.loader.UniversalSearchCursorLoader;
import com.ghstudios.android.mhgendatabaseold.R;
import com.ghstudios.android.ui.ClickListeners.ItemClickListener;
import com.ghstudios.android.ui.ClickListeners.MonsterClickListener;
import com.ghstudios.android.ui.ClickListeners.QuestClickListener;
import com.ghstudios.android.ui.ClickListeners.SkillClickListener;

import java.io.IOException;
import java.util.HashMap;

public class UniversalSearchFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * A simple handler class used to create mappings for the Universal Search results.
     * Override EITHER getImagePath or getImageResource so that getImage() will handle the rest.
     * @param <T>
     */
    private abstract class ResultHandler<T> {
        public String getImagePath(T obj) { return null; }
        public int getImageResource(T obj) { return -1; }
        abstract String getName(T obj);
        abstract String getType(T obj);
        abstract View.OnClickListener createListener(T obj);

        public Drawable getImage(T obj, Context ctx) {
            try {
                String imagePath = this.getImagePath(obj);
                if (imagePath != null) {
                    return Drawable.createFromStream(ctx.getAssets().open(imagePath), null);
                } else {
                    int resource = this.getImageResource(obj);
                    return Drawable.createFromStream(ctx.getResources().openRawResource(resource), null);
                }
            } catch (IOException e) {
                // should this throw instead of returning null?
                e.printStackTrace();
                return null;
            }
        }
    }

    private HashMap<Class, ResultHandler> mHandlers = new HashMap<>();

    private String mSearchTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandlers.put(Monster.class, new ResultHandler<Monster>() {
            @Override
            public String getImagePath(Monster obj) {
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
            public int getImageResource(Quest q) {
                if(q.getHunterType() == 1)
                    return R.drawable.quest_cat;
                else if(q.getGoalType() == Quest.QUEST_GOAL_DELIVER)
                    return R.drawable.quest_icon_green;
                else if(q.getGoalType() == Quest.QUEST_GOAL_CAPTURE)
                    return R.drawable.quest_icon_grey;
                else
                    return R.drawable.quest_icon_red;
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

        mHandlers.put(SkillTree.class, new ResultHandler<SkillTree>() {
            @Override
            public String getImagePath(SkillTree skill) {
                return "icons_items/Bomb-White.png";
            }

            @Override
            public String getName(SkillTree obj)  {
                return obj.getName();
            }

            @Override
            public String getType(SkillTree obj) {
                return "Skill Tree";
            }

            @Override
            public View.OnClickListener createListener(SkillTree obj) {
                return new SkillClickListener(getActivity(), obj.getId());
            }
        });

        mHandlers.put(Item.class, new ResultHandler<Item>() {
            @Override
            public String getImagePath(Item item) {
                return item.getItemImage();
            }

            @Override
            public String getName(Item obj) {
                return obj.getName();
            }

            @Override
            public String getType(Item obj) {
                String type = obj.getType();
                if (type == null || type.equals("")) {
                    // todo: localize, but item types should be localized too
                    type = "Item";
                }
                return type;
            }

            @Override
            public View.OnClickListener createListener(Item obj) {
                return new ItemClickListener(getActivity(), obj);
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

            Drawable image = handler.getImage(result, context);
            imageView.setImageDrawable(image);

            nameView.setText(handler.getName(result));
            typeView.setText(handler.getType(result));
            view.setOnClickListener(handler.createListener(result));
        }
    }
}
