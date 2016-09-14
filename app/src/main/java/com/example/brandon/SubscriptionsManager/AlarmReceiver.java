package com.example.brandon.SubscriptionsManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int  id    = intent.getIntExtra ("id"    , -1);
        long time  = intent.getLongExtra("time"  , -1);
        int  index = intent.getIntExtra ("index" , -1);

        SubscriptionsDatabase db = new SubscriptionsDatabase(context);

        long today = Subscriptions.today();

        Subscriptions setAlarm = db.getSubscriptions()[index];

        long nTime = setAlarm.generateNextBillingDate();

        setAlarm.setNextBillingDate(nTime);
        db.replaceSubscription(setAlarm, index);
        db.setAlarmForNotification(index, true);

        Log.e("alarm_call", String.format(Locale.US,
                "Current: %d, Alarm Set: %d, next time: %d", today, time, nTime));
    }
}
