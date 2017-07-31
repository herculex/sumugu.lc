package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

public class ItemPackageActivity extends Activity {

    private ListView mListView;

    private String[] arrayString = new String[]{"hello", "simpleway", "protein", "the way we found.", "do samething for",
            "today I want to make a choice", "path for right direction", "what we selected was right on the way?", "keep going on."};

    private String[] FROM = new String[]{ItemContract.Column.ITEM_CONTENT};
    private int[] TO = new int[]{R.id.text_content};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_package);


        mListView = (ListView) findViewById(R.id.listView);

//        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.itempackage_listview_item, R.id.text_content, arrayString);
//        mListView.setAdapter(adapter);

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.itempackage_listview_item, null, FROM, TO, 0);
        mListView.setAdapter(simpleCursorAdapter);
        getLoaderManager().initLoader(0, null, new ItemsLoader(this, simpleCursorAdapter));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.text_content);
                String content = "position:" + i;
                content += ",id:" + l;
                content += ",content:" + textView.getText().toString();

                Toast.makeText(ItemPackageActivity.this, content, Toast.LENGTH_SHORT).show();

                Intent itemContent = new Intent(ItemPackageActivity.this, ItemContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", l);    //get id of item inside of list
                bundle.putString("content", textView.getText().toString()); //get content
                itemContent.putExtras(bundle);
                startActivityForResult(itemContent, 1);
            }
        });

        TextView textCreate = (TextView) findViewById(R.id.tv_create);
        textCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemContent = new Intent(ItemPackageActivity.this, ItemContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", "000");
                bundle.putString("content", "create new content");
                itemContent.putExtras(bundle);
                startActivityForResult(itemContent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Toast.makeText(this, "it's ItemContent'detail come back.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2) {
            Toast.makeText(this, "it's ItemContent'create come back", Toast.LENGTH_SHORT).show();
        }
    }

    class ItemsLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        SimpleCursorAdapter mSimpleCursorAdapter;
        Context mContext;

        public ItemsLoader(Context context, SimpleCursorAdapter adapter) {
            mSimpleCursorAdapter = adapter;
            mContext = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String where = ItemContract.Column.ITEM_IS_FINISHED + "=0";
            return new CursorLoader(mContext, ItemContract.CONTENT_URI, null, where, null, ItemContract.DEFAULT_SORT);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mSimpleCursorAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mSimpleCursorAdapter.swapCursor(null);

        }
    }
}
