package com.example.brandon.SubscriptionsManager;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.icon_ids);
        Integer iconIds[] = new Integer[icons.length()];
        for(int i = 0; i < icons.length(); ++i){
            iconIds[i] = icons.getResourceId(i, -1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select the icon");

        // Prepare grid view
        GridView gridView = new GridView(getContext());

        final ArrayAdapter arrayAdapter = new IconArrayAdapter(getContext(), iconIds);
        gridView.setAdapter(arrayAdapter);

        gridView.setNumColumns(4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something here
                Integer iconId = (Integer)arrayAdapter.getItem(position);
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

class IconArrayAdapter extends ArrayAdapter<Integer> {
    Context context;

    public IconArrayAdapter(Context context, Integer[] items){
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Integer icon = getItem(position);
        ImageView view = new ImageView(context);
        view.setImageResource(icon);
        view.setScaleX(1.5f);
        view.setScaleY(1.5f);
        view.setPadding(10, 30, 10, 30);

        return view;
    }
}
