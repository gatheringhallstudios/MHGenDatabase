package com.ghstudios.android.features.weapons.detail

import android.content.Context
import android.database.Cursor
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.data.classes.Melody
import com.ghstudios.android.data.cursors.HornMelodiesCursor
import com.ghstudios.android.loader.HornMelodyListCursorLoader
import com.ghstudios.android.mhgendatabase.R

/**
 * Fragment used to display the list of songs supported by a hunting horn.
 * Created by Joseph on 7/1/2016.
 */
class WeaponSongFragment : ListFragment(), LoaderCallbacks<Cursor> {
    companion object {
        private const val ARG_WEAPON_ID = "WEAPON_ID"

        @JvmStatic fun newInstance(weaponId: Long): WeaponSongFragment {
            val args = Bundle()
            args.putLong(ARG_WEAPON_ID, weaponId)
            val f = WeaponSongFragment()
            f.arguments = args
            return f
        }
    }

    private var mWeaponId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the loader to load the list of runs
        loaderManager.initLoader(R.id.horn_melodies_list_fragment, arguments, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // You only ever load the runs, so assume this is the case
        mWeaponId = args!!.getLong(ARG_WEAPON_ID, -1)

        return HornMelodyListCursorLoader(activity, mWeaponId)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        // Create an adapter to point at this cursor
        val adapter = HornMelodiesCursorAdapter(
                activity!!, cursor as HornMelodiesCursor)
        listAdapter = adapter
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Stop using the cursor (via the adapter)
        listAdapter = null
    }

    class HornMelodiesCursorAdapter(context: Context,
                                    private val mHornMelodiesCursor: HornMelodiesCursor) : CursorAdapter(context, mHornMelodiesCursor, 0) {

        override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
            // Use a layout inflater to get a row view
            val inflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return inflater.inflate(R.layout.fragment_horn_melody_listitem,
                    parent, false)
        }

        override fun bindView(view: View, context: Context, cursor: Cursor) {
            // Get the Melody for the current row
            val melody = mHornMelodiesCursor.melody

            // Get assignable TextViews
            val nameTextView = view.findViewById<TextView>(R.id.name)
            val effect1TextView = view.findViewById<TextView>(R.id.effect1)
            val effect2TextView = view.findViewById<TextView>(R.id.effect2)
            val durationTextView = view.findViewById<TextView>(R.id.duration)
            val extensionTextView = view.findViewById<TextView>(R.id.extension)

            // Get assignable ImageViews
            val noteImages = listOf<ImageView>(
                    view.findViewById(R.id.horn_note1),
                    view.findViewById(R.id.horn_note2),
                    view.findViewById(R.id.horn_note3),
                    view.findViewById(R.id.horn_note4)
            )

            // Assign name
            nameTextView.text = melody?.name

            // Assign Effects
            effect1TextView.text = melody.effect1
            effect2TextView.text = melody.effect2

            // Assign Duration and extension
            durationTextView.text = context.getString(R.string.weapon_melody_duration, melody.duration)
            extensionTextView.text = context.getString(R.string.weapon_melody_extension, melody.extension)

            // Assign Song
            val notes = melody.song.toCharArray().toList()
            noteImages.forEach { it.visibility = View.GONE }
            for ((note, imageView) in notes.zip(noteImages)) {
                val color = ContextCompat.getColor(mContext, MHUtils.getNoteColor(note))

                imageView.setImageResource(R.drawable.icon_music_note)
                imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                imageView.visibility = View.VISIBLE
            }
        }
    }
}
