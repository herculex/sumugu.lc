package com.sumugu.liubo.lc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ListContract;


public class ListLineActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_line);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_addlist:
                startActivity(new Intent(this,ListActivity.class));
                return true;
            case R.id.action_refreshlist:
//                startService(new Intent(this.ListRefreshService.class)); //现阶段还不需要Refresh从服务器获取数据
                Toast.makeText(this,"BITE ME!",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_purgelist:
                int row = getContentResolver().delete(ListContract.CONTENT_URI,null,null);
                Toast.makeText(this,"OMG,Deleted ALL of "+row,Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;

        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
    }
}
