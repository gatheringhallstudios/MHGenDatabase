package com.ghstudios.android.features.armor.list;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.view.*;

import com.ghstudios.android.util.MHUtils;
import com.ghstudios.android.data.classes.ArmorFamily;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.ArmorClickListener;
import com.ghstudios.android.features.armorsetbuilder.detail.ASBPagerActivity;

import java.util.ArrayList;
import java.util.List;

import static com.ghstudios.android.data.classes.Armor.ARMOR_TYPE_BLADEMASTER;

/**
 * Pieced together from: Android samples:
 * com.example.android.apis.view.ExpandableList1
 * http://androidword.blogspot.com/2012/01/how-to-use-expandablelistview.html
 * http://stackoverflow.com/questions/6938560/android-fragments-setcontentview-
 * alternative
 * http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment-android
 */
public class ArmorExpandableListFragment extends Fragment {
    
    private static final String ARG_TYPE = "ARMOR_TYPE";

    public static final String KEY_FILTER_RANK = "FILTER_RANK";
    public static final String KEY_FILTER_SLOTS = "FILTER_SLOTS";
    public static final String KEY_FILTER_SLOTS_SPECIFICATION = "FILTER_SLOTS_SPEC";

    private String[] slots = {"Rare 1", "Rare 2", "Rare 3", "Rare 4", "Rare 5",
                                "Rare 6","Rare 7","Rare 8","Rare 9","Rare 10","Rare X"};

    private ArrayList<ArrayList<ArmorFamily>> children;
    private int hunterType;

    private ExpandableListView elv;
    private ArmorListAdapter adapter;

    public static ArmorExpandableListFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        ArmorExpandableListFragment f = new ArmorExpandableListFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hunterType = getArguments().getInt(ARG_TYPE,ARMOR_TYPE_BLADEMASTER);
        populateList();
    }

    /**
     * Updates the list of armors according to the filter's criteria.
     */
    private void populateList() {
        children = new ArrayList<>();
        List<ArmorFamily> families = DataManager.get().queryArmorFamilies(hunterType);

        //Add all 11 rarities
        for(int i=0;i<11;i++)
            children.add(new ArrayList<>());

        for (int i = 0; i < families.size(); i++) {
            children.get(families.get(i).getRarity()-1).add(families.get(i));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_generic_expandable_list, container, false);
        elv = v.findViewById(R.id.expandableListView);
        adapter = new ArmorListAdapter(slots);
        elv.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_armor_list, menu);
    }

    public class ArmorListAdapter extends BaseExpandableListAdapter {

        private String[] armors;

        public ArmorListAdapter(String[] armors) {
            super();
            this.armors = armors;

        }

        @Override
        public int getGroupCount() {
            return armors.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return children.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return armors[i];
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
            View v = view;
            Context context = viewGroup.getContext();

            if(v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.fragment_armor_expandablelist_group_item, viewGroup, false);
            }

            TextView armorGroupTextView = v.findViewById(R.id.name_text);

            armorGroupTextView.setText(getGroup(i).toString());

            if (getActivity().getIntent().getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)) {
                int piece = getActivity().getIntent().getIntExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, -1);

                if (piece != -1) {
                    elv.setDividerHeight(0);
                    if (i != piece) {
                        v = new FrameLayout(context); // We hide the group if it's not the type of armor we're looking for
                    }
                    else {
                        elv.expandGroup(i);
                    }
                }
            }
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;
            Context context = parent.getContext();

            if(v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.cell_armor_family_listitem, parent, false);
            }

            ConstraintLayout root = (ConstraintLayout) v;
            TextView armorTextView = v.findViewById(R.id.family_name);
            ImageView armorImageView = v.findViewById(R.id.icon);

            ArmorFamily family = (ArmorFamily) getChild(groupPosition,childPosition);

            armorTextView.setText(family.getName());

            armorImageView.setImageResource(MHUtils.getDrawableId(context,"armor_body"));
            armorImageView.setColorFilter(context.getResources().getIntArray(R.array.rare_colors)[family.getRarity()-1], PorterDuff.Mode.MULTIPLY);

            TextView minDef = v.findViewById(R.id.min_defense);
            TextView maxDef = v.findViewById(R.id.max_defense);
            minDef.setText(Integer.toString(family.getMinDef()));
            maxDef.setText(Integer.toString(family.getMaxDef()));

            //Set Skills
            TextView[] skills = {v.findViewById(R.id.skill_1),
                                v.findViewById(R.id.skill_2),
                                v.findViewById(R.id.skill_3),
                                v.findViewById(R.id.skill_4),
                                v.findViewById(R.id.skill_5)};

            for(int i=0;i<skills.length;i++){
                if(i < family.getSkills().size()){
                    skills[i].setVisibility(View.VISIBLE);
                    skills[i].setText(family.getSkills().get(i));
                }
                else{
                    skills[i].setVisibility(View.GONE);
                }
            }

            root.setTag(family.getId());

            if (getActivity().getIntent().getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)) {
                root.setOnClickListener(new ArmorClickListener(context, family.getId(),true, getActivity(), ASBPagerActivity.REQUEST_CODE_ADD_PIECE));
            }
            else {
                root.setOnClickListener(new ArmorClickListener(context, family.getId(),true));
            }

            return v;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}