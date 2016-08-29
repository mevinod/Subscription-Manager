package com.example.brandon.SubscriptionsManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class NewSubscriptionActivity extends ActionBarActivity
{
    Typeface fontAwesome = null;

    private Subscriptions[] brandSubscriptions;
    private LinearLayout subscriptionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_subscription_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.new_subscription_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        ScrollView scrollView = (ScrollView)findViewById(R.id.subscriptionsScrollView);
        scrollView.setVerticalScrollBarEnabled(false);

        subscriptionsContainer = (LinearLayout)scrollView.findViewById(R.id.subscriptionsScrollViewContent);

        BrandSubscriptions brandSubscriptionsDB = new BrandSubscriptions(this);
        brandSubscriptions = brandSubscriptionsDB.getSubscriptions();

        fillSubscriptionsInActivity(brandSubscriptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_new_subscription, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    fillSubscriptionsInActivity(searchBrandSubscriptions(query));
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query)
                {
                    fillSubscriptionsInActivity(searchBrandSubscriptions(query));
                    return false;
                }
            }
        );

        return true;
    }

    public Subscriptions[] searchBrandSubscriptions(String query)
    {
        ArrayList<Subscriptions> results = new ArrayList<Subscriptions>();

        for(Subscriptions subscription: brandSubscriptions)
        {
            String name = subscription.getName().toLowerCase();
            if(name.contains(query.toLowerCase()))
            {
                results.add(subscription);
            }
        }

        return results.toArray(new Subscriptions[results.size()]);
    }

    public void fillSubscriptionsInActivity(Subscriptions[] subscriptions)
    {
        subscriptionsContainer.removeAllViews();

        if(subscriptions.length != 0)
        {
            for (Subscriptions subscription : subscriptions)
            {
                View newView = subscription.getView(this, fontAwesome);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(0, 0, 0, 24);

                subscriptionsContainer.addView(newView, layoutParams);
            }
        }
        else
        {
            View blankView = View.inflate(this, R.layout.no_templates_found, null);
            subscriptionsContainer.addView(blankView);
        }

    }

    public void startCustomSubscriptionActivity(View view)
    {
        Toast.makeText(NewSubscriptionActivity.this, "Would create custom subscription activity", Toast.LENGTH_SHORT).show();
    }

    public class BrandSubscriptions extends SQLiteAssetHelper
    {

        private static final String DATABASE_NAME = "BrandSubscriptions.sql";
        private static final int DATABASE_VERSION = 1;

        public BrandSubscriptions(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public Subscriptions[] getSubscriptions()
        {

            SQLiteDatabase db = getReadableDatabase();

            Cursor c = db.rawQuery("SELECT * FROM subscriptions", null);
            c.moveToFirst();

            int subsLength = c.getCount();
            Subscriptions[] results = new Subscriptions[subsLength];

            for(int i = 0; i < subsLength; ++i)
            {
                String name = c.getString(c.getColumnIndex("name"));

                String colorString = c.getString(c.getColumnIndex("color"));
                int color = Color.parseColor(colorString);

                String iconHTML = c.getString(c.getColumnIndex("icon"));
                String icon = Html.fromHtml(iconHTML).toString();

                results[i] = new Subscriptions(icon, color, name, "", -1f,
                        Subscriptions.billingCycle.MONTHLY, 0, Subscriptions.reminders.NEVER);

                c.moveToNext();
            }

            c.close();
            db.close();

            return results;
        }
    }
}