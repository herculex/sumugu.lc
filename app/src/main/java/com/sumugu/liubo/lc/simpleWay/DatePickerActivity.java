package com.sumugu.liubo.lc.simpleWay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sumugu.liubo.lc.R;

import java.util.Calendar;

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

    Integer year_default = 2017;
//    Integer[] monthss = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12};


    TextView textDateConfirm, textDateCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

//        DatePickerActivity.this.setFinishOnTouchOutside(false);

        spinner_month = (Spinner) findViewById(R.id.spinner_month);
        spinner_day = (Spinner) findViewById(R.id.spinner_day);
        spinner_hour = (Spinner) findViewById(R.id.spinner_hour);
        spinner_minute = (Spinner) findViewById(R.id.spinner_minute);

        //
        textDateCancel = (TextView) findViewById(R.id.text_date_cancel);
        textDateConfirm = (TextView) findViewById(R.id.text_date_confirm);

        textDateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("setting", false);
                intent.putExtra("alarmclock", "cancel alarmclock！ ");

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        textDateConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("setting", true);
                intent.putExtra("alarmclock", getAlarmSetting());

                setResult(RESULT_OK, intent);
                finish();

            }
        });

        initialingPicker(Calendar.getInstance());

        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                initialingDayOfMonth(year_default, i + 1, spinner_day.getSelectedItemPosition() + 1);
//                Toast.makeText(DatePickerActivity.this, "select month ok.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

    }

    long getAlarmSetting() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year_default,
                spinner_month.getSelectedItemPosition(),
                spinner_day.getSelectedItemPosition() + 1,
                spinner_hour.getSelectedItemPosition(),
                Integer.parseInt(minutes_5s[spinner_minute.getSelectedItemPosition()]),
                0);

        return calendar.getTimeInMillis();
    }

    void initialingDayOfMonth(int year, int month, int day) {

        ArrayAdapter adapter_days;

        //初始化日期
        //根据month的单双数设置day
        String[] daysSource = days_big;
        switch (month) {
            case 2:
                boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                if (isLeap) {
                    daysSource = days_leap;
                } else {
                    daysSource = days_noleap;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysSource = days_small;
                break;

        }

        adapter_days = new ArrayAdapter(DatePickerActivity.this, android.R.layout.simple_spinner_item, daysSource);
        adapter_days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setAdapter(adapter_days);

        day = day - 1;
        if (day > daysSource.length - 1) {
            day = daysSource.length - 1;
        }

        spinner_day.setSelection(day);

    }

    void initialingPicker(Calendar calendar) {

        if (calendar.get(Calendar.MINUTE) >= 55) {
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.HOUR, 1);
        } else {
            calendar.add(Calendar.MINUTE, 5);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter adapter_month = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months);
        ArrayAdapter adapter_hour = new ArrayAdapter(this, android.R.layout.simple_spinner_item, hours);
        ArrayAdapter adapter_minute = new ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes_5s);

        adapter_hour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_minute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_month.setAdapter(adapter_month);
        spinner_hour.setAdapter(adapter_hour);
        spinner_minute.setAdapter(adapter_minute);

        //初始化Day of Month
        initialingDayOfMonth(year, month + 1, day);

        //
        spinner_month.setSelection(month);
        spinner_minute.setSelection(minute / 5);
        spinner_hour.setSelection(hour);


        TextView textViewYear = (TextView) findViewById(R.id.tv_year);
        textViewYear.setText(year + "年");
    }

}
