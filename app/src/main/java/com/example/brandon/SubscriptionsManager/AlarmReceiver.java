package com.example.brandon.SubscriptionsManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
        super();
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.BLACK,
                PorterDuff.Mode.SRC_ATOP);

        vectorDrawable.setColorFilter(porterDuffColorFilter);

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Context context, String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(175);
        paint.setColor(context.getResources().getColor(R.color.black));
        paint.setTextAlign(Paint.Align.LEFT);

        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");

        paint.setTypeface(fontAwesome);

        float baseline = -paint.ascent(); // ascent() is negative
        int width  = (int) (paint.measureText(text)    + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image  = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable)drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public static void createNotification(Context context, int id, Subscriptions subscription){
        String title   = subscription.getName();
        String amount  = subscription.getAmountString();
        String dueDate = subscription.getNextPaymentString();

        Uri notifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap icon;

        if(subscription.getIconID() != -1) {
            icon = getBitmap(context, subscription.getIconID());
        }else{
            icon = getBitmap(context, subscription.getIconText());
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.drawable.ic_alarm, 1)
                        .setContentTitle(title)
                        .setContentText(amount)
                        .setSubText(dueDate)
                        .setShowWhen(false)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(notifySound)
                        .setVibrate(new long[]{0,350,50,600})
                        .setColor(subscription.getColor());

        NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(context);
        mNotifyMgr.notify(id, mBuilder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int  id    = intent.getIntExtra ("id"    , -1);
        long time  = intent.getLongExtra("time"  , -1);
        int  index = intent.getIntExtra ("index" , -1);

        SubscriptionsDatabase db = new SubscriptionsDatabase(context);

        long today = Subscriptions.today();

        Subscriptions setAlarm = db.getSubscriptions()[index];

        long nTime;
        do{
            nTime = setAlarm.generateNextBillingDate();
        }while(Subscriptions.today() > nTime);

        setAlarm.setNextBillingDate(nTime);
        db.replaceSubscription(setAlarm, index);
        db.setAlarmForNotification(index, true);

        createNotification(context, id, setAlarm);

        Log.e("alarm_call", String.format(Locale.US,
                "Current: %d, Target time: %d, Alarm set: %d", today, time, nTime));
    }
}
