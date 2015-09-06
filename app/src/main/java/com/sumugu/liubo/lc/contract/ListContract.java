package com.sumugu.liubo.lc.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by liubo on 15/9/1.
 */
public class ListContract extends DbContract {

    //Table specific constants
    //Table Name is "list"
    public static final String TABLE="list";
    //default sort by list_created_at
    public static final String DEFAULT_SORT=Column.LIST_CREATED_AT+" DESC";

    //Columns specific constants
    public class Column{
        public static final String LIST_ID= BaseColumns._ID;
        public static final String LIST_TITLE="list_title";
        public static final String LIST_CONTENT="list_content";
        public static final String LIST_CREATED_AT="list_created_at";
        public static final String LIST_IS_FINISHED="list_is_finished";

    }

    // provider specific constants
    // content://com.sumugu.liubo.lc.ListProvider/list
    public static final String AUTHORITY="com.sumugu.liubo.lc.provider.ListProvider";
    public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TABLE); //CONTENT_URI=content://com.sumugu.liubo.lc.ListProvider/list
    public static final int LIST_ITEM=1;
    public static final int LIST_DIR=2;
    public static final String LIST_TYPE_ITEM="vnd.android.cursor.item/vnd.com.sumugu.liubo.lc.provider.list";
    public static final String LIST_TYPE_DIR="vnd.android.cursor.dir/vnd.com.sumugu.liubo.lc.provider.list";

}
