package com.example.brandon.SubscriptionsManager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Locale;

public class Subscriptions implements Serializable
{
    private String mIconText;
    private int    mIconID;
    private int    mColor;
    private String mName;
    private String mDescription;
    private double mAmount;
    private int    mBillingCycleID;
    private long   mFirstBillingDate;
    private int    mReminderID;

    private String mAmountString;

    public enum billingCycle{
        WEEKLY(0), MONTHLY(1), QUARTERLY(2), YEARLY(3);

        int value;
        billingCycle(int value){
            this.value = value;
        }
    };

    public enum reminders{
        NEVER(0), ONE_DAY(1), TWO_DAYS(2), THREE_DAYS(3), ONE_WEEK(4), TWO_WEEKS(5), ONE_MONTH(6);

        public int value;
        reminders(int value){
            this.value = value;
        }
    };

    public Subscriptions(int IconID, int color, String name, String description, double amount,
                         billingCycle billingCycle, long firstBillingDate, reminders reminder)
    {
        mIconID           = IconID;
        mIconText         = "";
        mColor            = color;
        mName             = name;
        mDescription      = description;
        mBillingCycleID   = billingCycle.value;
        mFirstBillingDate = firstBillingDate;
        mReminderID       = reminder.value;
        setAmount(amount);
    }

    public Subscriptions(String IconText, int color, String name, String description, double amount,
                         billingCycle billingCycle, long firstBillingDate, reminders reminder)
    {
        mIconID           = -1;
        mIconText         = IconText;
        mColor            = color;
        mName             = name;
        mDescription      = description;
        mBillingCycleID   = billingCycle.value;
        mFirstBillingDate = firstBillingDate;
        mReminderID       = reminder.value;
        setAmount(amount);
    }

    public View getView(Context context, Typeface font){
        View view = null;

        if(mIconID == -1) {
            view = View.inflate(context, R.layout.subscription_layout_text_icon, null);
        }else{
            view = View.inflate(context, R.layout.subscription_layout_image_icon, null);
        }

        view = fillOutView(view, font);

        return view;
    }

    public View fillOutView(View view, Typeface font){

        if(mIconID == -1) {
            TextView icon = ((TextView)view.findViewById(R.id.icon));
            icon.setText(mIconText);
            icon.setTypeface(font);
        }else{
            ((ImageView)view.findViewById(R.id.icon)).setImageResource(mIconID);
        }

        view.setBackgroundColor(mColor);

        TextView serviceName = (TextView)view.findViewById(R.id.serviceName);
        serviceName.setText(mName);
        serviceName.setTypeface(font);

        TextView description = (TextView)view.findViewById(R.id.description);
        description.setText(mDescription);
        description.setTypeface(font);

        TextView amount = (TextView)view.findViewById(R.id.amount);
        amount.setText(mAmountString);
        amount.setTypeface(font);

        TextView nextPayment = (TextView)view.findViewById(R.id.nextPaymentDate);
        nextPayment.setText(getNextPaymentString());
        nextPayment.setTypeface(font);

        if((mReminderID == reminders.NEVER.value))
        { // If the reminder is set to never, make the alarm icon go away.
            view.findViewById(R.id.alarmIcon).setVisibility(View.GONE);
        }

        return view;
    }

    private String getNextPaymentString()
    {
        String nextPayment = "";

        // TODO From the first billing date, and the billing cycle, calculate the next payment string

        return nextPayment;
    }

    // GETTERS

    public String getIconText() {
        return mIconText;
    }

    public int getIconID()
    {
        return mIconID;
    }

    public int getColor(){
        return mColor;
    }

    public String getName()
    {
        return mName;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public double getAmount()
    {
        return mAmount;
    }

    public String getAmountString()
    {
        return this.mAmountString;
    }

    public int getBillingCycleID()
    {
        return mBillingCycleID;
    }

    public long getFirstBillingDate()
    {
        return mFirstBillingDate;
    }

    public int getReminderID()
    {
        return mReminderID;
    }

    // SETTERS

    public void setIconText(String iconText) {
        this.mIconText = iconText;
    }

    public void setIconID(int iconID) {
        this.mIconID = iconID;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setAmount(double amount) {
        this.mAmount = amount;

        this.mAmountString = "";
        if(!(this.mAmount < 0)){
            this.mAmountString = String.format(Locale.US, "$%.2f", this.mAmount);
        }
    }

    public void setBillingCycleID(int billingCycleID) {
        this.mBillingCycleID = billingCycleID;
    }

    public void setFirstBillingDate(long billingDate) {
        this.mFirstBillingDate = billingDate;
    }

    public void setReminderID(int reminderID) {
        this.mReminderID = mReminderID;
    }

}