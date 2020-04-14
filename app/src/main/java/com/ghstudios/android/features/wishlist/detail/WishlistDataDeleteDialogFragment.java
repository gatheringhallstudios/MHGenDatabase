package com.ghstudios.android.features.wishlist.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.Toast;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.util.ExtensionsKt;

public class WishlistDataDeleteDialogFragment extends DialogFragment {
    public static final String EXTRA_DELETE =
            "com.daviancorp.android.ui.general.wishlist_data_delete";
    
    private static final String ARG_WISHLIST_DATA_ID = "WISHLIST_DATA_ID";
    private static final String ARG_WISHLIST_DATA_NAME = "WISHLIST_DATA_NAME";

    public static WishlistDataDeleteDialogFragment newInstance(long id, String name) {
        Bundle args = new Bundle();
        args.putLong(ARG_WISHLIST_DATA_ID, id);
        args.putString(ARG_WISHLIST_DATA_NAME, name);
        WishlistDataDeleteDialogFragment f = new WishlistDataDeleteDialogFragment();
        f.setArguments(args);
        return f;
    }
    
    private void sendResult(int resultCode, boolean delete) {
        Intent i = new Intent();
        i.putExtra(EXTRA_DELETE, delete);
        ExtensionsKt.sendDialogResult(this, resultCode, i);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final long wishlistDataId = getArguments().getLong(ARG_WISHLIST_DATA_ID);
        final String name = getArguments().getString(ARG_WISHLIST_DATA_NAME);
        
        return new AlertDialog.Builder(getActivity())
            .setTitle("Delete '" + name + "' from wishlist?")
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int id) -> {
                DataManager.get().getWishlistManager().queryDeleteWishlistData(wishlistDataId);

                Toast.makeText(getActivity(), "Deleted '" + name + "'", Toast.LENGTH_SHORT).show();
                sendResult(Activity.RESULT_OK, true);
            })
            .create();
    }
}
