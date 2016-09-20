package com.example.brandon.SubscriptionsManager;

import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChangeIconDialogFragment extends DialogFragment {
    private ArrayList<OnFinishedListener> onFinishedListeners = new ArrayList<OnFinishedListener>();

    public void setOnFinishedListener(OnFinishedListener listener){
        onFinishedListeners.add(listener);
    }

    public interface OnFinishedListener{
        void onFinishedWithResult(int iconId);
    }

    private int iconId;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        iconId = args.getInt("icon_id");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Select the icon");

        // Prepare grid view
        GridView gridView = new GridView(getContext());

        final ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(
                getContext(), android.R.layout.select_dialog_item);


        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.icon_ids);
        Integer iconIds[] = new Integer[icons.length()];
        for(int i = 0; i < icons.length(); ++i){
            iconIds[i] = icons.getResourceId(i, -1);
        }
        arrayAdapter.addAll(iconIds);
        gridView.setAdapter(arrayAdapter);

        gridView.setNumColumns(4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something here
                Integer iconId = arrayAdapter.getItem(position);
                for(OnFinishedListener listener: onFinishedListeners){
                    listener.onFinishedWithResult(iconId);
                }
                dismiss();
            }
        });

        builder.setView(gridView);
        icons.recycle();

        return builder.create();
    }
}
