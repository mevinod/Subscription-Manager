package com.example.brandon.SubscriptionsManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

public class CustomSubscriptionActivity extends ActionBarActivity
{
    private Subscriptions newSubscription;

    private View     subscription;
    private Typeface fontAwesome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_form);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.custom_subscription_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        final EditText serviceName = (EditText)findViewById(R.id.serviceName);
        serviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newSubscription.setName(charSequence.toString());
                newSubscription.fillOutView(subscription, fontAwesome);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final EditText description = (EditText)findViewById(R.id.description);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newSubscription.setDescription(charSequence.toString());
                newSubscription.fillOutView(subscription, fontAwesome);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final EditText amount = (EditText)findViewById(R.id.amount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                float value = 0;
                try{
                    value = Float.parseFloat(charSequence.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
                newSubscription.setAmount(value);
                newSubscription.fillOutView(subscription, fontAwesome);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final EditText billingCycle = (EditText)findViewById(R.id.billingCycle);
        billingCycle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(CustomSubscriptionActivity.this).create();
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setTitle("Bill Manager");
                    alertDialog.setMessage(getString(R.string.info_string));
                    alertDialog.show();
                }

                return true;
            }
        });

        final EditText firstBillingDate = (EditText)findViewById(R.id.firstBillingDate);
        firstBillingDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(CustomSubscriptionActivity.this).create();
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setTitle("Bill Manager");
                    alertDialog.setMessage(getString(R.string.info_string));
                    alertDialog.show();
                }

                return true;
            }
        });

        final EditText reminders = (EditText)findViewById(R.id.reminders);
        reminders.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(CustomSubscriptionActivity.this).create();
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setTitle("Bill Manager");
                    alertDialog.setMessage(getString(R.string.info_string));
                    alertDialog.show();
                }

                return true;
            }
        });

        ViewStub subscriptionStubView = (ViewStub)findViewById(R.id.viewStub);
        subscription = subscriptionStubView.inflate();

        newSubscription = new Subscriptions(R.drawable.ic_add, getResources().getColor(R.color.black),
                "", "", 0f, Subscriptions.billingCycle.MONTHLY, 0, Subscriptions.reminders.NEVER);

        fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        newSubscription.fillOutView(subscription, fontAwesome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_subscription, menu);
        return true;
    }

    public void createSubscription(MenuItem item) {
        Intent resultIntent = new Intent();

        resultIntent.putExtra("newSubscription", newSubscription);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
