package com.example.brandon.SubscriptionsManager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SubscriptionsFragment extends Fragment
{

    private SubscriptionsDatabase entriesDB = null;
    private LinearLayout subscriptionsContainer;
    private Typeface fontAwesome;

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

        ScrollView scrollView = (ScrollView)view.findViewById(R.id.subscriptions);
        subscriptionsContainer = (LinearLayout)scrollView.findViewById(R.id.subscriptionsContainer);

        fontAwesome = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");

        fillSubscriptionsInActivity(entriesDB.getSubscriptions());

        return view;
    }

    public void fillSubscriptionsInActivity(Subscriptions[] subscriptions)
    {
        if(subscriptionsContainer.getChildCount() > 0) {
            subscriptionsContainer.removeAllViews();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 45);

        for (Subscriptions subscription : subscriptions)
        {
            View newView = subscription.getView(getContext(), fontAwesome);

            subscriptionsContainer.addView(newView, layoutParams);
        }
    }
}
