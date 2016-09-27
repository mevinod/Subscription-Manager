package com.example.brandon.SubscriptionsManager;

import android.app.Activity;
import android.app.ActivityOptions;
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

public class NewSubscriptionActivity extends ActionBarActivity {
    Typeface fontAwesome = null;

    private Subscriptions[] brandSubscriptions;

    private Subscriptions[] displayedSubscriptions;
    private LinearLayout subscriptionsContainer;

    View blankView;

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

        fillSubscriptionsInActivity(displayedSubscriptions);

        blankView = View.inflate(this, R.layout.no_templates_found, null);
        subscriptionsContainer.addView(blankView);
        blankView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_subscription, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    boolean[] results = searchBrandSubscriptions(query);
                    updateLayout(results);

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    boolean[] results = searchBrandSubscriptions(query);
                    updateLayout(results);

                    return false;
                }
            }
        );

        return true;
    }

    public boolean[] searchBrandSubscriptions(String query) {
        boolean results[] = new boolean[brandSubscriptions.length + 1];

        boolean dif = true;

        for(int i = 0; i < brandSubscriptions.length; i++){
            String name = brandSubscriptions[i].getName().toLowerCase();
            results[i + 1] = name.contains(query.toLowerCase());
            dif &= results[i + 1];
        }

        results[0] = dif;

        return results;
    }

    public void updateLayout(boolean values[]){
        boolean emptyList;

        int count = 0;
        for(int i = 1; i < values.length; i++){
            count += values[i] ? 0:1;
        }
        emptyList = (count == (values.length - 1));

        if(!emptyList){
            blankView.setVisibility(View.GONE);
        }

        for(int i = 0; i < values.length - 1; i++){
            int visibility = values[i + 1]? View.VISIBLE : View.GONE;
            subscriptionsContainer.getChildAt(i).setVisibility(visibility);
        }

        if(emptyList){
            blankView.setVisibility(View.VISIBLE);
        }
    }

    public void fillSubscriptionsInActivity(Subscriptions[] subscriptions) {
        displayedSubscriptions = subscriptions.clone();

        subscriptionsContainer.removeAllViewsInLayout();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 45);

        if(subscriptions.length != 0) {

            for(int i = 0; i < subscriptions.length; i++){
                View newView = brandSubscriptions[i].getView(this, fontAwesome);
                newView.findViewById(R.id.nextPaymentDate).setVisibility(View.GONE);

                newView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startTemplateSubscriptionActivity(view);
                    }
                });
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

        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, view, "subscriptionView");

        startActivityForResult(launchActivity, 0, options.toBundle());
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

        private static final String DATABASE_NAME = "BrandSubscriptions2.sql";
        private static final int DATABASE_VERSION = 2;

        BrandSubscriptions(Context context) {
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

                String subType = c.getString(c.getColumnIndex("type"));

                if(subType.equals("font")){
                    String iconHTML = c.getString(c.getColumnIndex("icon"));
                    String icon = Html.fromHtml(iconHTML).toString();

                    results[i] = new Subscriptions(icon, color, name, "", BigDecimal.valueOf(-1f),
                            Subscriptions.billingCycle.MONTHLY, -1,
                            0, Subscriptions.reminders.NEVER);
                }
                else if(subType.equals("image_id")){
                    String iconText = c.getString(c.getColumnIndex("icon"));
                    int icon = getResources().getIdentifier(iconText, "drawable", getPackageName());

                    results[i] = new Subscriptions(icon, color, name, "", BigDecimal.valueOf(-1f),
                            Subscriptions.billingCycle.MONTHLY, -1,
                            0, Subscriptions.reminders.NEVER);
                }

                c.moveToNext();
            }

            c.close();
            db.close();

            return results;
        }
    }
}