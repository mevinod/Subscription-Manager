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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

public class CustomSubscriptionActivity extends ActionBarActivity {

    private Subscriptions newSubscription;

    private View     subscription;
    private Typeface fontAwesome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_form_custom);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.edit_subscription_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        Button deleteSubscription = (Button)findViewById(R.id.deleteSubscription);
        deleteSubscription.setVisibility(View.GONE);

        newSubscription = new Subscriptions(R.drawable.wallet, getResources().getColor(R.color.black),
                "", "", BigDecimal.valueOf(0f), Subscriptions.billingCycle.MONTHLY, -1, 0, Subscriptions.reminders.NEVER);

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
        amount.setText(newSubscription.getAmountString());
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0) {
                    try {
                        if (charSequence.charAt(0) != '$') {
                            String value = charSequence.toString();
                            newSubscription.setAmount(new BigDecimal(value));
                            newSubscription.fillOutView(subscription, fontAwesome);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    newSubscription.setAmount(BigDecimal.valueOf(0f));
                    newSubscription.fillOutView(subscription, fontAwesome);
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
                    amount.setText(newSubscription.getAmountString());
                }else{
                    if(newSubscription.getAmount().floatValue() == 0f){
                        amount.setText("");
                    } else {
                        amount.setText(String.format(Locale.US, "%.2f", newSubscription.getAmount()));
                    }
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
                        newSubscription.setBillingCycleID(index);
                        newSubscription.fillOutView(subscription, fontAwesome);
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

                Bundle args = new Bundle();
                args.putLong("date_in_milliseconds", newSubscription.getFirstBillingDate());
                frag.setArguments(args);

                frag.setOnFinishedListener(new FirstBillingDateDialogFragment.OnFinishedListener() {
                    @Override
                    public void onFinishedWithResult(String monthName, int day, int year, long time) {
                        String date = String.format(Locale.getDefault(),
                                "%s %d, %d", monthName, day, year);
                        firstBillingDate.setText(date);
                        newSubscription.setFirstBillingDate(time);
                        newSubscription.fillOutView(subscription, fontAwesome);
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
                        newSubscription.setReminderID(index);
                        newSubscription.fillOutView(subscription, fontAwesome);
                        reminders.setText(name);
                    }
                });
            }
        });

        ViewStub subscriptionStubView = (ViewStub)findViewById(R.id.viewStub);
        subscription = subscriptionStubView.inflate();

        fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        newSubscription.fillOutView(subscription, fontAwesome);

        // To change the icon of the subscription
        ImageView icon = (ImageView)subscription.findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeIconDialogFragment frag = new ChangeIconDialogFragment();

                Bundle args = new Bundle();

                args.putInt("icon_id", newSubscription.getIconID());

                frag.setArguments(args);

                frag.setOnFinishedListener(new ChangeIconDialogFragment.OnFinishedListener() {
                    @Override
                    public void onFinishedWithResult(int iconId) {
                        newSubscription.setIconID(iconId);
                        newSubscription.fillOutView(subscription, fontAwesome);
                    }
                });

                frag.show(getSupportFragmentManager(), "change_icon");
            }
        });

        // To change the color of the subscription
        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeColorDialogFragment frag = new ChangeColorDialogFragment();

                Bundle args = new Bundle();
                args.putInt("color", newSubscription.getColor());

                frag.setArguments(args);

                frag.setOnFinishedListener(new ChangeColorDialogFragment.OnFinishedListener() {
                    @Override
                    public void onFinishedWithResult(int color) {
                        newSubscription.setColor(color);
                        newSubscription.fillOutView(subscription, fontAwesome);
                    }
                });

                frag.show(getSupportFragmentManager(), "change_color");
            }
        });
    }

    private long today(){
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_subscription, menu);
        return true;
    }

    public void createSubscription(MenuItem item) {
        Intent resultIntent = new Intent();

        resultIntent.putExtra("newSubscription", newSubscription);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
