package com.example.brandon.SubscriptionsManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SubscriptionsFragment extends Fragment
{

    private SubscriptionsDatabase entriesDB = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.subscriptions_fragment, container, false);

        entriesDB = new SubscriptionsDatabase(getActivity());
        String newText = getResources().getString(R.string.monthly_payment) +
                String.valueOf(entriesDB.getTotalPayment());

        TextView paymentTextView = (TextView)view.findViewById(R.id.paymentTextView);
        paymentTextView.setText(newText);

        return view;
    }
}
