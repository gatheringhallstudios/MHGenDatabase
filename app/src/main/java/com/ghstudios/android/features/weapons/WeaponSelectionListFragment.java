package com.ghstudios.android.features.weapons;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghstudios.android.features.weapons.list.WeaponListActivity;
import com.ghstudios.android.mhgendatabase.R;

public class WeaponSelectionListFragment extends ListFragment {

    private WeaponItemAdapter mAdapter = null;

	private final static int GREAT_SWORD = 0;
	private final static int LONG_SWORD = 1;
	private final static int SWORD_AND_SHIELD = 2;
	private final static int DUAL_BLADES = 3;
	private final static int HAMMER = 4;
	private final static int HUNTING_HORN = 5;
	private final static int LANCE = 6;
	private final static int GUNLANCE = 7;
    private final static int SWITCH_AXE = 8;
	private final static int CHARGE_BLADE = 9;
    private final static int INSECT_GLAIVE = 10;
	private final static int LIGHT_BOWGUN = 11;
	private final static int HEAVY_BOWGUN = 12;
	private final static int BOW = 13;

	static final String[] weapons = new String[] { "Great Sword", "Long Sword",
			"Sword and Shield", "Dual Blades", "Hammer", "Hunting Horn",
			"Lance", "Gunlance", "Switch Axe", "Charge Blade", "Insect Glaive", "Light Bowgun", "Heavy Bowgun",
			"Bow" };

	static final int[] resources = new int[]{
			R.drawable.icon_great_sword,R.drawable.icon_long_sword,
			R.drawable.icon_sword_and_shield,R.drawable.icon_dual_blades,
			R.drawable.icon_hammer,R.drawable.icon_hunting_horn,
			R.drawable.icon_lance,R.drawable.icon_gunlance,
			R.drawable.icon_switch_axe,R.drawable.icon_charge_blade,
			R.drawable.icon_insect_glaive,R.drawable.icon_light_bowgun,
			R.drawable.icon_heavy_bowgun,R.drawable.icon_bow};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list, parent, false);

        ArrayList<String> items = new ArrayList<>(Arrays.asList(weapons));
        mAdapter = new WeaponItemAdapter(items);
        setListAdapter(mAdapter);

		return v;
	}

	private class WeaponItemAdapter extends ArrayAdapter<String> {
		public WeaponItemAdapter(ArrayList<String> items) {
			super(getActivity(), 0, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.fragment_list_item_large,
						parent, false);
			}

			String item = getItem(position);

			TextView textView = convertView.findViewById(R.id.item_label);
			ImageView imageView = convertView.findViewById(R.id.item_image);

            RelativeLayout itemLayout = convertView.findViewById(R.id.listitem);

			textView.setText(item);
			imageView.setImageResource(resources[position]);

			itemLayout.setOnClickListener(new WeaponListClickListener(convertView.getContext(), position));

			return convertView;
		}

		private class WeaponListClickListener implements OnClickListener {
			private Context c;
			private int position;

			public WeaponListClickListener(Context context, int position) {
				super();
				this.position = position;
				this.c = context;
			}

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(c, WeaponListActivity.class);

				switch (position) {
				case GREAT_SWORD:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Great Sword");
					break;
				case LONG_SWORD:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Long Sword");
					break;
				case SWORD_AND_SHIELD:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Sword and Shield");
					break;
				case DUAL_BLADES:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Dual Blades");
					break;
				case HAMMER:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Hammer");
					break;
				case HUNTING_HORN:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Hunting Horn");
					break;
				case LANCE:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Lance");
					break;
				case GUNLANCE:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Gunlance");
					break;
				case SWITCH_AXE:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Switch Axe");
					break;
                case CHARGE_BLADE:
                    intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
                            "Charge Blade");
                    break;
                case INSECT_GLAIVE:
                    intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
                            "Insect Glaive");
                    break;
				case LIGHT_BOWGUN:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Light Bowgun");
					break;
				case HEAVY_BOWGUN:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE,
							"Heavy Bowgun");
					break;
				case BOW:
					intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE, "Bow");
					break;
				}
				c.startActivity(intent);
			}
		}
	}
}