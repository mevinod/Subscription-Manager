package com.mevinod.brandon.SubscriptionsManager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.mevinod.brandon.SubscriptionsManager.R;

import java.util.ArrayList;

public class DeleteSubscriptionDialog extends AlertDialog.Builder {
    private ArrayList<OnDeleteClicked> onDeleteClickedListeners = new ArrayList<OnDeleteClicked>();

    public void setOnDeleteClickedListener(OnDeleteClicked listener){
        onDeleteClickedListeners.add(listener);
    }

    public interface OnDeleteClicked{
        void onDeleteClicked(int index);
    }

    Context context;
    int index;

    AlertDialog dialog;

    public DeleteSubscriptionDialog(Context context, int index){
        super(context);
        this.context = context;
        this.index = index;
    }

    @Override
    public AlertDialog show() {
        super.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(context.getResources().getColor(R.color.red));

        return dialog;
    }

    @Override
    public AlertDialog create() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Subscription will be deleted")
                .setTitle("Are you sure?")
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for(OnDeleteClicked listeners: onDeleteClickedListeners){
                            listeners.onDeleteClicked(index);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();

        return dialog;
    }
}