package com.ghstudios.android.ui.detail;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.support.v4.content.Loader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.CursorAdapter;
//import android.support.v7.internal.widget.ViewUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Melody;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.data.database.HornMelodiesCursor;
import com.ghstudios.android.loader.WeaponLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.MHUtils;
import com.ghstudios.android.ui.general.DrawSharpness;
import com.ghstudios.android.loader.HornMelodyListCursorLoader;

public class WeaponBladeDetailFragment extends WeaponDetailFragment{

	private TextView mWeaponSpecialTypeTextView, mWeaponSpecialTextView,
            mWeaponElementTextView,mWeaponElementTypeTextView,mWeaponNoteText;
	private ImageView mWeaponNote1ImageView,
			mWeaponNote2ImageView, mWeaponNote3ImageView;
    private DrawSharpness mWeaponSharpnessDrawnView;
    private View DividerView;
    View NoteContainer;

    // Public because this needs to be accessed by superclass WeaponDetailFragment
    public  ListView mWeaponHornMelodiesListView;

	public static WeaponBladeDetailFragment newInstance(long weaponId) {
		Bundle args = new Bundle();
		args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId);
		WeaponBladeDetailFragment f = new WeaponBladeDetailFragment();
		f.setArguments(args);
		return f;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Override and recreate calls from WeaponDetailFragment
        // so we can call HornMelodyLoader after WeaponLoader is finished ----------------------
        setRetainInstance(true);

        // Check for a Weapon ID as an argument, and find the weapon
        Bundle args = getArguments();
        if (args != null) {
            long weaponId = args.getLong(ARG_WEAPON_ID, -1);
            if (weaponId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(R.id.weapon_detail_fragment, args, new WeaponLoaderCallbacks2());
            }
        }
        //-------------------------------------------------------------------------------------

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.weapon_style_test,
				container, false);

		//mWeaponLabelTextView = (TextView) view
		//		.findViewById(R.id.detail_weapon_name);
        mWeaponDescription = (TextView) view
                .findViewById(R.id.detail_weapon_description);
		mWeaponTypeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_type);
		mWeaponAttackTextView = (TextView) view
				.findViewById(R.id.detail_weapon_attack);
		mWeaponElementTextView = (TextView) view
				.findViewById(R.id.detail_weapon_element);
        mWeaponElementTypeTextView = (TextView)view.findViewById(R.id.detail_weapon_element_text);
		mWeaponSharpnessDrawnView = (DrawSharpness) view
				.findViewById(R.id.detail_weapon_blade_sharpness);
		mWeaponRarityTextView = (TextView) view
				.findViewById(R.id.detail_weapon_rarity);
		mWeaponSlotTextView = (TextView) view
				.findViewById(R.id.detail_weapon_slot);
		mWeaponAffinityTextView = (TextView) view
				.findViewById(R.id.detail_weapon_affinity);
		mWeaponDefenseTextView = (TextView) view
				.findViewById(R.id.detail_weapon_defense);
        mWeaponDefenseTextTextView=(TextView) view
                .findViewById(R.id.detail_weapon_defense_text);
		mWeaponCreationTextView = (TextView) view
				.findViewById(R.id.detail_weapon_creation);
		mWeaponUpgradeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_upgrade);
		mWeaponSpecialTypeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_blade_special);
		mWeaponSpecialTextView = (TextView) view
				.findViewById(R.id.detail_weapon_blade_special_value);
        mWeaponNoteText = (TextView)view.findViewById(R.id.detail_weapon_blade_note_text);
		NoteContainer = view.findViewById(R.id.detail_weapon_note_container);
		mWeaponNote1ImageView = (ImageView) view
				.findViewById(R.id.detail_weapon_blade_note1);
		mWeaponNote2ImageView = (ImageView) view
				.findViewById(R.id.detail_weapon_blade_note2);
		mWeaponNote3ImageView = (ImageView) view
				.findViewById(R.id.detail_weapon_blade_note3);

	//	mWeaponHornMelodiesListView = (ListView) view
      //          .findViewById(R.id.horn_melodies_list);

        //DividerView = view.findViewById(R.id.divider);

		return view;
	}

	@Override
	protected void updateUI() throws IOException {
		super.updateUI();

		/* Sharpness */
		mWeaponSharpnessDrawnView.init(mWeapon.getSharpness1(),mWeapon.getSharpness2(),mWeapon.getSharpness3());
        // Redraw sharpness after data is loaded
        mWeaponSharpnessDrawnView.invalidate();

        // String notes to use in notes display and song list
        String notes = "";

		// Read a Bitmap from Assets
		AssetManager manager = getActivity().getAssets();
		InputStream open = null;
		Bitmap bitmap = null;
		
		/* Hunting Horn notes */
		if (mWeapon.getWtype().equals("Hunting Horn")) {
//            DividerView.setVisibility(View.VISIBLE);
//
//			mWeaponSpecialTypeTextView.setText("Horn Notes:");
			
			notes = mWeapon.getHornNotes();
			
			try {
                open = manager.open(getNoteImage(notes.charAt(0)));
                bitmap = BitmapFactory.decodeStream(open);
                mWeaponNote1ImageView.setImageBitmap(bitmap);
				
				open = manager.open(getNoteImage(notes.charAt(1)));
				bitmap = BitmapFactory.decodeStream(open);
                mWeaponNote2ImageView.setImageBitmap(bitmap);
				
				open = manager.open(getNoteImage(notes.charAt(2)));
				bitmap = BitmapFactory.decodeStream(open);
                mWeaponNote3ImageView.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (open != null) {
					open.close();
				}
			}
		}
        else
        {
            mWeaponNoteText.setVisibility(View.GONE);
            NoteContainer.setVisibility(View.GONE);
        }
		
		/* Gunlance */
		if (mWeapon.getWtype().equals("Gunlance")) {
			mWeaponSpecialTypeTextView.setText("Shelling");
			mWeaponSpecialTextView.setText(mWeapon.getShellingType());
		}

        /* Switch Axe */
		else if (mWeapon.getWtype().equals("Switch Axe")) {
			mWeaponSpecialTypeTextView.setText("Phial");
			mWeaponSpecialTextView.setText(mWeapon.getPhial());
		}
        else if(mWeapon.getWtype().equals("Charge Blade")){
            mWeaponSpecialTypeTextView.setText("Phial");
            mWeaponSpecialTextView.setText(mWeapon.getPhial());
        }
        else{
            mWeaponSpecialTextView.setVisibility(View.GONE);
            mWeaponSpecialTypeTextView.setVisibility(View.GONE);
        }

        /* Element */
        String element = "";
        if (!mWeapon.getElement().equals(""))
        {
            mWeaponElementTextView.setText(Long.toString(mWeapon.getElementAttack()));
            mWeaponElementTypeTextView.setText(mWeapon.getElement());
        }
        else
        {
            mWeaponElementTextView.setText("0");
            mWeaponElementTypeTextView.setText("None");
        }

        if (!"".equals(mWeapon.getElement2())) {
            mWeaponElementTypeTextView.setText(mWeapon.getElement() + " " +mWeapon.getElement2());
            mWeaponElementTextView.setText(mWeapon.getElementAttack() + " " + mWeapon.getElement2Attack());
        }

	}

    public static class HornMelodiesCursorAdapter extends CursorAdapter {

        private HornMelodiesCursor mHornMelodiesCursor;

        public HornMelodiesCursorAdapter(Context context,
                                         HornMelodiesCursor cursor) {
            super(context, cursor, 0);
            mHornMelodiesCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_horn_melody_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the Melody for the current row
            Melody melody = mHornMelodiesCursor.getMelody();

            // Get assignable TextViews
            TextView effect1TextView = (TextView) view.findViewById(R.id.effect1);
            TextView effect2TextView = (TextView) view.findViewById(R.id.effect2);
            TextView durationTextView = (TextView) view.findViewById(R.id.duration);
            TextView extensionTextView = (TextView) view.findViewById(R.id.extension);

            // Get assignable ImageViews
            ImageView note1ImageView = (ImageView) view.findViewById(R.id.horn_note1);
            ImageView note2ImageView = (ImageView) view.findViewById(R.id.horn_note2);
            ImageView note3ImageView = (ImageView) view.findViewById(R.id.horn_note3);
            ImageView note4ImageView = (ImageView) view.findViewById(R.id.horn_note4);

            // Assign Effect 1
            String cellText = melody.getEffect1();
            effect1TextView.setText(cellText);

            // Assign Effect 2
            cellText = melody.getEffect2();
            if(!cellText.equals("N/A")) {
                effect2TextView.setText(cellText);
                effect2TextView.setVisibility(View.VISIBLE);
            } else {
                effect2TextView.setVisibility(View.GONE);
            }

            // Assign Duration
            cellText = "DUR: " + melody.getDuration();
            if(!cellText.equals("DUR: N/A")) {
                durationTextView.setText(cellText);
                durationTextView.setVisibility(View.VISIBLE);
            } else {
                durationTextView.setVisibility(View.GONE);
            }

            // Assign Extension
            cellText = "EXT: " + melody.getExtension();
            if(!cellText.equals("EXT: N/A")) {
                extensionTextView.setText(cellText);
                extensionTextView.setVisibility(View.VISIBLE);
            } else {
                extensionTextView.setVisibility(View.GONE);
            }

            // Get string version of song
            String song = melody.getSong();

            // Read a Bitmap from Assets
            AssetManager manager = context.getAssets();
            InputStream open = null;
            Bitmap bitmap = null;
            try {
                // Note 1
                if(song.length()>=1) {
                    open = manager.open(getNoteImage(song.charAt(0)));
                    bitmap = BitmapFactory.decodeStream(open);
                    note1ImageView.setImageBitmap(bitmap);
                }
                else note1ImageView.setImageBitmap(null);
                // Note 2
                if(song.length()>=2) {
                    open = manager.open(getNoteImage(song.charAt(1)));
                    bitmap = BitmapFactory.decodeStream(open);
                    note2ImageView.setImageBitmap(bitmap);
                }
                else note2ImageView.setImageBitmap(null);
                // Note 3
                if(song.length()>=3) {
                    open = manager.open(getNoteImage(song.charAt(2)));
                    bitmap = BitmapFactory.decodeStream(open);
                    note3ImageView.setImageBitmap(bitmap);
                }
                else note3ImageView.setImageBitmap(null);
                // Note 4
                if(song.length()>=4) {
                    open = manager.open(getNoteImage(song.charAt(3)));
                    bitmap = BitmapFactory.decodeStream(open);
                    note4ImageView.setImageBitmap(bitmap);
                }
                else note4ImageView.setImageBitmap(null);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (open != null) {
                    // Close input stream
                    try{open.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getNoteImage(char note) {
        String file = "icons_monster_info/";

        switch (note) {
            case 'B':
                return file + "Note.blue.png";
            case 'C':
                return file + "Note.aqua.png";
            case 'G':
                return file + "Note.green.png";
            case 'O':
                return file + "Note.orange.png";
            case 'P':
                return file + "Note.purple.png";
            case 'R':
                return file + "Note.red.png";
            case 'W':
                return file + "Note.white.png";
            case 'Y':
                return file + "Note.yellow.png";
        }
        return "";
    }

    // This class is replicating the one from WeaponDetailFragment so we can load HornMelodiesLoaderCallbacks afterwards
    public class WeaponLoaderCallbacks2 implements LoaderManager.LoaderCallbacks<Weapon> {

        @Override
        public Loader<Weapon> onCreateLoader(int id, Bundle args) {
            return new WeaponLoader(getActivity(), args.getLong(ARG_WEAPON_ID));
        }

        @Override
        public void onLoadFinished(Loader<Weapon> loader, Weapon run) {
            mWeapon = run;

            try {
                updateUI();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

//            if(mWeapon.getWtype().equals("Hunting Horn")) {
//                // Initialize the loader to load the list horn melodies
//                getLoaderManager().initLoader(R.id.horn_melodies_list, null, new HornMelodiesLoaderCallbacks());
//            }
        }

        @Override
        public void onLoaderReset(Loader<Weapon> loader) {
            // Do nothing
        }
    }
}
