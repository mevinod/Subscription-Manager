package com.mevinod.brandon.SubscriptionsManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.mevinod.brandon.SubscriptionsManager.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RemindersDialogFragment extends DialogFragment {
    private ArrayList<OnFinishedListener> onFinishedListeners = new ArrayList<OnFinishedListener>();

    public void setOnFinishedListener(OnFinishedListener listener){
        onFinishedListeners.add(listener);
    }

    public interface OnFinishedListener{
        void onFinishedWithResult(int index, String name);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Remind me");

        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.select_dialog_item);

        arrayAdapter.addAll(getResources().getStringArray(R.array.reminders));

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        for(OnFinishedListener listener: onFinishedListeners){
                            listener.onFinishedWithResult(which, strName);
                        }
                    }
                });

        return builder.create();
    }
}
