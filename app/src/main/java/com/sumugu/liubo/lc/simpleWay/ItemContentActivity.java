package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;

public class ItemContentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        String content = bundle.getString("content");

        TextView textViewId = (TextView) findViewById(R.id.text_id);
        TextView textViewContent = (TextView) findViewById(R.id.text_content);

        textViewId.setText(id);
        textViewContent.setText(content);

        TextView textBack = (TextView) findViewById(R.id.tv_back);
        TextView textSave = (TextView) findViewById(R.id.tv_save);

        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ItemContentActivity.this, "saved is done.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
