package com.sumugu.liubo.lc.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by liubo on 15/9/1.
 */
public class ItemContract extends DbContract {
    //Table specific constants
    //Table Name is "item"
    public static final String TABLE = "item";
    //default sort by item_created_at
    public static final String DEFAULT_SORT = Column.ITEM_CREATED_AT + " DESC";
    // provider specific constants
    // content://com.sumugu.liubo.lc.ItemProvider/item
    public static final String AUTHORITY = "com.sumugu.liubo.lc.provider.ItemProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE); //CONTENT_URI=content://com.sumugu.liubo.lc.ItemProvider/item
    public static final int ITEM_ITEM = 1;
    public static final int ITEM_DIR = 2;
    //单个条目
    public static final String ITEM_TYPE_ITEM = "vnd.android.cursor.item/vnd.com.sumugu.liubo.lc.provider.item";
    //所有条目的目录
    public static final String ITEM_TYPE_DIR = "vnd.android.cursor.dir/vnd.com.sumugu.liubo.lc.provider.item";

    //Columns specific constants
    public class Column {
        public static final String ITEM_ID = BaseColumns._ID;
        public static final String ITEM_TITLE = "item_title";
        public static final String ITEM_CONTENT = "item_content";
        public static final String ITEM_CREATED_AT = "item_created_at";
        public static final String ITEM_CREATED_AT_DAY = "item_created_at_day";
        public static final String ITEM_IS_FINISHED = "item_is_finished";
        public static final String ITEM_FINISHED_AT = "item_finished_at";
        public static final String ITEM_HAS_CLOCK = "item_has_clock";
        public static final String ITEM_ALARM_CLOCK = "item_alarm_clock";
        public static final String ITEM_LIST_ID = "item_list_id";

    }

}
