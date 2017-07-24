package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;

public class DatePickerActivity extends Activity {

    Spinner spinner_month, spinner_day, spinner_hour, spinner_minute;
    String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    String[] days_small = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30"};

    String[] days_big = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31"};

    String[] days_leap = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
            "28", "29"};

    String[] days_noleap = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
            "28"};

    String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    String[] minutes = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
            "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42",
            "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57",
            "58", "59"};

    String[] minutes_5s = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};

//    Integer[] monthss = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12};


    TextView textDateConfirm, textDateCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        spinner_month = (Spinner) findViewById(R.id.spinner_month);
        ArrayAdapter adapter_month = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(adapter_month);

        spinner_day = (Spinner) findViewById(R.id.spinner_day);
        spinner_hour = (Spinner) findViewById(R.id.spinner_hour);
        spinner_minute = (Spinner) findViewById(R.id.spinner_minute);

        ArrayAdapter adapter_day = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days_big);
        ArrayAdapter adapter_hour = new ArrayAdapter(this, android.R.layout.simple_spinner_item, hours);
        ArrayAdapter adapter_minute = new ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes_5s);

        adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_hour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_minute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_day.setAdapter(adapter_day);
        spinner_hour.setAdapter(adapter_hour);
        spinner_minute.setAdapter(adapter_minute);

        //
        textDateCancel = (TextView) findViewById(R.id.text_date_cancel);
        textDateConfirm = (TextView) findViewById(R.id.text_date_confirm);

        textDateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("date", "cancel date！ ");

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        textDateConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("date", "hello,world ,date is date for testing ");

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
