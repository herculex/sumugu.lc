package com.sumugu.liubo.lc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sumugu.liubo.lc.simpleWay.ItemPackageActivity;

public class ItemDetailActivity extends AppCompatActivity{
    private static final String TAG = ItemDetailActivity.class.getSimpleName();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(ItemDetailActivity.this, ItemPackageActivity.class);
                startActivity(main);

                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

    }

}
