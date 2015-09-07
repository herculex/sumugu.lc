package com.sumugu.liubo.lc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random rand = new Random();
        String sms = String.valueOf(rand.nextInt(999)+1);
        String call = String.valueOf(rand.nextInt(999)+1);

        ((TextView) findViewById(R.id.textMissing)).setText("Sms:"+sms+" Call:"+call);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        switch (id)
        {
            case R.id.action_listline:
                startActivity(new Intent(this,ListLineActivity.class));
                return true;
            case R.id.action_unlock:
                Toast.makeText(this,"not link yet!",Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }
}
