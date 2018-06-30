package com.ghstudios.android.features.monsters.detail;

import java.util.List;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ghstudios.android.AssetRegistry;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.data.classes.ElementStatus;
import com.ghstudios.android.data.classes.MonsterDamage;
import com.ghstudios.android.data.classes.MonsterStatus;
import com.ghstudios.android.mhgendatabase.R;

public class MonsterDamageFragment extends Fragment {
    private static final String ARG_MONSTER_ID = "MONSTER_ID";
    
    private TextView mMonsterLabelTextView;
    private ImageView mMonsterIconImageView;
    
    private LinearLayout mWeaponDamageTL, mElementalDamageTL;

    private TableLayout mStatusTable; // Location of table to add rows to
    
    public static MonsterDamageFragment newInstance(long monsterId) {
        Bundle args = new Bundle();
        args.putLong(ARG_MONSTER_ID, monsterId);
        MonsterDamageFragment f = new MonsterDamageFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monster_damage, container, false);
        
        mMonsterLabelTextView = view.findViewById(R.id.detail_monster_label);
        mMonsterIconImageView = view.findViewById(R.id.detail_monster_image);
    
        mWeaponDamageTL = view.findViewById(R.id.weapon_damage);
        mElementalDamageTL = view.findViewById(R.id.elemental_damage);

        mStatusTable = view.findViewById(R.id.statusTable);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MonsterDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(MonsterDetailViewModel.class);

        viewModel.getMonsterData().observe(this, monster -> {
            String cellImage = "icons_monster/" + monster.getFileLocation();
            Drawable monsterImage = MHUtils.loadAssetDrawable(getContext(), cellImage);

            mMonsterLabelTextView.setText(monster.getName());
            mMonsterIconImageView.setImageDrawable(monsterImage);
        });

        viewModel.getDamageData().observe(this, this::populateDamage);
        viewModel.getStatusData().observe(this, this::populateStatus);
    }

    private void populateDamage(List<MonsterDamage> damages) {
        MonsterDamage damage = null;
        String body_part;
        
        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        // build each row of both tables per record
        for(int i = 0; i < damages.size(); i++) {
            LinearLayout wdRow = (LinearLayout) inflater.inflate(
                    R.layout.fragment_monster_damage_listitem, mWeaponDamageTL, false);
            LinearLayout edRow = (LinearLayout) inflater.inflate(
                    R.layout.fragment_monster_damage_listitem, mElementalDamageTL, false);
              
            damage = damages.get(i);
            
            body_part = damage.getBodyPart();

            // Table 1
            TextView body_part_tv1 = wdRow.findViewById(R.id.body_part);
            TextView dummy_tv = wdRow.findViewById(R.id.dmg1);
            TextView cut_tv = wdRow.findViewById(R.id.dmg2);
            TextView impact_tv = wdRow.findViewById(R.id.dmg3);
            TextView shot_tv = wdRow.findViewById(R.id.dmg4);
            TextView ko_tv = wdRow.findViewById(R.id.dmg5);

            // Table 2
            TextView body_part_tv2 = edRow.findViewById(R.id.body_part);
            TextView fire_tv = edRow.findViewById(R.id.dmg1);
            TextView water_tv = edRow.findViewById(R.id.dmg2);
            TextView ice_tv = edRow.findViewById(R.id.dmg3);
            TextView thunder_tv = edRow.findViewById(R.id.dmg4);
            TextView dragon_tv = edRow.findViewById(R.id.dmg5);


            SpannableString s = new SpannableString(body_part);

            if (body_part.contains("(")) {
                int start = body_part.indexOf("(");
                int end = body_part.length();
                s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.text_color_secondary)),start,end,0);
                s.setSpan(new RelativeSizeSpan(.8f),start,end,0);
            }


            body_part_tv1.setText(s);
            body_part_tv2.setText(s);


            checkDamageValue(damage.getCut(),cut_tv,false,false);
            checkDamageValue(damage.getImpact(),impact_tv,false,false);
            checkDamageValue(damage.getShot(),shot_tv,false,false);
            checkDamageValue(damage.getKo(),ko_tv,false,true);

            checkDamageValue(damage.getFire(),fire_tv,true,false);
            checkDamageValue(damage.getWater(),water_tv,true,false);
            checkDamageValue(damage.getIce(),ice_tv,true,false);
            checkDamageValue(damage.getThunder(),thunder_tv,true,false);
            checkDamageValue(damage.getDragon(),dragon_tv,true,false);

            dummy_tv.setText("");

            mWeaponDamageTL.addView(wdRow);
            mElementalDamageTL.addView(edRow);
        }
    }
    
    private String checkDamageValue(int damage,TextView tv, boolean element, boolean isKO) {
        String ret = Integer.toString(damage);
        if(damage<=0)
            ret = "-";

        tv.setText(ret);

        if(!isKO && (!element && damage>=45) || (element && damage>=25))
            tv.setTypeface(null,Typeface.BOLD);

        return ret;
    }

    private void populateStatus(List<MonsterStatus> statuses) {
        String status, initial, increase, max, duration, damage;

        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        for (MonsterStatus currentStatus : statuses) {
            TableRow wdRow = (TableRow) inflater.inflate(
                    R.layout.fragment_monster_status_listitem, mStatusTable, false);

            // Get our strings and our views
            initial = Long.toString(currentStatus.getInitial());
            increase = Long.toString(currentStatus.getIncrease());
            max = Long.toString(currentStatus.getMax());
            duration = Long.toString(currentStatus.getDuration());
            damage = Long.toString(currentStatus.getDamage());

            String DefaultString = "-";

            if(currentStatus.getInitial()==0)
                initial = DefaultString;

            if(currentStatus.getIncrease()==0)
                increase = DefaultString;

            if(currentStatus.getMax()==0)
                max = DefaultString;

            if(currentStatus.getDuration()==0)
                duration = DefaultString;
            else
                duration += "s";

            if(currentStatus.getDamage()==0)
                damage = DefaultString;

            ImageView statusImage = (ImageView) wdRow.findViewById(R.id.statusImage);
            TextView initialView = (TextView) wdRow.findViewById(R.id.initial);
            TextView increaseView = (TextView) wdRow.findViewById(R.id.increase);
            TextView maxView = (TextView) wdRow.findViewById(R.id.max);
            TextView durationView = (TextView) wdRow.findViewById(R.id.duration);
            TextView damageView = (TextView) wdRow.findViewById(R.id.damage);

            // Check which image to load
            ElementStatus element = currentStatus.getStatusEnum();
            int imageFile = AssetRegistry.getElementRegistry().get(element, R.color.transparent);

            // initialize our views
            initialView.setText(initial);
            increaseView.setText(increase);
            maxView.setText(max);
            durationView.setText(duration);
            damageView.setText(damage);

            if (imageFile != -1) {
                Drawable draw = ContextCompat.getDrawable(getContext(), imageFile);
                android.view.ViewGroup.LayoutParams layoutParams = statusImage.getLayoutParams();
                statusImage.setLayoutParams(layoutParams);
                statusImage.setImageDrawable(draw);
            }

            mStatusTable.addView(wdRow);
        }
    }
}
