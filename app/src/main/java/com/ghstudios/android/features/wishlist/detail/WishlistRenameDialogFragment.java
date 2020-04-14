package com.ghstudios.android.features.wishlist.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.util.ExtensionsKt;

public class WishlistRenameDialogFragment extends DialogFragment {
    public static final String EXTRA_RENAME =
            "com.daviancorp.android.ui.general.wishlist_rename";
    private static final String ARG_WISHLIST_ID = "WISHLIST_ID";
    private static final String ARG_WISHLIST_NAME = "WISHLIST_NAME";

    public static WishlistRenameDialogFragment newInstance(long id, String name) {
        Bundle args = new Bundle();
        args.putLong(ARG_WISHLIST_ID, id);
        args.putString(ARG_WISHLIST_NAME, name);
        WishlistRenameDialogFragment f = new WishlistRenameDialogFragment();
        f.setArguments(args);
        return f;
    }
    
    private void sendResult(int resultCode, boolean rename) {
        Intent i = new Intent();
        i.putExtra(EXTRA_RENAME, rename);
        ExtensionsKt.sendDialogResult(this, resultCode, i);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        View addView = inflater.inflate(R.layout.dialog_wishlist_rename, null);
        final EditText nameInput = (EditText) addView.findViewById(R.id.rename);

        long wishlistId = getArguments().getLong(ARG_WISHLIST_ID);
        
        return new AlertDialog.Builder(getActivity())
            .setTitle("Rename '" + getArguments().getString(ARG_WISHLIST_NAME) + "' wishlist?")
            .setView(addView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int id) -> {
                String name = nameInput.getText().toString();
                DataManager.get().getWishlistManager().updateWishlistName(wishlistId, name);

                String message = getString(R.string.wishlist_renamed, name);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                sendResult(Activity.RESULT_OK, true);
            })
            .create();
    }
}
