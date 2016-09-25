package com.example.brandon.SubscriptionsManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class NewSubscriptionActivity extends ActionBarActivity {
    Typeface fontAwesome = null;

    private Subscriptions[] brandSubscriptions;

    private Subscriptions[] displayedSubscriptions;
    private LinearLayout subscriptionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        displayedSubscriptions = brandSubscriptions.clone();

        fillSubscriptionsInActivity(brandSubscriptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_subscription, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Subscriptions[] results = searchBrandSubscriptions(query);
                    if(!Subscriptions.checkArraysEqual(results, displayedSubscriptions)) {
                        fillSubscriptionsInActivity(results);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    Subscriptions[] results = searchBrandSubscriptions(query);
                    if(!Subscriptions.checkArraysEqual(results, displayedSubscriptions)) {
                        fillSubscriptionsInActivity(results);
                    }
                    return false;
                }
            }
        );

        return true;
    }

    public Subscriptions[] searchBrandSubscriptions(String query) {
        ArrayList<Subscriptions> results = new ArrayList<Subscriptions>();

        for(Subscriptions subscription: brandSubscriptions) {
            String name = subscription.getName().toLowerCase();
            if(name.contains(query.toLowerCase())) {
                results.add(subscription);
            }
        }

        return results.toArray(new Subscriptions[results.size()]);
    }

    public void fillSubscriptionsInActivity(Subscriptions[] subscriptions) {
        displayedSubscriptions = subscriptions.clone();
        subscriptionsContainer.removeAllViews();

        if(subscriptions.length != 0) {
            for (Subscriptions subscription : subscriptions) {
                View newView = subscription.getView(this, fontAwesome);
                newView.findViewById(R.id.nextPaymentDate).setVisibility(View.GONE);

                newView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startTemplateSubscriptionActivity(view);
                    }
                });

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(0, 0, 0, 45);

                subscriptionsContainer.addView(newView, layoutParams);
            }
        }
        else {
            View blankView = View.inflate(this, R.layout.no_templates_found, null);
            subscriptionsContainer.addView(blankView);
        }
    }

    public void startCustomSubscriptionActivity(View view) {
        Intent launchActivity = new Intent(this, CustomSubscriptionActivity.class);
        startActivityForResult(launchActivity, 0);
    }

    public void startTemplateSubscriptionActivity(View view){
        Intent launchActivity = new Intent(NewSubscriptionActivity.this,
                TemplateSubscriptionActivity.class);

        int index = subscriptionsContainer.indexOfChild(view);

        Subscriptions templateSubscription = displayedSubscriptions[index];
        templateSubscription.setAmount(BigDecimal.valueOf(0f));

        launchActivity.putExtra("subscription", templateSubscription);
        startActivityForResult(launchActivity, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    public class BrandSubscriptions extends SQLiteAssetHelper {

        private static final String DATABASE_NAME = "BrandSubscriptions.sql";
        private static final int DATABASE_VERSION = 1;

        public BrandSubscriptions(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public Subscriptions[] getSubscriptions() {
            SQLiteDatabase db = getReadableDatabase();

            Cursor c = db.rawQuery("SELECT * FROM subscriptions", null);
            c.moveToFirst();

            int subsLength = c.getCount();
            Subscriptions[] results = new Subscriptions[subsLength];

            for(int i = 0; i < subsLength; ++i) {
                String name = c.getString(c.getColumnIndex("name"));

                String colorString = c.getString(c.getColumnIndex("color"));
                int color = Color.parseColor(colorString);

                String iconHTML = c.getString(c.getColumnIndex("icon"));
                String icon = Html.fromHtml(iconHTML).toString();

                results[i] = new Subscriptions(icon, color, name, "", BigDecimal.valueOf(-1f),
                        Subscriptions.billingCycle.MONTHLY, -1,
                        0, Subscriptions.reminders.NEVER);

                c.moveToNext();
            }

            c.close();
            db.close();

            return results;
        }
    }
}