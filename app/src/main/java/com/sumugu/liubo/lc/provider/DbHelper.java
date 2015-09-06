package com.sumugu.liubo.lc.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sumugu.liubo.lc.contract.*;

/**
 * Created by liubo on 15/9/1.
 */
public class DbHelper extends SQLiteOpenHelper {
    //TAG
    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, DbContract.DB_NAME, null, DbContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_create_table_list = String.format("create table %s (" +
                "%s int primary key," +
                "%s text," +
                "%s text," +
                "%s int," +
                "%s int" +
                ")",
                ListContract.TABLE,
                ListContract.Column.LIST_ID,
                ListContract.Column.LIST_TITLE,
                ListContract.Column.LIST_CONTENT,
                ListContract.Column.LIST_CREATED_AT,
                ListContract.Column.LIST_IS_FINISHED
        );
        Log.d(TAG, "sumugu,onCreate with SQL:" + sql_create_table_list);
        db.execSQL(sql_create_table_list);

        String sql_create_table_item = String.format("create table %s (" +
                "%s int primary key," +
                "%s text," +
                "%s text," +
                "%s int," +
                "%s int," +
                "%s int," +
                "%s int," +
                "%s int",
                ItemContract.TABLE,
                ItemContract.Column.ITEM_ID,
                ItemContract.Column.ITEM_TITLE,
                ItemContract.Column.ITEM_CONTENT,
                ItemContract.Column.ITEM_CREATED_AT,
                ItemContract.Column.ITEM_IS_FINISHED,
                ItemContract.Column.ITEM_HAS_CLOCK,
                ItemContract.Column.ITEM_ALARM_CLOCK,
                ItemContract.Column.ITEM_LIST_ID
        );
        Log.d(TAG,"sumugu,onCreate with SQL:"+sql_create_table_item);
        db.execSQL(sql_create_table_item);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Hold on, do ALTER TABLE
        db.execSQL("drop table if exists "+DbContract.DB_NAME);
        onCreate(db);
    }
}
