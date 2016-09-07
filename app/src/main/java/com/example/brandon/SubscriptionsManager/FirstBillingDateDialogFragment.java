package com.example.brandon.SubscriptionsManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class FirstBillingDateDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private ArrayList<OnFinishedListener> onFinishedListeners = new ArrayList<OnFinishedListener>();

    public void setOnFinishedListener(OnFinishedListener listener){
        onFinishedListeners.add(listener);
    }

    public interface OnFinishedListener{
        void onFinishedWithResult(String monthName, int day, int year, long time);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Bundle args = getArguments();
        Long time = args.getLong("date_in_milliseconds", Subscriptions.today());

        if(time == -1){
            time = Subscriptions.today();
        }

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMinDate(Subscriptions.today());

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        for(OnFinishedListener listener: onFinishedListeners){
            String monthString = getResources().getStringArray(R.array.month_names)[month];

            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            long time = c.getTimeInMillis();

            listener.onFinishedWithResult(monthString, day, year, time);
        }
    }
}
