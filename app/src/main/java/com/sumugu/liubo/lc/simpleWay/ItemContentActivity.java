package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;

import java.util.Calendar;

public class ItemContentActivity extends Activity {
    TextView textAlarm;
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

        textAlarm = (TextView) findViewById(R.id.text_alarm);
        textAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemContentActivity.this, DatePickerActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Calendar calender = Calendar.getInstance();
        textAlarm.setText(calender.get(Calendar.MONTH) + 1 + "," + calender.get(Calendar.DAY_OF_MONTH) + "," + calender.get(Calendar.HOUR_OF_DAY) + "," + calender.get(Calendar.MINUTE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                textAlarm.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", data.getLongExtra("alarmclock", 0)));

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(this, "没选好", Toast.LENGTH_SHORT).show();
                textAlarm.setText("没选好");

            } else {
                Toast.makeText(this, "what the fuck happend?!", Toast.LENGTH_SHORT).show();
                textAlarm.setText("what the fuck happend?!");
            }
        }
    }


}
