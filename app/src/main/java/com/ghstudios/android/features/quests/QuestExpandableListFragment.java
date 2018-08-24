package com.ghstudios.android.features.quests;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.Quest;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.QuestClickListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Pieced together from: Android samples:
 * com.example.android.apis.view.ExpandableList1
 * http://androidword.blogspot.com/2012/01/how-to-use-expandablelistview.html
 * http://stackoverflow.com/questions/6938560/android-fragments-setcontentview-
 * alternative
 * http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment-android
 */
public class QuestExpandableListFragment extends Fragment {
    private String mHub;
    private static final String ARG_HUB = "QUEST_HUB";
    private String[] caravan = {"1", "2", "3", "4", "5", "6","7","8","9","10"};
    private String[] guild = {"1", "2", "3", "4", "5", "6", "7","G1","G2","G3","G4"};
    private String[] event = {"1", "2", "3", "4", "5", "6", "7","G1","G2","G3","G4"};
    private String[] permit=null;

    private ArrayList<ArrayList<Quest>> children;

    public static QuestExpandableListFragment newInstance(String hub) {
        Bundle args = new Bundle();
        args.putString(ARG_HUB, hub);
        QuestExpandableListFragment f = new QuestExpandableListFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHub = null;
        Bundle args = getArguments();
        if (args != null) {
            mHub = args.getString(ARG_HUB);
        }
        populateList();

    }

    private void populateList() {
        children = new ArrayList<>();
        List<Quest> quests = DataManager.get(getActivity()).queryQuestArrayHub(mHub);
        HashMap<String,ArrayList<Quest>> questGroups = new HashMap<>();
        for (int i = 0; i < quests.size(); i++) {
            String grouping = quests.get(i).getStars();

            //Permit quests aren't grouped by stars, but by permit monster id
            if(mHub == "Permit")
                grouping = Integer.toString(quests.get(i).getPermitMonsterId());

            if(questGroups.containsKey(grouping)){
                questGroups.get(grouping).add(quests.get(i));
            }else{
                ArrayList<Quest> qs = new ArrayList<>();
                qs.add(quests.get(i));
                children.add(qs);
                questGroups.put(grouping,qs);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_generic_expandable_list, container, false);
        ExpandableListView elv = v
                .findViewById(R.id.expandableListView);
        if (mHub.equals("Village")) {
            elv.setAdapter(new QuestListAdapter(caravan));
        } else if(mHub.equals("Guild")) {
            elv.setAdapter(new QuestListAdapter(guild));
        } else if(mHub.equals("Permit")){
            permit = DataManager.get(getActivity()).questDeviantMonsterNames();
            elv.setAdapter(new QuestListAdapter(permit));
        }
        else{
            elv.setAdapter(new QuestListAdapter(event));
        }

        return v;

    }

    public class QuestListAdapter extends BaseExpandableListAdapter {

        private String[] quests;

        public QuestListAdapter(String[] quests) {
            super();
            this.quests = quests;

        }

        @Override
        public int getGroupCount() {
            return quests.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return children.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return quests[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return children.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view,
                                 ViewGroup viewGroup) {
            View v;
            Context context = viewGroup.getContext();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(
                    R.layout.fragment_quest_expandablelist_group_item,
                    viewGroup, false);

            TextView questGroupTextView = v.findViewById(R.id.numstars);
            ImageView[] stars = new ImageView[10];
            stars[0] = v.findViewById(R.id.star1);
            stars[1] = v.findViewById(R.id.star2);
            stars[2] = v.findViewById(R.id.star3);
            stars[3] = v.findViewById(R.id.star4);
            stars[4] = v.findViewById(R.id.star5);
            stars[5] = v.findViewById(R.id.star6);
            stars[6] = v.findViewById(R.id.star7);
            stars[7] = v.findViewById(R.id.star8);
            stars[8] = v.findViewById(R.id.star9);
            stars[9] = v.findViewById(R.id.star10);

            if(quests == permit) {
                for (int j = 0; j <= 9; j++) {
                    stars[j].setVisibility(View.INVISIBLE);
                }
            }else if(quests == caravan){
                for (int j = 0; j <= i; j++) {
                    stars[j].setVisibility(View.VISIBLE);
                }
            } else{
                if(i<7){
                    for (int j = 0; j <= i; j++) {
                        stars[j].setVisibility(View.VISIBLE);
                    }
                }else{
                    stars[0].setVisibility(View.VISIBLE);
                }
            }

            questGroupTextView.setText(getGroup(i).toString());

            return v;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view,
                                 ViewGroup viewGroup) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(
                    R.layout.fragment_quest_expandablelist_child_item,
                    viewGroup, false);

            ImageView iv = v.findViewById(R.id.item_image);
            TextView questChildTextView = v.findViewById(R.id.name_text);
            TextView keyTextView = v.findViewById(R.id.key);
            TextView urgentTextView = v.findViewById(R.id.urgent);
            LinearLayout root = v.findViewById(R.id.root);

            Quest q = (Quest)getChild(i,i1);
            AssetLoader.setIcon(iv, q);

            questChildTextView.setText(getChild(i, i1).toString());
            if(q.getType() == Quest.QUEST_TYPE_NONE)
            {
                keyTextView.setVisibility(View.GONE);
                urgentTextView.setVisibility(View.GONE);
            }
            else if(q.getType() == Quest.QUEST_TYPE_KEY){
                urgentTextView.setVisibility(View.GONE);
                keyTextView.setVisibility(View.VISIBLE);
            }
            else{
                urgentTextView.setVisibility(View.VISIBLE);
                keyTextView.setVisibility(View.GONE);
            }

            long questId = ((Quest) getChild(i, i1)).getId();

            root.setTag(questId);
            root.setOnClickListener(new QuestClickListener(context, questId));
            return v;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

    }

}