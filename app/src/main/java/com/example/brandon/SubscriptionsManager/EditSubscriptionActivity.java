package com.example.brandon.SubscriptionsManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

import java.util.Locale;

public class EditSubscriptionActivity extends ActionBarActivity {

    Subscriptions subscription;
    int index;
    View subscriptionView;

    private Typeface fontAwesome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.subscription_form_custom);

        Bundle args = getIntent().getExtras();
        subscription = (Subscriptions)args.getSerializable("subscription");
        index = args.getInt("index");

        fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        if(!(subscription == null)){
            if(subscription.getIconID() == -1) {
                setContentView(R.layout.subscription_form_template);
            }
            else{
                setContentView(R.layout.subscription_form_custom);

                final EditText serviceName = (EditText)findViewById(R.id.serviceName);
                serviceName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        subscription.setName(charSequence.toString());
                        subscription.fillOutView(subscriptionView, fontAwesome);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            final EditText description = (EditText)findViewById(R.id.description);
            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    subscription.setDescription(charSequence.toString());
                    subscription.fillOutView(subscriptionView, fontAwesome);
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
                        if(charSequence.charAt(0) != '$'){
                            value = Float.parseFloat(charSequence.toString());
                            subscription.setAmount(value);
                            subscription.fillOutView(subscriptionView, fontAwesome);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        amount.setText(subscription.getAmountString());
                    }else{
                        amount.setText(String.format(Locale.US, "%.2f", subscription.getAmount()));
                    }
                }
            });

            final EditText billingCycle = (EditText)findViewById(R.id.billingCycle);
            billingCycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BillingCycleDialogFragment frag = new BillingCycleDialogFragment();
                    frag.show(getSupportFragmentManager(), "billing_cycle");
                    frag.setOnFinishedListener(new BillingCycleDialogFragment.OnFinishedListener() {
                        @Override
                        public void onFinishedWithResult(int index, String name) {
                            subscription.setBillingCycleID(index);
                            subscription.fillOutView(subscriptionView, fontAwesome);
                            billingCycle.setText(name);
                        }
                    });
                }
            });

            final EditText firstBillingDate = (EditText)findViewById(R.id.firstBillingDate);
            firstBillingDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirstBillingDateDialogFragment frag = new FirstBillingDateDialogFragment();
                    frag.setOnFinishedListener(new FirstBillingDateDialogFragment.OnFinishedListener() {
                        @Override
                        public void onFinishedWithResult(String monthName, int day, int year, long time) {
                            String date = String.format(Locale.getDefault(),
                                    "%s %d, %d", monthName, day, year);
                            firstBillingDate.setText(date);
                            subscription.setFirstBillingDate(time);
                            subscription.fillOutView(subscriptionView, fontAwesome);
                        }
                    });

                    frag.show(getSupportFragmentManager(), "date_selector");
                }
            });

            final EditText reminders = (EditText)findViewById(R.id.reminders);
            reminders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RemindersDialogFragment frag = new RemindersDialogFragment();
                    frag.show(getSupportFragmentManager(), "reminders");
                    frag.setOnFinishedListener(new RemindersDialogFragment.OnFinishedListener() {
                        @Override
                        public void onFinishedWithResult(int index, String name) {
                            subscription.setReminderID(index);
                            subscription.fillOutView(subscriptionView, fontAwesome);
                            reminders.setText(name);
                        }
                    });
                }
            });
        }

        final Toolbar toolbar = (Toolbar)findViewById(R.id.edit_subscription_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        subscriptionView = ((ViewStub)findViewById(R.id.viewStub)).inflate();
        subscription.fillOutView(subscriptionView, fontAwesome);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_subscription, menu);
        return true;
    }

    public void createSubscription(MenuItem item) {
        Intent resultIntent = new Intent();

        resultIntent.putExtra("subscription", subscription);
        resultIntent.putExtra("index", index);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void deleteSubscription(View view) {
        final SubscriptionsDatabase entriesDB = new SubscriptionsDatabase(this);

        DeleteSubscriptionDialog deleteDialog = new DeleteSubscriptionDialog(this, index);
        deleteDialog.setOnDeleteClickedListener(new DeleteSubscriptionDialog.OnDeleteClicked() {
            @Override
            public void onDeleteClicked(int index) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("index", index);
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        deleteDialog.show();
    }
}



