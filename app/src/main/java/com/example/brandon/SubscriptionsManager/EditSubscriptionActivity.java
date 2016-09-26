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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Locale;

public class EditSubscriptionActivity extends ActionBarActivity {

    Subscriptions subscription;
    int index;
    View subscriptionView;

    private Typeface fontAwesome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        subscription = (Subscriptions)args.getSerializable("subscription");
        index = args.getInt("index");

        fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        boolean isCustomView = (subscription.getIconID() != -1);

        if(!(subscription == null)){
            if(!isCustomView) {
                setContentView(R.layout.subscription_form_template);
            }
            else{
                setContentView(R.layout.subscription_form_custom);

                final EditText serviceName = (EditText)findViewById(R.id.serviceName);
                serviceName.setText(subscription.getName());
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
            description.setText(subscription.getDescription());
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
            amount.setText(subscription.getAmountString());
            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length() != 0) {
                        try {
                            if (charSequence.charAt(0) != '$') {
                                BigDecimal value = new BigDecimal(charSequence.toString());
                                subscription.setAmount(value);
                                subscription.fillOutView(subscriptionView, fontAwesome);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        subscription.setAmount(BigDecimal.valueOf(0));
                        subscription.fillOutView(subscriptionView, fontAwesome);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if(!focus){
                        amount.setText(subscription.getAmountString());
                    }else{
                        if(subscription.getAmount().floatValue() == 0){
                            amount.setText("");
                        } else {
                            amount.setText(String.format(Locale.US, "%.2f", subscription.getAmount()));
                        }
                    }
                }
            });

            final EditText billingCycle = (EditText)findViewById(R.id.billingCycle);
            billingCycle.setText(subscription.getBillingCycleString(this));
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
            firstBillingDate.setText(subscription.getFirstBillingDateString(this));
            firstBillingDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirstBillingDateDialogFragment frag = new FirstBillingDateDialogFragment();

                    Bundle args = new Bundle();
                    args.putLong("date_in_milliseconds", subscription.getFirstBillingDate());
                    frag.setArguments(args);

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
            reminders.setText(subscription.getReminderString(this));
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

        if(isCustomView){
            ImageView icon = (ImageView)subscriptionView.findViewById(R.id.icon);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeIconDialogFragment frag = new ChangeIconDialogFragment();

                    Bundle args = new Bundle();
                    args.putInt("icon_id", subscription.getIconID());

                    frag.setArguments(args);

                    frag.setOnFinishedListener(new ChangeIconDialogFragment.OnFinishedListener() {
                        @Override
                        public void onFinishedWithResult(int iconId) {
                            subscription.setIconID(iconId);
                            subscription.fillOutView(subscriptionView, fontAwesome);
                        }
                    });

                    frag.show(getSupportFragmentManager(), "change_icon");
                }
            });

            subscriptionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeColorDialogFragment frag = new ChangeColorDialogFragment();

                    Bundle args = new Bundle();
                    args.putInt("color", subscription.getColor());

                    frag.setArguments(args);

                    frag.setOnFinishedListener(new ChangeColorDialogFragment.OnFinishedListener() {
                        @Override
                        public void onFinishedWithResult(int color) {
                            subscription.setColor(color);
                            subscription.fillOutView(subscriptionView, fontAwesome);
                        }
                    });

                    frag.show(getSupportFragmentManager(), "change_color");
                }
            });
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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