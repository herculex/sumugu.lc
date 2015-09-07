package com.sumugu.liubo.lc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ItemContract;
import com.sumugu.liubo.lc.contract.ListContract;


public class ItemLineActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_line);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_line, menu);
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
            case R.id.action_additem:
                long listId=this.getIntent().getLongExtra(ListContract.Column.LIST_ID,-1);
                startActivity(new Intent(this,ItemActivity.class).putExtra(ListContract.Column.LIST_ID,listId));
                return true;
            case R.id.action_purgeitem:
                String where = ItemContract.Column.ITEM_LIST_ID +"="+String.valueOf(getIntent().getLongExtra(ListContract.Column.LIST_ID,-1));
                int row = this.getContentResolver().delete(ItemContract.CONTENT_URI,where,null);
                Toast.makeText(this,"你删掉了"+row+"条！后悔吗？然并卵！",Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;

        }
    }
}
