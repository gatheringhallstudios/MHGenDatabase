package com.ghstudios.android.features.skills;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.SkillTree;
import com.ghstudios.android.data.cursors.SkillTreeCursor;
import com.ghstudios.android.features.skills.detail.SkillTreeDetailPagerActivity;
import com.ghstudios.android.loader.SkillTreeListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.SkillClickListener;
import com.ghstudios.android.features.armorsetbuilder.detail.ASBDetailPagerActivity;

public class SkillTreeListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.skill_tree_list_fragment, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_generic_list, parent, false);
		//JOE:This list is never empty, so remove empty view to prevent flash
		View emptyView = v.findViewById(android.R.id.empty);
		((ViewGroup)emptyView.getParent()).removeView(emptyView);
		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		return new SkillTreeListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		if (getListAdapter() == null) {
			SkillTreeListCursorAdapter adapter = new SkillTreeListCursorAdapter(
					getActivity(), (SkillTreeCursor) cursor);
			setListAdapter(adapter);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private class SkillTreeListCursorAdapter extends CursorAdapter {

		private SkillTreeCursor mSkillTreeCursor;

		public SkillTreeListCursorAdapter(Context context,
				SkillTreeCursor cursor) {
			super(context, cursor, 0);
			mSkillTreeCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_skilltree_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the skill for the current row
            final SkillTree skilltree = mSkillTreeCursor.getSkillTree();
            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.listitem);

			// Set up the text view
			TextView skilltreeNameTextView = (TextView) view.findViewById(R.id.item);
			String cellText = skilltree.getName();
			skilltreeNameTextView.setText(cellText);

            if (getActivity().getIntent().getBooleanExtra(ASBDetailPagerActivity.EXTRA_FROM_TALISMAN_EDITOR, false)) {
                itemLayout.setOnClickListener(v -> {
                    Intent i = getActivity().getIntent();
                    i.putExtra(SkillTreeDetailPagerActivity.EXTRA_SKILLTREE_ID, skilltree.getId());

                    getActivity().setResult(Activity.RESULT_OK, i);
                    getActivity().finish();
                });
            }
            else {
                itemLayout.setOnClickListener(new SkillClickListener(context, skilltree.getId()));
            }
		}
	}

}
