package com.example.brandon.SubscriptionsManager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class Subscriptions implements Serializable {
    private String mIconText;
    private int    mIconID;
    private int    mColor;
    private String mName;
    private String mDescription;
    private double mAmount;
    private int    mBillingCycleID;
    private long   mFirstBillingDate;
    private long   mNextBillingDate;
    private int    mReminderID;

    private String mAmountString;

    public enum billingCycle{
        WEEKLY(0), MONTHLY(1), QUARTERLY(2), YEARLY(3);

        public int value;
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
                         billingCycle billingCycle, long firstBillingDate, long nextBillingDate,
                         reminders reminder) {

        mIconID           = IconID;
        mIconText         = "";
        mColor            = color;
        mName             = name;
        mDescription      = description;
        mBillingCycleID   = billingCycle.value;
        mFirstBillingDate = firstBillingDate;
        mNextBillingDate  = nextBillingDate;
        mReminderID       = reminder.value;
        setAmount(amount);
    }

    public Subscriptions(String IconText, int color, String name, String description, double amount,
                         billingCycle billingCycle, long firstBillingDate, long nextBillingDate,
                         reminders reminder) {
        mIconID           = -1;
        mIconText         = IconText;
        mColor            = color;
        mName             = name;
        mDescription      = description;
        mBillingCycleID   = billingCycle.value;
        mFirstBillingDate = firstBillingDate;
        mNextBillingDate  = nextBillingDate;
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
            ImageView imageView = (ImageView)view.findViewById(R.id.icon);
            imageView.setImageResource(mIconID);

            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE,
                    PorterDuff.Mode.SRC_ATOP);

            imageView.setColorFilter(porterDuffColorFilter);
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
        if(!(mFirstBillingDate < 0)){
            nextPayment.setText(getNextPaymentString());
            nextPayment.setTypeface(font);
        }else{
            nextPayment.setText("");
        }

        if((mReminderID == reminders.NEVER.value))
        { // If the reminder is set to never, make the alarm icon go away.
            view.findViewById(R.id.alarmIcon).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.alarmIcon).setVisibility(View.VISIBLE);
        }

        return view;
    }

    public boolean equals(Subscriptions subscription){
        boolean equal;
        equal  = (this.mIconID           == subscription.getIconID());
        equal &= this.mIconText.equals(subscription.getIconText());
        equal &= (this.mColor            == subscription.getColor());
        equal &= this.mName.equals(subscription.getName());
        equal &= this.mDescription.equals(subscription.getDescription());
        equal &= (this.mBillingCycleID   == subscription.getBillingCycleID());
        equal &= (this.mFirstBillingDate == subscription.getFirstBillingDate());
        equal &= (this.mNextBillingDate  == subscription.getNextBillingDate());
        equal &= (this.mReminderID       == subscription.getReminderID());
        return equal;
    }

    public static boolean checkArraysEqual(Subscriptions[] arryOne, Subscriptions[] arryTwo){
        boolean equal = (arryOne.length == arryTwo.length);
        if(equal){
            for(int sub = 0; sub < arryOne.length; ++sub){
                if(!arryOne[sub].equals(arryTwo[sub])) {
                    return false;
                }
            }
        }
        return equal;
    }

    public String getNextPaymentString() {
        // TODO I think this works properly, but I am not 100% sure. hue hue hue.

        String nextPayment = "";

        long startDate = today();
        long endDate   = mFirstBillingDate;

        if(startDate > mFirstBillingDate) {
            if(today() > mNextBillingDate) {
                long newTime = generateNextBillingDate();
                setNextBillingDate(newTime);
            }
            endDate = mNextBillingDate;
        }

        nextPayment = getDateDistance(startDate, endDate);

        return nextPayment;
    }

    public long generateNextBillingDate(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getNextBillingDate());

        billingCycle billCycle = billingCycle.values()[mBillingCycleID];

        if (billCycle == billingCycle.WEEKLY) {
            c.add(Calendar.WEEK_OF_YEAR, 1);
        } else if (billCycle == billingCycle.MONTHLY) {
            c.add(Calendar.MONTH, 1);
        } else if (billCycle == billingCycle.QUARTERLY) {
            c.add(Calendar.MONTH, 3);
        } else if (billCycle == billingCycle.YEARLY) {
            c.add(Calendar.YEAR, 1);
        }

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    private String getDateDistance(long startDate, long endDate){
        String dateString = "";

        long deltaDate = endDate - startDate;
        int days = (int) (deltaDate / (1000*60*60*24));

        if(startDate > endDate){
            setNextBillingDate(endDate);
            return getNextPaymentString();
        }

        switch (days){
            case(0):
                dateString = "TODAY";
                break;
            case(1):
                dateString = "TOMORROW";
                break;
            default:
                int months = days / 30;
                if(months == 1){
                    dateString = String.format(Locale.US, "IN %d MONTH", months);
                }
                else if(months > 1){
                    dateString = String.format(Locale.US, "IN %d MONTHS", months);
                }
                else{
                    dateString = String.format(Locale.US, "IN %d DAYS", days);
                }
                break;
        }

        return dateString;
    }

    static public long today(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis(); // Today's date in milliseconds
    }

    // GETTERS

    public String getIconText() {
        return mIconText;
    }

    public int getIconID() {
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

    public double getAmount() {
        return mAmount;
    }

    public String getAmountString() {
        return this.mAmountString;
    }

    public String getBillingCycleString(Context context){
        String[] billingCycles = context.getResources().getStringArray(R.array.billing_cycles);
        return billingCycles[getBillingCycleID()];
    }

    public int getBillingCycleID() {
        return mBillingCycleID;
    }

    public String getFirstBillingDateString(Context context){
        return convertMillisToString(context, getFirstBillingDate());
    }

    public static String convertMillisToString(Context context, long millis){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);
        int year  = c.get(Calendar.YEAR);

        String monthString = context.getResources().getStringArray(R.array.month_names)[month];
        return String.format(Locale.US, "%s %d, %d", monthString, day, year);
    }

    public long getFirstBillingDate() {
        return mFirstBillingDate;
    }

    public long getNextBillingDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mNextBillingDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis(); // Today's date in milliseconds
    }

    public String getReminderString(Context context){
        String[] reminders = context.getResources().getStringArray(R.array.reminders);
        return reminders[getReminderID()];
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
            DecimalFormat decimalFormat = new DecimalFormat("$#,###.##");
            this.mAmountString = decimalFormat.format(amount);
        }
    }

    public void setBillingCycleID(int billingCycleID) {
        this.mBillingCycleID = billingCycleID;
        this.mNextBillingDate = mFirstBillingDate;;
    }

    public void setFirstBillingDate(long billingDate) {
        this.mFirstBillingDate = billingDate;
        this.mNextBillingDate  = mFirstBillingDate;
    }

    public void setNextBillingDate(long billingDate){
        this.mNextBillingDate = billingDate;
    }

    public void setReminderID(int reminderID) {
        this.mReminderID = reminderID;
    }
}