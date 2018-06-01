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
import android.widget.TextView;

import com.ghstudios.android.MHUtils;
import com.ghstudios.android.data.classes.MonsterDamage;
import com.ghstudios.android.features.monsters.MonsterDetailViewModel;
import com.ghstudios.android.mhgendatabase.R;

public class MonsterDamageFragment extends Fragment {
    private static final String ARG_MONSTER_ID = "MONSTER_ID";
    
    private TextView mMonsterLabelTextView;
    private ImageView mMonsterIconImageView;
    
    private LinearLayout mWeaponDamageTL, mElementalDamageTL;
    private View mDividerView;
    
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
        
        mMonsterLabelTextView = (TextView) view.findViewById(R.id.detail_monster_label);
        mMonsterIconImageView = (ImageView) view.findViewById(R.id.detail_monster_image);
    
        mWeaponDamageTL = (LinearLayout) view.findViewById(R.id.weapon_damage);
        mElementalDamageTL = (LinearLayout) view.findViewById(R.id.elemental_damage);

        mDividerView = view.findViewById(R.id.divider);
        
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

        viewModel.getMonsterDamageData().observe(this, monsterDamages -> {
            updateUI(monsterDamages);
        });
    }

    private void updateUI(List<MonsterDamage> damages) {
        MonsterDamage damage = null;
        String body_part, cut, impact, shot, ko, fire, water, ice, thunder, dragon;
        
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
}
