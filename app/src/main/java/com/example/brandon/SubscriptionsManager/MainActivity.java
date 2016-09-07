package com.example.brandon.SubscriptionsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SubscriptionsDatabase entriesDB = null;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(entriesDB == null) {
            entriesDB = new SubscriptionsDatabase(this);
        }

        if(entriesDB.length() == 0) {
            setFragmentBlankDatabase();
        }
        else {
            setFragmentSubscriptions();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newSubscription = new Intent(MainActivity.this, NewSubscriptionActivity.class);
                startActivityForResult(newSubscription, 0);
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(entriesDB.length() == 0) {
            setFragmentBlankDatabase();
        }
        else {
            setFragmentSubscriptions();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            Subscriptions newSubscription = (Subscriptions)
                    data.getSerializableExtra("newSubscription");

            if(newSubscription.getFirstBillingDate() == -1){
                newSubscription.setFirstBillingDate(Subscriptions.today());
            }

            entriesDB.insertSubscription(newSubscription);
            setFragmentSubscriptions();
        }

        if (requestCode == 1 && data != null) {
            int index = data.getIntExtra("index", -1);

            if(resultCode == Activity.RESULT_OK) {
                Subscriptions newSubscription = (Subscriptions)
                        data.getSerializableExtra("subscription");

                entriesDB.replaceSubscription(newSubscription, index);
            }

            else if(resultCode == Activity.RESULT_CANCELED){
                entriesDB.removeRow(index);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info_outline) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setIcon(R.mipmap.ic_launcher);
            alertDialog.setTitle("Bill Manager");
            alertDialog.setMessage(getString(R.string.info_string));
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragmentBlankDatabase(){
        BlankDatabaseFragment frag = new BlankDatabaseFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag).commit();
    }

    public void setFragmentSubscriptions(){
        SubscriptionsFragment frag = new SubscriptionsFragment();

        frag.setOnBecomesEmptyListener(new SubscriptionsFragment.BecomesEmptyListener() {
            @Override
            public void onBecomesEmpty() {
                setFragmentBlankDatabase();
            }
        });

        frag.setOnSubscriptionClickListener(new SubscriptionsFragment.OnSubscriptionClickListener() {
            @Override
            public void onSubscriptionClick(Subscriptions subscription, int index) {
                Intent launchActivity = new Intent(MainActivity.this, EditSubscriptionActivity.class);
                launchActivity.putExtra("subscription", subscription);
                launchActivity.putExtra("index", index);
                startActivityForResult(launchActivity, 1);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag).commit();
    }
}
