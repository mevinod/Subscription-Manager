package com.example.brandon.SubscriptionsManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SubscriptionsDatabase extends SQLiteOpenHelper {
    static ArrayList<DataChangeListener> listeners = new ArrayList<> ();

    private Context context;

    final public int REMOVED = 0, REPLACED = 1, INSERTED = 2;

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "subscriptions.db";
    private static final String SUBSCRIPTIONS_TABLE_NAME = "subscriptions";

    private static final String COLUMN_ID                = "id";
    private static final String COLUMN_COLOR             = "color";
    private static final String COLUMN_ICON_TEXT         = "icon_text";
    private static final String COLUMN_ICON_IMAGE        = "icon_image";
    private static final String COLUMN_NAME              = "name";
    private static final String COLUMN_DESCRIPTION       = "description";
    private static final String COLUMN_AMOUNT            = "amount";
    private static final String COLUMN_BILLING_CYCLE     = "billing_cycle";
    private static final String COLUMN_BILLING_DATE      = "billing_date";
    private static final String COLUMN_NEXT_BILLING_DATE = "next_billing_date";
    private static final String COLUMN_REMINDER          = "reminder";
    private static final String COLUMN_TYPE              = "type";

    public static final int CUSTOM_TYPE = 0, TEMPLATE_TYPE = 1;

    private static final String SUBSCRIPTIONS_TABLE_CREATE = "CREATE TABLE " +
            SUBSCRIPTIONS_TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_COLOR              + " INTEGER, " +
            COLUMN_ICON_TEXT          + " TEXT, "    +
            COLUMN_ICON_IMAGE         + " INTEGER, " +
            COLUMN_NAME               + " TEXT, "    +
            COLUMN_DESCRIPTION        + " TEXT, "    +
            COLUMN_AMOUNT             + " TEXT, " +
            COLUMN_BILLING_CYCLE      + " INTEGER, " +
            COLUMN_BILLING_DATE       + " INTEGER, " +
            COLUMN_NEXT_BILLING_DATE  + " INTEGER, " +
            COLUMN_REMINDER           + " INTEGER, " +
            COLUMN_TYPE               + " INTEGER " +
            ");";

    public SubscriptionsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void setOnDataChanged (DataChangeListener listener) {
        // Store the listener object
        listeners.add(listener);
    }

    public interface DataChangeListener {
        void onDataChanged(int index, int type);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SUBSCRIPTIONS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUBSCRIPTIONS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SUBSCRIPTIONS_TABLE_NAME);
        onCreate(db);
    }

    public String getDatabaseName()
    {
        return DATABASE_NAME;
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + SUBSCRIPTIONS_TABLE_NAME);
        onCreate(db);
        db.close();
    }

    private ContentValues getContentValuesForSubscription(Subscriptions entry){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ICON_IMAGE,         entry.getIconID());
        values.put(COLUMN_ICON_TEXT,          entry.getIconText());
        values.put(COLUMN_COLOR,              entry.getColor());
        values.put(COLUMN_NAME,               entry.getName());
        values.put(COLUMN_DESCRIPTION,        entry.getDescription());
        values.put(COLUMN_AMOUNT,             entry.getAmount().toPlainString());
        values.put(COLUMN_BILLING_CYCLE,      entry.getBillingCycleID());
        values.put(COLUMN_BILLING_DATE,       entry.getFirstBillingDate());
        values.put(COLUMN_NEXT_BILLING_DATE,  entry.getNextBillingDate());
        values.put(COLUMN_REMINDER,           entry.getReminderID());
        values.put(COLUMN_TYPE,               entry.getSubscriptionType());

        return values;
    }

    public void insertSubscription(Subscriptions entry) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = getContentValuesForSubscription(entry);

        db.insert(SUBSCRIPTIONS_TABLE_NAME, null, values);
        db.close();

        setAlarmForNotification(length() - 1, true);
        notifyDataChange(new int[]{length() - 1}, INSERTED);
    }

    public void removeRow(int index) {
        setAlarmForNotification(index, false);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(SUBSCRIPTIONS_TABLE_NAME, null, null, null, null, null, null);

        if(cursor.moveToPosition(index)) {
            String rowId = cursor.getString(cursor.getColumnIndex(COLUMN_ID));

            db.delete(SUBSCRIPTIONS_TABLE_NAME, COLUMN_ID + "=?",  new String[]{rowId});
        }

        cursor.close();
        db.close();

        notifyDataChange(new int[]{index}, REMOVED);
    }

    public int getDatabaseID(int index){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(SUBSCRIPTIONS_TABLE_NAME, null, null, null, null, null, null);

        int rowId = -1;
        if(cursor.moveToPosition(index)) {
            rowId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            return rowId;
        }

        cursor.close();
        db.close();

        return rowId;
    }

    public void replaceSubscription(Subscriptions entry, int index) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(SUBSCRIPTIONS_TABLE_NAME, null, null, null, null, null, null);

        ContentValues values = getContentValuesForSubscription(entry);

        if(cursor.moveToPosition(index)) {
            String rowId = cursor.getString(cursor.getColumnIndex(COLUMN_ID));

            db.update(SUBSCRIPTIONS_TABLE_NAME, values, COLUMN_ID + "=?",  new String[]{rowId});
        }

        cursor.close();
        db.close();

        setAlarmForNotification(index, true);
        notifyDataChange(new int[]{index}, REPLACED);
    }

    public void setAlarmForNotification(int index, boolean displayNotification){
        Subscriptions setAlarm = getSubscriptions()[index];

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        long thisTime = setAlarm.getNextBillingDate();

        int id = getDatabaseID(index);
        intent.putExtra("id", id);
        intent.putExtra("subscription", setAlarm);
        intent.putExtra("index", index);
        intent.putExtra("time", thisTime);

        Subscriptions.reminders reminder =
                Subscriptions.reminders.values()[setAlarm.getReminderID()];

        if(displayNotification && reminder != Subscriptions.reminders.NEVER){
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(thisTime);

            if (reminder == Subscriptions.reminders.ONE_DAY) {
                c.add(Calendar.DATE, -1);
            } else if(reminder == Subscriptions.reminders.TWO_DAYS){
                c.add(Calendar.DATE, -2);
            } else if(reminder == Subscriptions.reminders.THREE_DAYS){
                c.add(Calendar.DATE, -3);
            } else if(reminder == Subscriptions.reminders.ONE_WEEK){
                c.add(Calendar.WEEK_OF_YEAR, -1);
            } else if(reminder == Subscriptions.reminders.TWO_WEEKS){
                c.add(Calendar.WEEK_OF_YEAR, -2);
            } else if(reminder == Subscriptions.reminders.ONE_MONTH){
                c.add(Calendar.MONTH, -1);
            }

            thisTime = c.getTimeInMillis();

            Log.e("alarm", String.format(Locale.US, "%d", thisTime));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, thisTime, alarmIntent);
        }
        else{
            PendingIntent displayIntent = PendingIntent.getBroadcast(
                    context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            if(displayIntent != null){
                alarmManager.cancel(displayIntent);
                displayIntent.cancel();
            }
        }

        // Set an alarm to go off at mNextBillingDate, repeating, displays a notification;
        // If display notification is true, change the alarm for this subscription.
        // If display notification is false, remove the alarm for this subscription.
    }

    public int length() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " +  SUBSCRIPTIONS_TABLE_NAME, null);
        int length = c.getCount();
        c.close();

        return length;
    }

    public void updateAlarms(){
        for(int i = 0; i < length(); ++i) {
            setAlarmForNotification(i, true);
        }
    }

    public Subscriptions[] getSubscriptions() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM subscriptions", null);
        c.moveToFirst();

        int subsLength = c.getCount();
        Subscriptions[] results = new Subscriptions[subsLength];

        for(int i = 0; i < subsLength; ++i) {
            int iconID = c.getInt(c.getColumnIndex(COLUMN_ICON_IMAGE));
            String iconText = c.getString(c.getColumnIndex(COLUMN_ICON_TEXT));

            int color = c.getInt(c.getColumnIndex(COLUMN_COLOR));

            String name = c.getString(c.getColumnIndex(COLUMN_NAME));
            String description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));

            BigDecimal amount = new BigDecimal(c.getString(c.getColumnIndex(COLUMN_AMOUNT)));

            int billingCycle = c.getInt(c.getColumnIndex(COLUMN_BILLING_CYCLE));
            long firstBillingDate = c.getLong(c.getColumnIndex(COLUMN_BILLING_DATE));
            long nextBillingDate = c.getLong(c.getColumnIndex(COLUMN_NEXT_BILLING_DATE));

            int reminder = c.getInt(c.getColumnIndex(COLUMN_REMINDER));

            int subscriptionType = c.getInt(c.getColumnIndex(COLUMN_TYPE));

            if(iconID != -1) {
                results[i] = new Subscriptions(iconID, color, name, description, amount,
                        Subscriptions.billingCycle.values()[billingCycle], firstBillingDate,
                        nextBillingDate, Subscriptions.reminders.values()[reminder], subscriptionType);
            } else {
                results[i] = new Subscriptions(iconText, color, name, description, amount,
                        Subscriptions.billingCycle.values()[billingCycle], firstBillingDate,
                        nextBillingDate, Subscriptions.reminders.values()[reminder], subscriptionType);
            }

            c.moveToNext();
        }

        c.close();
        db.close();

        return results;
    }

    public BigDecimal getTotalPayment() {
        SQLiteDatabase db = getReadableDatabase();
        BigDecimal total = new BigDecimal(0);

        Cursor c = db.rawQuery("SELECT * FROM " + SUBSCRIPTIONS_TABLE_NAME, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            BigDecimal monthlyPayment = new BigDecimal(c.getString(c.getColumnIndex(COLUMN_AMOUNT)));

            int billingCycleId = c.getInt(c.getColumnIndex(COLUMN_BILLING_CYCLE));
            Subscriptions.billingCycle billingCycle =
                    Subscriptions.billingCycle.values()[billingCycleId];

            if(billingCycle == Subscriptions.billingCycle.WEEKLY){
                monthlyPayment = monthlyPayment.multiply(BigDecimal.valueOf(4));
            }
            else if(billingCycle == Subscriptions.billingCycle.QUARTERLY){
                monthlyPayment = monthlyPayment.divide(BigDecimal.valueOf(4f), 4);
            }
            else if(billingCycle == Subscriptions.billingCycle.YEARLY){
                monthlyPayment = monthlyPayment.divide(BigDecimal.valueOf(12f), 4);
            }

            total = total.add(monthlyPayment);
            c.moveToNext();
        }

        c.close();
        db.close();
        return total;
    }

    public void notifyDataChange(int indexes[], int type){
        for (DataChangeListener listener : listeners) {
            for(int index: indexes){
                listener.onDataChanged(index, type);
            }
        }
    }
}
