package com.ghstudios.android.features.wishlist.list;

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
import com.ghstudios.android.data.WishlistManager;
import com.ghstudios.android.mhgendatabase.R;

public class WishlistAddDialogFragment extends DialogFragment {
    public static final String EXTRA_ADD =
            "com.daviancorp.android.ui.general.wishlist_add";
    
    private void sendResult(int resultCode, boolean add) {
        if (getTargetFragment() == null)
            return;
        
        Intent i = new Intent();
        i.putExtra(EXTRA_ADD, add);
        
        getTargetFragment()
            .onActivityResult(getTargetRequestCode(), resultCode, i);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        
        View addView = inflater.inflate(R.layout.dialog_wishlist_add, null);
        final EditText nameInput = (EditText) addView.findViewById(R.id.name_text);
        
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.option_wishlist_add)
            .setView(addView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int id) -> {
                WishlistManager manager = DataManager.get().getWishlistManager();

                String name = nameInput.getText().toString();
                manager.addWishlist(name);

                String message = getString(R.string.wishlist_add_item, name);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                sendResult(Activity.RESULT_OK, true);
            })
            .create();
    }
}
