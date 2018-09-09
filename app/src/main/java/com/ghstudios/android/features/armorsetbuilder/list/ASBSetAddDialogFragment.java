package com.ghstudios.android.features.armorsetbuilder.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ghstudios.android.mhgendatabase.R;

public class ASBSetAddDialogFragment extends DialogFragment {
    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_RANK = "rank";
    private static final String ARG_HUNTER_TYPE = "hunter_type";

    private boolean isEditing;

    public static ASBSetAddDialogFragment newInstance() {
        ASBSetAddDialogFragment f = new ASBSetAddDialogFragment();
        f.isEditing = false;
        return f;
    }

    public static ASBSetAddDialogFragment newInstance(long id, String name, int rank, int hunterType) {
        ASBSetAddDialogFragment f = new ASBSetAddDialogFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putInt(ARG_RANK, rank);
        args.putInt(ARG_HUNTER_TYPE, hunterType);

        f.setArguments(args);
        f.isEditing = true;

        return f;
    }

    private void sendResult(int resultCode, String name, int rank, int hunterType) {
        if (getTargetFragment() == null)
            return;

        Intent i = new Intent();
        if (isEditing) {
            i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, getArguments().getLong(ARG_ID));
        }
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_NAME, name);
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_RANK, rank);
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_HUNTER_TYPE, hunterType);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View addView = inflater.inflate(R.layout.dialog_asb_set_add, null);

        final EditText nameInput = addView.findViewById(R.id.name_text);
        final Spinner rankSpinner = addView.findViewById(R.id.spinner_rank);
        final Spinner hunterTypeSpinner = addView.findViewById(R.id.spinner_hunter_type);

        rankSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.rank, R.layout.view_spinner_item));
        ((ArrayAdapter) rankSpinner.getAdapter()).setDropDownViewResource(R.layout.view_spinner_dropdown_item);

        hunterTypeSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.hunter_type, R.layout
                .view_spinner_item));
        ((ArrayAdapter) hunterTypeSpinner.getAdapter()).setDropDownViewResource(R.layout.view_spinner_dropdown_item);

        if (isEditing) {
            nameInput.setText(getArguments().getString(ARG_NAME));
            rankSpinner.setSelection(getArguments().getInt(ARG_RANK));
            hunterTypeSpinner.setSelection(getArguments().getInt(ARG_HUNTER_TYPE));
        }

        Dialog d = new AlertDialog.Builder(getActivity())
                .setTitle(!isEditing ? R.string.dialog_title_add_asb_set : R.string.dialog_title_edit_asb_set)
                .setView(addView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    String name = nameInput.getText().toString();

                    int rankIdx = rankSpinner.getSelectedItemPosition();
                    int hunterTypeIdx = hunterTypeSpinner.getSelectedItemPosition();
                    sendResult(Activity.RESULT_OK, name, rankIdx, hunterTypeIdx);
                })
                .create();

        // Allow the auto-focused name input to pop up the onscreen keyboard
        Window window = d.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return d;
    }
}
