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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(entriesDB == null)
        {
            entriesDB = new SubscriptionsDatabase(this);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent newSubscription = new Intent(MainActivity.this, NewSubscriptionActivity.class);
                startActivityForResult(newSubscription, 0);
            }
        });

        if(entriesDB.length() == 0)
        {
            setFragmentBlankDatabase();
        }

        else
        {
            setFragmentSubscriptions();
        }
    }

    public void setFragmentBlankDatabase(){
        BlankDatabaseFragment frag = new BlankDatabaseFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag).commit();
    }

    public void setFragmentSubscriptions(){
        SubscriptionsFragment frag = new SubscriptionsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            Subscriptions newSubscription = (Subscriptions)
                    data.getSerializableExtra("newSubscription");

            entriesDB.insertSubscription(newSubscription);
            setFragmentSubscriptions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The acti
        // on bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_info_outline)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setIcon(R.mipmap.ic_launcher);
            alertDialog.setTitle("Bill Manager");
            alertDialog.setMessage(getString(R.string.info_string));
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
