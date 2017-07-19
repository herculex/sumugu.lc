package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;

public class ItemPackageActivity extends Activity {

    private ListView mListView;

    private String[] arrayString = new String[]{"hello", "simpleway", "protein", "the way we found.", "do samething for",
            "today I want to make a choice", "path for right direction", "what we selected was right on the way?", "keep going on."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_package);


        mListView = (ListView) findViewById(R.id.listView);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.itempackage_listview_item, R.id.text_content, arrayString);
        mListView.setAdapter(adapter);

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
                bundle.putString("id", Integer.toString(i));
                bundle.putString("content", textView.getText().toString());
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
}
